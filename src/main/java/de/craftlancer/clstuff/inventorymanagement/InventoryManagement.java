package de.craftlancer.clstuff.inventorymanagement;

import de.craftlancer.clstuff.CLStuff;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
import java.util.stream.Collectors;

public class InventoryManagement implements Listener {
    
    private static final long ONE_WEEK = 604800000;
    
    private Map<UUID, LastInventory> lastInventories = new HashMap<>();
    private CLStuff plugin;
    private File directory;
    private File lastInventoryFile;
    
    public InventoryManagement(CLStuff plugin) {
        
        ConfigurationSerialization.registerClass(LastInventory.class);
        
        this.plugin = plugin;
        this.directory = new File(plugin.getDataFolder(), "inventoryManagement");
        directory.mkdirs();
        this.lastInventoryFile = new File(directory, "lastInventories.yml");
        
        load();
    }
    
    private void load() {
        if (!lastInventoryFile.exists())
            plugin.saveResource(lastInventoryFile.getName(), false);
        
        YamlConfiguration lastInventoryConfig = YamlConfiguration.loadConfiguration(lastInventoryFile);
        
        ((List<LastInventory>) lastInventoryConfig.getList("inventories", new ArrayList<>())).forEach(l -> lastInventories.put(l.getOwner(), l));
    }
    
    public void save() {
        if (!lastInventoryFile.exists())
            plugin.saveResource(lastInventoryFile.getName(), false);
        
        YamlConfiguration lastInventoryConfig = YamlConfiguration.loadConfiguration(lastInventoryFile);
        
        lastInventoryConfig.set("inventories", lastInventories.values().stream()
                .filter(lastInventory -> lastInventory.getTimeCreated() + ONE_WEEK <= System.currentTimeMillis()).collect(Collectors.toList()));
        
        try {
            lastInventoryConfig.save(lastInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        lastInventories.put(event.getEntity().getUniqueId(), new LastInventory(event.getEntity().getInventory(), event.getEntity().getUniqueId()));
    }
    
    public LastInventory getLastInventory(UUID owner) {
        return lastInventories.get(owner);
    }
    
    public String getPrefix() {
        return "§8[§bInventoryManagement§8]§7 ";
    }
}
