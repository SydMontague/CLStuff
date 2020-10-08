package de.craftlancer.clstuff;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public class PreventCMDUpgrade implements Listener {
    private final CLStuff plugin;
    
    private Map<Material, List<Integer>> cmdUpgradableMap = new EnumMap<>(Material.class);
    
    public PreventCMDUpgrade(CLStuff plugin) {
        this.plugin = plugin;
        loadUpgradeableMap();
        
        plugin.getCommand("cmdupgrade").setExecutor((a, b, c, d) -> {
            if (a.isOp()) {
                loadUpgradeableMap();
                a.sendMessage("CustomModelData list reloaded.");
            }
            
            return true;
        });
    }
    
    private void loadUpgradeableMap() {
        cmdUpgradableMap.clear();
        File configFile = new File(plugin.getDataFolder(), "noNetheriteUpgrade.yml");
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        config.getKeys(false).forEach(a -> cmdUpgradableMap.put(Material.matchMaterial(a), config.getIntegerList(a)));
    }
    
    @EventHandler
    public void onSmith(PrepareSmithingEvent event) {
        ItemStack item = event.getInventory().getItem(0);
        
        if (item == null || !item.getItemMeta().hasCustomModelData())
            return;
        
        Material type = item.getType();
        int cmd = item.getItemMeta().getCustomModelData();
        
        if (cmdUpgradableMap.getOrDefault(type, Collections.emptyList()).contains(cmd))
            return;
        
        event.setResult(null);
    }
}
