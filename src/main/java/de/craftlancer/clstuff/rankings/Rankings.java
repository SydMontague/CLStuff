package de.craftlancer.clstuff.rankings;

import de.craftlancer.clfeatures.CLFeatures;
import de.craftlancer.clfeatures.trophydepositor.TrophyDepositorFeature;
import de.craftlancer.clstuff.rewards.Reward;
import de.craftlancer.clstuff.rewards.RewardRegisterable;
import de.craftlancer.clstuff.rewards.RewardsManager;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.LastSeenCache;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageRegisterable;
import de.craftlancer.core.util.MessageUtil;
import de.craftlancer.core.util.Tuple;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Rankings implements CommandExecutor, MessageRegisterable, RewardRegisterable {
    private final Plugin plugin;
    private final File rankingsFile;
    
    final LastSeenCache lastSeenCache;
    final TrophyDepositorFeature trophyDepositor;
    
    private boolean isUpdating = false;
    private Map<UUID, RankingsEntry> scoreMap = new HashMap<>();
    private Map<Integer, String> rewardMap = new HashMap<>();
    
    public Rankings(Plugin plugin) {
        RewardsManager.getInstance().register(this);
        this.plugin = plugin;
        this.lastSeenCache = CLCore.getInstance().getLastSeenCache();
        this.rankingsFile = new File(plugin.getDataFolder(), "rankings.yml");
        this.trophyDepositor = ((TrophyDepositorFeature) CLFeatures.getInstance().getFeature("trophyDepositor"));
        
        if (rankingsFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(rankingsFile);
            scoreMap = config.getMapList("scoreMap").stream().map(RankingsEntry::new).collect(Collectors.toMap(RankingsEntry::getUUID, a -> a));
            
            ConfigurationSection rewardsSection = config.getConfigurationSection("rewards");
            if (rewardsSection != null)
                for (String key : rewardsSection.getKeys(false))
                    rewardMap.put(Integer.parseInt(key), rewardsSection.getString(key));
            
        }
        
        BaseComponent component = new TextComponent("§8[§bScores§8]");
        MessageUtil.register(this, component, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_AQUA, ChatColor.GREEN);
        
        new LambdaRunnable(this::checkRankingRewards).runTaskTimer(plugin, 10, 20);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int finalPage = args.length >= 1 ? Utils.parseIntegerOrDefault(args[0], 1) - 1 : 0;
        int numPages = (plugin.getServer().getOfflinePlayers().length + Utils.ELEMENTS_PER_PAGE - 1) / Utils.ELEMENTS_PER_PAGE;
        if (finalPage < 0 || finalPage >= numPages) sender.sendMessage("That page doesn't exist.");
        else {
            new LambdaRunnable(() -> {
                sender.sendMessage(Utils.TEXT_COLOR_UNIMPORTANT + String.format("Craftlancer Player Ranking | Page " + "%d/%d", finalPage + 1, numPages));
                sender.sendMessage(Utils.TEXT_COLOR_UNIMPORTANT + Utils.INDENTATION + "Rank - Tag - Name - Score");
                
                List<Tuple<UUID, Double>> entries = Utils.paginate(updateScores().entrySet().stream().map(a -> new Tuple<>(a.getKey(), a.getValue().getScore())).sorted(Comparator.comparingDouble(Tuple<UUID, Double>::getValue).reversed()), finalPage);
                
                int i = 1 + Utils.ELEMENTS_PER_PAGE * finalPage;
                for (Tuple<UUID, Double> entry : entries) {
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
        ConfigurationSection rewardsSection = config.createSection("rewards");
        for (Map.Entry<Integer, String> entry : rewardMap.entrySet())
            rewardsSection.set(String.valueOf(entry.getKey()), entry.getValue());
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(rankingsFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Rankings: ", e);
            }
        });
        
        if (plugin.isEnabled()) saveTask.runTaskAsynchronously(plugin);
        else saveTask.run();
    }
    
    public Map<UUID, RankingsEntry> updateScores() {
        if (isUpdating) return scoreMap;
        
        isUpdating = true;
        Set<OfflinePlayer> bannedPlayer = Bukkit.getBannedPlayers();
        
        for (OfflinePlayer p : plugin.getServer().getOfflinePlayers())
            //updatePlayer(p, bannedPlayer.contains(p) || p.isOp());
            updatePlayer(p, bannedPlayer.contains(p));
        
        checkRankingRewards();
        
        isUpdating = false;
        return scoreMap;
    }
    
    public void checkRankingRewards() {
        for (Player p : Bukkit.getOnlinePlayers())
            for (Map.Entry<Integer, String> entry : rewardMap.entrySet())
                if (getScore(p.getUniqueId()) >= entry.getKey()) {
                    Optional<Reward> optional = RewardsManager.getInstance().getReward(entry.getValue());
                    if (optional.isPresent() && !optional.get().hasReceived(p.getUniqueId()))
                        optional.get().reward(p, true);
                }
    }
    
    public RankingsEntry getRankingsEntry(OfflinePlayer player) {
        return scoreMap.get(player.getUniqueId());
    }
    
    public RankingsEntry getRankingsEntry(UUID uuid) {
        return scoreMap.get(uuid);
    }
    
    public double getScore(UUID uuid) {
        return scoreMap.containsKey(uuid) ? scoreMap.get(uuid).getScore() : 0;
    }
    
    public double getScore(OfflinePlayer player) {
        return getScore(player.getUniqueId());
    }
    
    private void updatePlayer(OfflinePlayer player, boolean isBanned) {
        if (!player.isOnline() && !player.hasPlayedBefore()) return;
        
        long lastSeen = lastSeenCache.getLastSeen(player);
        RankingsEntry entry = scoreMap.computeIfAbsent(player.getUniqueId(), a -> new RankingsEntry(player.getUniqueId()));
        entry.isBanned = isBanned;
        
        if (!player.isOnline() && lastSeen < entry.lastUpdate) return;
        
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        
        entry.unspent = data.getRemainingClaimBlocks();
        entry.spent = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks() - entry.unspent;
        entry.playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
        entry.balance = CLCore.getInstance().getEconomy().getBalance(player);
        entry.lastUpdate = System.currentTimeMillis();
    }
    
    @Override
    public String getMessageID() {
        return "Scores";
    }
    
    void addReward(int score, Reward reward) {
        rewardMap.put(score, reward.getKey());
    }
    
    boolean removeReward(int score) {
        return rewardMap.remove(score) != null;
    }
    
    Map<Integer, String> getRewardMap() {
        return rewardMap;
    }
    
    void clearRewards() {
        rewardMap.clear();
    }
    
    @Override
    public String getKey() {
        return "Scoring";
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
        
        public double getScore() {
            if (isBanned) return 0;
            
            return trophyDepositor.getScore(uuid);
        }
        
        public double getBalance() {
            return balance;
        }
        
        public int getPlaytime() {
            return playtime;
        }
        
        public int getSpent() {
            return spent;
        }
        
        public int getUnspent() {
            return unspent;
        }
        
        public long getLastUpdate() {
            return lastUpdate;
        }
    }
}
