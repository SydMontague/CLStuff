package de.craftlancer.clstuff.explosionregulator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;

/*
 * info <id>
 * create <id> <limit> <freeLimit> <materials...>
 * setlimit <id> <limit>
 * setfreelimit <id> <freeLimit>
 * add <id> <materials...>
 * remove <id> <materials...>
 * 
 */
public class ExplosionRegulator implements Listener {
    
    private final CLStuff plugin;
    private Map<String, ItemGroup> itemGroups = new HashMap<>();
    
    public ExplosionRegulator(CLStuff plugin) {
        this.plugin = plugin;
        
        Configuration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "explosionRegulator.yml"));
        itemGroups = config.getKeys(false).stream().collect(Collectors.toMap(a -> a, a -> (ItemGroup) config.get(a)));
        
        plugin.getCommand("explonerf").setExecutor(new ExplosionRegulatorCommandHandler(plugin, this));
        new LambdaRunnable(() -> itemGroups.forEach((a, b) -> b.tick())).runTaskTimer(plugin, 1200L, 1200L);
    }
    
    public void save() {
        File configFile = new File(plugin.getDataFolder(), "explosionRegulator.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("itemGroups", itemGroups);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(configFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Item Groups: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        float yield = itemGroups.values().stream().filter(a -> a.addCount(event.blockList())).map(ItemGroup::getCurrentYield).min(Float::compare).orElse(1.0f);
        event.setYield(yield);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        float yield = itemGroups.values().stream().filter(a -> a.addCount(event.blockList())).map(ItemGroup::getCurrentYield).min(Float::compare).orElse(1.0f);
        event.setYield(yield);
    }
    
    public boolean hasItemGroup(String id) {
        return itemGroups.containsKey(id);
    }
    
    public void addItemGroup(String id, ItemGroup itemGroup) {
        itemGroups.put(id, itemGroup);
    }
    
    public boolean removeItemGroup(String id) {
        return itemGroups.remove(id) != null;
    }
    
    public ItemGroup getItemGroup(String id) {
        return itemGroups.get(id);
    }

    public Collection<String> getIds() {
        return itemGroups.keySet();
    }
}
