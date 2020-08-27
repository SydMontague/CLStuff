package de.craftlancer.clstuff.heroes;

import de.craftlancer.clstuff.CLStuff;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Heroes {
    
    private static File CONFIG_FILE;
    private static File LOCATIONS_FILE;
    private static File OLD_LOCATIONS_FILE;
    
    private List<HeroesLocation> heroesLocations;
    private CLStuff plugin;
    
    public Heroes(CLStuff plugin) {
        this.plugin = plugin;
        
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "/heroes" + File.separator + "/config.yml");
        LOCATIONS_FILE = new File(plugin.getDataFolder() + File.separator + "/heroes" + File.separator + "/locations.yml");
        OLD_LOCATIONS_FILE = new File(plugin.getDataFolder() + File.separator + "/heroes" + File.separator + "/oldLocations.yml");
        
        Config.load();
        
        new BaltopCalculateRunnable(this).runTaskTimerAsynchronously(plugin, 0, Config.DELAY);
        
        onEnable();
    }
    
    private void onEnable() {
        if (OLD_LOCATIONS_FILE.exists()) {
            heroesLocations = new ArrayList<>();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(OLD_LOCATIONS_FILE);
            for (String category : config.getKeys(false)) {
                for (String ranking : config.getConfigurationSection(category).getKeys(false)) {
                    ConfigurationSection section = config.getConfigurationSection(category).getConfigurationSection(ranking);
                    HeroesLocation heroesLocation = new HeroesLocation(category, ranking);
                    heroesLocation.setSignLocations((List<Location>) section.getList("sign_location"));
                    heroesLocation.setDisplayLocations((List<Location>) section.getList(section.contains("banner_location") ? "banner_location" : "head_location"));
                    heroesLocations.add(heroesLocation);
                }
            }
            OLD_LOCATIONS_FILE.delete();
        } else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(LOCATIONS_FILE);
            heroesLocations = (List<HeroesLocation>) config.getList("locations");
        }
    }
    
    public void save() {
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(LOCATIONS_FILE);
        
        config.set("locations", heroesLocations);
        
        try {
            config.save(LOCATIONS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    public static class Config {
        public static String PREFIX;
        public static int DELAY;
        
        public static void load() {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(CONFIG_FILE);
            
            PREFIX = configuration.getString("Prefix");
            DELAY = configuration.getInt("Refresh_Delay");
        }
    }
}
