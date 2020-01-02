package de.craftlancer.clstuff.squest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.clstuff.CLStuff;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;

// squest reward add
// squest reward list
// squest reward remove

// /squest reward add <quest> <type> <distribution key> <extra data>
// <extra data>:
// item -> <amount>
// command -> <commandString>
// potion -> <type> <level> <time>
// broadcast -> <message> <discord>

// <distribution key>
// EVERYONE -> everyone
// MOST_DONATED -> most:X (rank)
// DONATION_ABOVE -> above:X (%)
// DONATION_SHARE -> share
// EVERY_DONATOR -> alldonated

public class Quest implements Listener {
    private final UUID uuid;
    private final String name;
    private final Location chestLocation;
    
    private String description = "";
    private QuestState state = QuestState.INACTIVE;
    private int requiredPoints = -1;
    
    private List<QuestRequirement> requirements = new ArrayList<>();
    private List<QuestReward> rewards = new ArrayList<>();
    
    private Set<UUID> rewardPlayers = new HashSet<>();
    
    public Quest(CLStuff plugin, String name, Location chestLocation) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.chestLocation = chestLocation;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @SuppressWarnings("unchecked")
    public Quest(CLStuff plugin, ConfigurationSection config) {
        this.uuid = UUID.fromString(config.getName());
        this.name = config.getString("name");
        this.chestLocation = config.getLocation("chestLocation");
        
        this.requirements = (List<QuestRequirement>) config.getList("requirements", new ArrayList<>());
        
        this.description = config.getString("description");
        this.state = QuestState.valueOf(config.getString("state", QuestState.INACTIVE.name()));
        this.rewards = (List<QuestReward>) config.getList("rewards");
        this.rewardPlayers = config.getStringList("unrewardedPlayers").stream().map(UUID::fromString).collect(Collectors.toSet());
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void save(ConfigurationSection config) {
        ConfigurationSection configSection = config.createSection(uuid.toString());
        
        configSection.set("name", name);
        configSection.set("chestLocation", chestLocation);
        
        configSection.set("requirements", requirements);
        configSection.set("unrewardedPlayers", new ArrayList<>(rewardPlayers));
        
        configSection.set("description", description);
        configSection.set("state", state.name());
        configSection.set("rewards", rewards);
    }
    
    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (state != QuestState.ACTIVE)
            return;
        
        if (!event.hasBlock() || !event.getClickedBlock().getLocation().equals(chestLocation))
            return;
        
        event.setCancelled(true);
        Player p = event.getPlayer();
        
        if (!event.hasItem() || p.getGameMode() != GameMode.SURVIVAL || p.hasPermission("clstuff.squest.exempt"))
            return;
        
        ItemStack item = event.getItem();
        
        if (requirements.stream().anyMatch(a -> a.isRequiredItem(item))) {
            Bukkit.broadcastMessage(ChatColor.GRAY + p.getDisplayName() + " delivered " + ChatColor.WHITE + item.getAmount() + " " + item.getType().name()
                    + " to " + ChatColor.GREEN + getName());
            
            if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null)
                DiscordUtil.queueMessage(DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("event"),
                                         ChatColor.stripColor(p.getDisplayName()) + " delivered " + item.getAmount() + " " + item.getType().name() + " to " + getName());
            
            requirements.stream().filter(a -> a.isRequiredItem(item)).forEach(a -> a.contribute(p, item));
        }
        
        checkFinished();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getLocation().equals(chestLocation))
            event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!rewardPlayers.contains(p.getUniqueId()))
            return;
        
        rewards.forEach(a -> a.rewardPlayer(this, p));
        rewardPlayers.remove(p.getUniqueId());
    }
    
    public void startQuest() {
        if (state != QuestState.INACTIVE)
            return;
        
        state = QuestState.ACTIVE;
    }
    
    public void checkFinished() {
        if (state != QuestState.ACTIVE)
            return;
        
        // if all requirements are met or enough points are collected if applicable
        if (!(requirements.stream().allMatch(QuestRequirement::isFinished) || (requiredPoints > 0 && requiredPoints <= getCurrentPoints())))
            return;
        
        rewards.forEach(a -> a.questCompleted(this));
        rewardPlayers = requirements.stream().map(a -> a.getContribution().keySet()).flatMap(Set::stream).collect(Collectors.toCollection(HashSet::new));
        
        Bukkit.getOnlinePlayers().stream().filter(a -> rewardPlayers.contains(a.getUniqueId())).forEach(p -> {
            rewards.forEach(a -> a.rewardPlayer(this, p));
            rewardPlayers.remove(p.getUniqueId());
        });
        state = QuestState.COMPLETED;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Location getChestLocation() {
        return chestLocation;
    }
    
    public QuestState getState() {
        return state;
    }
    
    public List<QuestRequirement> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }
    
    public boolean addItem(ItemStack item, int weight) {
        return requirements.add(new QuestRequirement(item, weight));
    }
    
    public boolean removeItem(int index) {
        if (index >= requirements.size())
            return false;
        
        requirements.remove(index);
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    public int getRequiredPoints() {
        return requiredPoints;
    }
    
    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }
    
    public void addReward(QuestReward reward) {
        rewards.add(reward);
    }
    
    public boolean removeReward(int index) {
        if (index >= rewards.size())
            return false;
        
        rewards.remove(index);
        return true;
    }
    
    public List<QuestReward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }
    
    public int getCurrentPoints() {
        return requirements.stream().collect(Collectors.summingInt(a -> a.getWeight() * a.getCurrentAmount()));
    }
    
}
