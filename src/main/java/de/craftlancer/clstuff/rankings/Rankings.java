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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import de.craftlancer.clclans.ClanUtils;
import de.craftlancer.clfeatures.CLFeatures;
import de.craftlancer.clfeatures.trophychest.TrophyChestFeature;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.LastSeenCache;
import de.craftlancer.core.NMSUtils;
import de.craftlancer.core.Utils;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Rankings implements CommandExecutor {
    private final Plugin plugin;
    private final File rankingsFile;
    private final LastSeenCache lastSeenCache;
    
    private boolean isUpdating = false;
    private long lastUpdate = -1;
    private Map<UUID, Integer> scoreMap = new HashMap<>();
    
    public Rankings(Plugin plugin) {
        this.plugin = plugin;
        this.lastSeenCache = CLCore.getInstance().getLastSeenCache();
        this.rankingsFile = new File(plugin.getDataFolder(), "rankings.yml");
        
        if(rankingsFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(rankingsFile);
            
            lastUpdate = config.getLong("lastUpdate", -1);
            scoreMap = config.getConfigurationSection("scoreMap").getValues(false).entrySet().stream()
                             .collect(Collectors.toMap(a -> UUID.fromString(a.getKey()), b -> (Integer) b.getValue()));
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
                sender.sendMessage(ClanUtils.TEXT_COLOR_UNIMPORTANT + String.format("Craftlancer Player Ranking | Page %d/%d", finalPage + 1, numPages));
                sender.sendMessage(ClanUtils.TEXT_COLOR_UNIMPORTANT + ClanUtils.INDENTATION + "Rank - Tag - Name - Score");
                
                List<Map.Entry<UUID, Integer>> entries = Utils.paginate(updateScores().entrySet().stream()
                                                                                     .sorted(Comparator.comparingInt(Map.Entry<UUID, Integer>::getValue)
                                                                                                       .reversed()),
                                                                       finalPage);
                
                int i = 1 + Utils.ELEMENTS_PER_PAGE * finalPage;
                for (Map.Entry<UUID, Integer> entry : entries) {
                    BaseComponent base = new TextComponent(ClanUtils.INDENTATION);
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
        config.set("lastUpdate", lastUpdate);
        config.set("scoreMap", scoreMap.entrySet().stream().collect(Collectors.toMap(a -> a.getKey().toString(), Map.Entry::getValue)));
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(rankingsFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Rankings: ", e);
            }
        });

        if (NMSUtils.isRunning())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    public Map<UUID, Integer> updateScores() {
        // don't update when an update is less than a minute old
        if(isUpdating || lastUpdate + 60000 > System.currentTimeMillis())
            return scoreMap;
        
        isUpdating = true;
        
        Set<OfflinePlayer> bannedPlayer = Bukkit.getBannedPlayers();
        
        for (OfflinePlayer p : plugin.getServer().getOfflinePlayers())
            scoreMap.put(p.getUniqueId(), bannedPlayer.contains(p) ? 0 : calculateScore(p));

        lastUpdate = System.currentTimeMillis();
        isUpdating = false;
        return scoreMap;
    }
    
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    public int getScore(OfflinePlayer player) {
        if (player.isBanned())
            return 0;
        
        return scoreMap.getOrDefault(player.getUniqueId(), 0);
    }
    
    private int calculateScore(OfflinePlayer player) {
        if (!player.hasPlayedBefore())
            return 0;

        long lastSeen = lastSeenCache.getLastSeen(player);
        
        if (lastSeen < lastUpdate)
            return scoreMap.getOrDefault(player.getUniqueId(), 0);
        
        if(player.isBanned() || player.isOp())
            return 0;
        
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        
        int unspent = data.getRemainingClaimBlocks();
        int spent = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks() - unspent;
        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
        double balance = CLCore.getInstance().getEconomy().getBalance(player);

        long daysInactive = (System.currentTimeMillis() - lastSeen) / ClanUtils.MS_PER_DAY;
        double totalPoints = unspent + playtime + balance;
        totalPoints *= Math.max(0.1D, Math.min(1.0D, 1.03D - daysInactive / 100D));
        totalPoints += spent * 1.25D;
        totalPoints += ((TrophyChestFeature) CLFeatures.getInstance().getFeature("trophyChest")).getScore(player);

        return (int) totalPoints;
    }
}
