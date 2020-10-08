package de.craftlancer.clstuff.arena;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.CLStuff;

/*
 * Each Arena GUI has a list of bosses that may only exist once
 * Config via file -> needs reload command
 * Support for multiple spawns at once!
 * 
 * Name
 * Custom Head
 * Beschreibung
 * Mob(s)
 * 
 * 
 */
/*
 * coords as filename
 * 
 * location:
 *   world: "world"
 *   x: 0
 *   y: 0
 *   z: 0 
 * spawnlocation:
 *   world: "world"
 *   x: 0
 *   y: 0
 *   z: 0
 * name: "name"
 * mobs:
 *   - name: "mob name"
 *     description: 
 *     - "line 1"
 *     - "line 2"
 *     head: <item registry key>
 *     cost:
 *     - type: <registry key> | "money"
 *       amount: 1
 *     spawns:
 *     - mythicMob1
 *     - mythicMob2
 * 
 * 
 * 
 */
public class ArenaGUI implements Listener {
    
    private Map<Location, ArenaEntry> entry = new HashMap<>();
    private Plugin plugin;
    
    private static final String toFilename(Location loc) {
        return String.format("%s,%d,%d,%d.yml", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    @Nonnull
    private static Location getSimpleLocation(@Nullable ConfigurationSection section) {
        if(section == null)
            return new Location(Bukkit.getWorlds().get(0), 0, 128, 0);
        
        return new Location(Bukkit.getWorld(section.getString("world")), section.getInt("x"), section.getInt("y"), section.getInt("z"));
    }
    
    private static void setSimpleLocation(@Nonnull ConfigurationSection section, @Nonnull Location loc) {
        section.set("world", loc.getWorld().getName());
        section.set("x", loc.getBlockX());
        section.set("y", loc.getBlockY());
        section.set("z", loc.getBlockZ());
    }
    
    public ArenaGUI(CLStuff plugin) {
        this.plugin = plugin;
        
        loadConfig();
        plugin.getCommand("arenagui").setExecutor(new ArenaCommand(plugin, this));
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @SuppressWarnings("unchecked")
    protected void loadConfig() {
        this.entry = new HashMap<>();
        
        File arenasDir = new File(plugin.getDataFolder(), "arenas");
        arenasDir.mkdirs();
        
        for (File file : arenasDir.listFiles((a, b) -> b.endsWith(".yml"))) {
            Configuration config = YamlConfiguration.loadConfiguration(file);
            
            Location loc = getSimpleLocation(config.getConfigurationSection("location"));
            Location spawnLoc = getSimpleLocation(config.getConfigurationSection("spawnLocation"));
            Location playerLoc = getSimpleLocation(config.getConfigurationSection("playerLoc"));
            long teleportDelay = config.getLong("teleportDelay", 0L);
            
            String name = config.getString("name", "");
            
            List<ArenaMob> mob = config.getMapList("mobs").stream().map(a -> {
                String mobName = (String) a.get("name");
                boolean teleportPlayer = Optional.ofNullable((Boolean) a.get("teleportPlayer")).orElse(false);
                List<String> lore = (List<String>) a.get("lore");
                String head = (String) a.get("head");
                List<String> spawns = (List<String>) a.get("spawns");
                List<ArenaCost> cost = ((List<Map<?, ?>>) a.get("cost")).stream().map(b -> new ArenaCost((String) b.get("type"), (Integer) b.get("amount")))
                                                                        .collect(Collectors.toList());
                
                return new ArenaMob(mobName, lore, head, spawns, cost, teleportPlayer);
            }).collect(Collectors.toList());
            
            entry.put(loc, new ArenaEntry(plugin, loc, spawnLoc, playerLoc, name, teleportDelay, mob));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;
        
        if (entry.containsKey(event.getClickedBlock().getLocation())) {
            event.getPlayer().openInventory(entry.get(event.getClickedBlock().getLocation()).getGUI().getInventory());
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteractCreate(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;
        
        if (!event.getPlayer().hasMetadata("arena.create"))
            return;
        
        event.getPlayer().removeMetadata("arena.create", plugin);
        File file = new File(plugin.getDataFolder(), "arenas" + File.separator + toFilename(event.getClickedBlock().getLocation()));
        FileConfiguration config = new YamlConfiguration();
        
        setSimpleLocation(config.createSection("location"), event.getClickedBlock().getLocation());
        setSimpleLocation(config.createSection("spawnLocation"), event.getClickedBlock().getLocation());
        config.set("name", "");
        config.set("mobs", Collections.emptyList());
        
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
