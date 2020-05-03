package de.craftlancer.clstuff.rankings;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import de.craftlancer.clfeatures.CLFeatures;
import de.craftlancer.clfeatures.trophychest.TrophyChestFeature;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.LastSeenCache;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.Tuple;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Rankings implements CommandExecutor {
    private final Plugin plugin;
    private final File rankingsFile;
    
    final LastSeenCache lastSeenCache;
    final TrophyChestFeature trophyChest;
    
    private boolean isUpdating = false;
    private Map<UUID, RankingsEntry> scoreMap = new HashMap<>();
    
    public Rankings(Plugin plugin) {
        this.plugin = plugin;
        this.lastSeenCache = CLCore.getInstance().getLastSeenCache();
        this.rankingsFile = new File(plugin.getDataFolder(), "rankings.yml");
        
        this.trophyChest = ((TrophyChestFeature) CLFeatures.getInstance().getFeature("trophyChest"));
        
        if (rankingsFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(rankingsFile);
            scoreMap = config.getMapList("scoreMap").stream().map(RankingsEntry::new).collect(Collectors.toMap(RankingsEntry::getUUID, a -> a));
        }
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int finalPage = args.length >= 1 ? Utils.parseIntegerOrDefault(args[0], 1) - 1 : 0;
        int numPages = plugin.getServer().getOfflinePlayers().length / Utils.ELEMENTS_PER_PAGE;
        if (finalPage < 0 || finalPage >= numPages)
            sender.sendMessage("That page doesn't exist.");
        else {
            new LambdaRunnable(() -> {
                sender.sendMessage(Utils.TEXT_COLOR_UNIMPORTANT + String.format("Craftlancer Player Ranking | Page %d/%d", finalPage + 1, numPages));
                sender.sendMessage(Utils.TEXT_COLOR_UNIMPORTANT + Utils.INDENTATION + "Rank - Tag - Name - Score");
                
                List<Tuple<UUID, Integer>> entries = Utils.paginate(updateScores().entrySet().stream()
                                                                                  .map(a -> new Tuple<UUID, Integer>(a.getKey(), a.getValue().getScore()))
                                                                                  .sorted(Comparator.comparingInt(Tuple<UUID, Integer>::getValue).reversed()),
                                                                    finalPage);
                
                int i = 1 + Utils.ELEMENTS_PER_PAGE * finalPage;
                for (Tuple<UUID, Integer> entry : entries) {
                    BaseComponent base = new TextComponent(Utils.INDENTATION);
                    base.addExtra(String.format("#%d", i++));
                    base.addExtra(" - ");
                    base.addExtra(Bukkit.getOfflinePlayer(entry.getKey()).getName());
                    base.addExtra(" - ");
                    base.addExtra(Integer.toString(entry.getValue().intValue()));
                    sender.spigot().sendMessage(base);
                }
            }).runTaskAsynchronously(plugin);
        }
        return true;
    }
    
    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("scoreMap", scoreMap.values().stream().map(RankingsEntry::serialize).collect(Collectors.toList()));
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(rankingsFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Rankings: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    public Map<UUID, RankingsEntry> updateScores() {
        // don't update when an update is ongoing
        if (isUpdating)
            return scoreMap;
        
        isUpdating = true;
        Set<OfflinePlayer> bannedPlayer = Bukkit.getBannedPlayers();
        
        for (OfflinePlayer p : plugin.getServer().getOfflinePlayers())
            updatePlayer(p, bannedPlayer.contains(p) || p.isOp());
        
        isUpdating = false;
        return scoreMap;
    }
    
    public int getScore(UUID uuid) {
        return scoreMap.containsKey(uuid) ? scoreMap.get(uuid).getScore() : 0;
    }
    
    public int getScore(OfflinePlayer player) {
        return getScore(player.getUniqueId());
    }
    
    private void updatePlayer(OfflinePlayer player, boolean isBanned) {
        if (!player.hasPlayedBefore())
            return;
        
        long lastSeen = lastSeenCache.getLastSeen(player);
        RankingsEntry entry = scoreMap.computeIfAbsent(player.getUniqueId(), a -> new RankingsEntry(player.getUniqueId()));
        entry.isBanned = isBanned;
        
        if (!player.isOnline() && lastSeen < entry.lastUpdate)
            return;
        
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        
        entry.unspent = data.getRemainingClaimBlocks();
        entry.spent = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks() - entry.unspent;
        entry.playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
        entry.balance = CLCore.getInstance().getEconomy().getBalance(player);
        entry.lastUpdate = System.currentTimeMillis();
    }
    
    public class RankingsEntry implements ConfigurationSerializable {
        private final UUID uuid;
        
        long lastUpdate = -1;
        
        int unspent = 0;
        int spent = 0;
        int playtime = 0;
        double balance = 0;
        boolean isBanned = false;
        
        public RankingsEntry(UUID uuid) {
            this.uuid = uuid;
        }
        
        public RankingsEntry(Map<?, ?> map) {
            this.uuid = UUID.fromString(map.get("uuid").toString());
            this.unspent = ((Number) map.get("unspent")).intValue();
            this.spent = ((Number) map.get("spent")).intValue();
            this.balance = ((Number) map.get("balance")).doubleValue();
            this.playtime = ((Number) map.get("playtime")).intValue();
            this.isBanned = (Boolean) map.get("isBanned");
            this.lastUpdate = ((Number) map.get("lastUpdate")).longValue();
        }
        
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            
            map.put("uuid", uuid.toString());
            map.put("unspent", unspent);
            map.put("spent", spent);
            map.put("balance", balance);
            map.put("playtime", playtime);
            map.put("isBanned", isBanned);
            map.put("lastUpdate", lastUpdate);
            
            return map;
        }
        
        public UUID getUUID() {
            return uuid;
        }
        
        public int getScore() {
            if (isBanned)
                return 0;
            
            long lastSeen = lastSeenCache.getLastSeen(uuid);
            
            long daysInactive = (System.currentTimeMillis() - lastSeen) / Utils.MS_PER_DAY;
            double totalPoints = unspent + playtime + balance;
            totalPoints *= Math.max(0.1D, Math.min(1.0D, 1.03D - daysInactive / 100D));
            totalPoints += spent * 1.25D;
            totalPoints += trophyChest.getScore(uuid);
            
            return (int) totalPoints / 100;
        }
    }
}
