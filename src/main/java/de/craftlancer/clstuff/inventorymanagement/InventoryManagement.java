package de.craftlancer.clstuff.inventorymanagement;

import de.craftlancer.clstuff.CLStuff;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManagement implements Listener {
    
    private static final long ONE_WEEK = 604800000;
    
    private Map<UUID, List<LastInventory>> lastInventories = new HashMap<>();
    private CLStuff plugin;
    private File lastInventoryFile;
    
    public InventoryManagement(CLStuff plugin) {
        
        ConfigurationSerialization.registerClass(LastInventory.class);
        
        this.plugin = plugin;
        File directory = new File(plugin.getDataFolder(), "inventoryManagement");
        directory.mkdirs();
        this.lastInventoryFile = new File(directory, "inventoryManagementData.yml");
        
        load();
    }
    
    private void load() {
        if (!lastInventoryFile.exists())
            plugin.saveResource(lastInventoryFile.getName(), false);
        
        YamlConfiguration lastInventoryConfig = YamlConfiguration.loadConfiguration(lastInventoryFile);
        
        ConfigurationSection section = lastInventoryConfig.getConfigurationSection("lastInventories");
        
        if (section == null)
            return;
        
        section.getKeys(false).forEach(key -> lastInventories.put(UUID.fromString(key), (List<LastInventory>) section.getList(key)));
    }
    
    public void save() {
        if (!lastInventoryFile.exists())
            plugin.saveResource(lastInventoryFile.getName(), false);
        
        YamlConfiguration lastInventoryConfig = YamlConfiguration.loadConfiguration(lastInventoryFile);
        
        ConfigurationSection section = lastInventoryConfig.createSection("lastInventories");
        
        lastInventories.forEach((uuid, list) -> {
            list.removeIf(l -> l.getTimeCreated() + ONE_WEEK <= System.currentTimeMillis());
            section.set(uuid.toString(), list);
        });
        
        try {
            lastInventoryConfig.save(lastInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<LastInventory> list = lastInventories.getOrDefault(player.getUniqueId(), new ArrayList<>());
        list.add(new LastInventory(player.getInventory(), player));
        lastInventories.put(player.getUniqueId(), list);
    }
    
    public List<LastInventory> getLastInventories(UUID owner) {
        return lastInventories.get(owner);
    }
    
    public String getPrefix() {
        return "§8[§bInventoryManagement§8]§7 ";
    }
}
