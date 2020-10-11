package de.craftlancer.clstuff.heroes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.runnables.BaltopCalculateRunnable;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.Tuple;

public class Heroes {
    private final CLStuff plugin;
    private final File moduleFolder;
    private final String prefix;
    private final long refreshDelay;
    
    private List<HeroesLocation> heroesLocations;
    
    private Queue<Tuple<UUID, Location>> applyHeadQueue = new LinkedList<>();
    
    static {
        ConfigurationSerialization.registerClass(HeroesLocation.class);
    }
    
    @SuppressWarnings("unchecked")
    public Heroes(CLStuff plugin) {
        this.plugin = plugin;
        moduleFolder = new File(plugin.getDataFolder(), "heroes");
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(moduleFolder, "config.yml"));
        prefix = config.getString("Prefix", "");
        refreshDelay = config.getInt("Refresh_Delay", 432000);

        YamlConfiguration locationConfig = YamlConfiguration.loadConfiguration(new File(moduleFolder, "locations.yml"));
        heroesLocations = (List<HeroesLocation>) locationConfig.getList("locations", new ArrayList<>());
        
        new BaltopCalculateRunnable(this).runTaskTimerAsynchronously(plugin, 0, refreshDelay);
        
        // one head per minute
        new LambdaRunnable(this::applyHeads).runTaskTimer(getPlugin(), 1200L, 1200L);
    }
    
    private void applyHeads() {
        if(applyHeadQueue.isEmpty())
            return;
        
        Tuple<UUID, Location> entry = applyHeadQueue.poll();
        
        if (!MaterialUtil.isHead(entry.getValue().getBlock().getType()))
            return;
        
        //for the fun of it, why not?
        entry.getValue().getWorld().playSound(entry.getValue(), Sound.ENTITY_TURTLE_EGG_HATCH, 1f, 1f);
        
        Skull skull = (Skull) entry.getValue().getBlock().getState();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
        skull.update();
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public long getRefreshDelay() {
        return refreshDelay;
    }
    
    public void save() {
        File locationFile = new File(moduleFolder, "locations.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
        
        config.set("locations", heroesLocations);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(locationFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving HeroesLocations: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    public CLStuff getPlugin() {
        return plugin;
    }
    
    public List<HeroesLocation> getHeroesLocations() {
        return heroesLocations;
    }
    
    public HeroesLocation getHeroLocation(String category, String ranking) {
        Optional<HeroesLocation> optional = heroesLocations.stream().filter(h -> h.getCategory().equals(category) && h.getRanking().equals(ranking)).findFirst();
        return optional.orElseGet(() -> new HeroesLocation(category, ranking));
    }
    
    public void addHeroesLocation(HeroesLocation heroesLocation) {
        heroesLocations.add(heroesLocation);
    }

    public void addHeadUpdate(UUID key, Location location) {
        applyHeadQueue.add(new Tuple<>(key, location));
    }
}
