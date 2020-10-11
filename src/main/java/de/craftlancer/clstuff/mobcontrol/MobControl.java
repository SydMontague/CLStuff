package de.craftlancer.clstuff.mobcontrol;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

/*
 * Per-World settings
 * Reload command
 * Chance of hostile spawns in range of noob player not spawning, based on rank
 * Set spawn limits and spawn ticks per world via command
 * 
 */
public class MobControl implements Listener, CommandExecutor {
    private final CLStuff plugin;
    private final Random rng = new Random();
    private Map<String, WorldSettings> worldSettings = new HashMap<>();
    
    public MobControl(CLStuff plugin) {
        this.plugin = plugin;
        loadConfiguration();
        
        plugin.getCommand("mobControl").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("clstuff.mobcontrol"))
            return false;
        
        loadConfiguration();
        
        MessageUtil.sendMessage(plugin, sender, MessageLevel.NORMAL, "MobControl reloaded.");
        return true;
    }
    
    public void loadConfiguration() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "mobControl.yml"));
        ConfigurationSection worlds = config.getConfigurationSection("worlds");
        
        if (worlds != null)
            worldSettings = worlds.getKeys(false).stream().collect(Collectors.toMap(a -> a, a -> new WorldSettings(worlds.getConfigurationSection(a))));
        
        applyLimits();
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Location loc = event.getLocation();
        WorldSettings settings = worldSettings.get(loc.getWorld().getName());
        
        if (settings == null)
            return;
        
        switch (event.getSpawnReason()) {
            case NATURAL:
            case REINFORCEMENTS:
                break;
            default:
                return;
        }
        
        int minChance = 100;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Optional<GroupSetting> setting = settings.getGroupSetting(CLCore.getInstance().getPermissions().getPrimaryGroup(p));
            if (setting.isPresent() && setting.get().getRange() >= p.getLocation().distanceSquared(event.getLocation()))
                minChance = Math.min(minChance, setting.get().getChance());
        }
        
        if (minChance != 100 && (minChance == 0 || minChance < rng.nextInt(100)))
            event.setCancelled(true);
    }
    
    private void applyLimits() {
        worldSettings.forEach((a, b) -> {
            World w = Bukkit.getWorld(a);
            w.setAmbientSpawnLimit(b.getSpawnLimit(SpawnGroup.AMBIENT));
            w.setAnimalSpawnLimit(b.getSpawnLimit(SpawnGroup.ANIMALS));
            w.setMonsterSpawnLimit(b.getSpawnLimit(SpawnGroup.MONSTER));
            w.setWaterAmbientSpawnLimit(b.getSpawnLimit(SpawnGroup.WATER_AMBIENT));
            w.setWaterAnimalSpawnLimit(b.getSpawnLimit(SpawnGroup.WATER_ANIMALS));
            
            w.setTicksPerAmbientSpawns(b.getTicksPerSpawn(SpawnGroup.AMBIENT));
            w.setTicksPerAnimalSpawns(b.getTicksPerSpawn(SpawnGroup.ANIMALS));
            w.setTicksPerMonsterSpawns(b.getTicksPerSpawn(SpawnGroup.MONSTER));
            w.setTicksPerWaterAmbientSpawns(b.getTicksPerSpawn(SpawnGroup.WATER_AMBIENT));
            w.setTicksPerWaterSpawns(b.getTicksPerSpawn(SpawnGroup.WATER_ANIMALS));
        });
    }
}

class WorldSettings {
    Map<SpawnGroup, Integer> spawnLimits = new EnumMap<>(SpawnGroup.class);
    Map<SpawnGroup, Integer> ticksPer = new EnumMap<>(SpawnGroup.class);
    Map<String, GroupSetting> spawnChancePerGroup = new HashMap<>();
    
    public WorldSettings(ConfigurationSection config) {
        spawnLimits.put(SpawnGroup.AMBIENT, config.getInt("spawnLimits.ambient", -1));
        spawnLimits.put(SpawnGroup.ANIMALS, config.getInt("spawnLimits.animals", -1));
        spawnLimits.put(SpawnGroup.MONSTER, config.getInt("spawnLimits.monster", -1));
        spawnLimits.put(SpawnGroup.WATER_AMBIENT, config.getInt("spawnLimits.waterAmbient", -1));
        spawnLimits.put(SpawnGroup.WATER_ANIMALS, config.getInt("spawnLimits.waterAnimals", -1));
        
        ticksPer.put(SpawnGroup.AMBIENT, config.getInt("ticksPer.ambient", -1));
        ticksPer.put(SpawnGroup.ANIMALS, config.getInt("ticksPer.animals", -1));
        ticksPer.put(SpawnGroup.MONSTER, config.getInt("ticksPer.monster", -1));
        ticksPer.put(SpawnGroup.WATER_AMBIENT, config.getInt("ticksPer.waterAmbient", -1));
        ticksPer.put(SpawnGroup.WATER_ANIMALS, config.getInt("ticksPer.waterAnimals", -1));
        
        ConfigurationSection spawnChances = config.getConfigurationSection("spawnChance");
        
        if (spawnChances != null)
            spawnChances.getKeys(false).forEach(a -> spawnChancePerGroup.put(a, new GroupSetting(spawnChances.getConfigurationSection(a))));
    }
    
    public Optional<GroupSetting> getGroupSetting(String group) {
        return Optional.ofNullable(spawnChancePerGroup.get(group));
    }
    
    public int getSpawnLimit(SpawnGroup category) {
        return spawnLimits.getOrDefault(category, -1);
    }
    
    public int getTicksPerSpawn(SpawnGroup category) {
        return ticksPer.getOrDefault(category, -1);
    }
}

class GroupSetting {
    private final int range;
    private final int chance;
    
    public GroupSetting(ConfigurationSection config) {
        range = config.getInt("range", 0) * config.getInt("range", 0);
        chance = config.getInt("chance", 100);
    }
    
    public int getRange() {
        return range;
    }
    
    public int getChance() {
        return chance;
    }
}

enum SpawnGroup {
    MONSTER,
    ANIMALS,
    AMBIENT,
    WATER_ANIMALS,
    WATER_AMBIENT;
}
