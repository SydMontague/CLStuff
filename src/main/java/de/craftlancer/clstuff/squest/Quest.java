package de.craftlancer.clstuff.squest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.clstuff.CLStuff;

public class Quest implements Listener {
    private final UUID uuid;
    private final String name;
    private final Location chestLocation;
    private List<ItemStack> remaining = new ArrayList<>();
    private String description = "";
    private QuestReward reward = new EmptyReward();
    private QuestState state = QuestState.INACTIVE;
    
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
        
        this.remaining = (List<ItemStack>) config.getList("items", new ArrayList<>());
        this.description = config.getString("description");
        this.state = QuestState.valueOf(config.getString("state", QuestState.INACTIVE.name()));
        this.reward = (QuestReward) config.get("reward");
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void save(ConfigurationSection config) {
        ConfigurationSection configSection = config.createSection(uuid.toString());
        
        configSection.set("name", name);
        configSection.set("chestLocation", chestLocation);
        
        configSection.set("items", remaining);
        configSection.set("description", description);
        configSection.set("state", state.name());
        configSection.set("reward", reward);
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
        
        remaining.stream().filter(a -> a.isSimilar(item) || a.getAmount() != 0).forEach(a -> {
            int givenAmount = item.getAmount();
            int remainAmount = a.getAmount();
            
            a.setAmount(Math.max(remainAmount - givenAmount, 0));
            item.setAmount(Math.max(givenAmount - remainAmount, 0));
        });
        
        checkFinished();
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getLocation().equals(chestLocation) && !event.getPlayer().hasPermission("clstuff.squest.admin"))
            event.setCancelled(true);
    }
    
    public void startQuest() {
        if(state != QuestState.INACTIVE)
            return;
        
        state = QuestState.ACTIVE;
    }
    
    public void checkFinished() {
        if(state != QuestState.ACTIVE)
            return;

        if(!remaining.stream().allMatch(a -> a.getAmount() == 0))
            return;
        
        reward.questCompleted();
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
    
    public QuestReward getReward() {
        return reward;
    }
    
    public void setReward(QuestReward reward) {
        this.reward = reward;
    }
    
    public List<ItemStack> getRemaining() {
        return Collections.unmodifiableList(remaining);
    }
    
    public boolean addItem(ItemStack item) {
        return remaining.add(item);
    }
    
    public boolean removeItem(int index) {
        if(index >= remaining.size())
            return false;
        
        remaining.remove(index);
        return true;
    }
}