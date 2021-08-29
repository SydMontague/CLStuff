package de.craftlancer.clstuff.heroes;

import de.craftlancer.clapi.clstuff.heroes.AbstractHeroesLocation;
import de.craftlancer.clapi.clstuff.heroes.HeroesCategory;
import de.craftlancer.clapi.clstuff.heroes.HeroesManager;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.commands.HeroesCommandHandler;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MaterialUtil;
import de.craftlancer.core.util.MessageRegisterable;
import de.craftlancer.core.util.MessageUtil;
import de.craftlancer.core.util.Tuple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;

public class Heroes implements HeroesManager, MessageRegisterable {
    private CLStuff plugin;
    private File moduleFolder;
    private String prefix;
    private long refreshDelay;
    
    private List<HeroesCategory> categories;
    
    private List<HeroesLocation> heroesLocations;
    
    private Queue<Tuple<UUID, Location>> applyHeadQueue = new LinkedList<>();
    
    static {
        ConfigurationSerialization.registerClass(HeroesLocation.class);
    }
    
    public Heroes(CLStuff plugin) {
        this.plugin = plugin;
        plugin.getCommand("heroes").setExecutor(new HeroesCommandHandler(plugin, this));
        
        load();
        
        MessageUtil.register(this, new TextComponent("§8[§6Heroes§8]"),
                ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_AQUA, ChatColor.GREEN);
        Bukkit.getServicesManager().register(HeroesManager.class, this, plugin, ServicePriority.Highest);
        
        registerCategory(new CategoryPlayerScore(plugin));
        registerCategory(new CategoryPlayerBalance(plugin));
        registerCategory(new CategoryPlayerPlaytime(plugin));
        
        // one head per minute
        new LambdaRunnable(this::applyHeads).runTaskTimer(getPlugin(), 1200L, 1200L);
        new LambdaRunnable(this::refreshDisplays).runTaskTimer(getPlugin(), 20, refreshDelay);
    }
    
    @Override
    public void registerCategory(HeroesCategory heroesCategory) {
        if (categories == null)
            categories = new ArrayList<>();
        
        categories.add(heroesCategory);
    }
    
    @Override
    public void refreshDisplays() {
        new HeroesCategoryRunnable(plugin, this, categories.get(0), 1).run();
    }
    
    @Override
    public void load() {
        
        moduleFolder = new File(plugin.getDataFolder(), "heroes");
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(moduleFolder, "config.yml"));
        prefix = config.getString("Prefix", "");
        refreshDelay = config.getInt("Refresh_Delay", 432000);
        
        YamlConfiguration locationConfig = YamlConfiguration.loadConfiguration(new File(moduleFolder, "locations.yml"));
        heroesLocations = (List<HeroesLocation>) locationConfig.getList("locations", new ArrayList<>());
    }
    
    @Override
    public void save() {
        File locationFile = new File(moduleFolder, "locations.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
        
        config.set("locations", heroesLocations);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(locationFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving HeroesLocations: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    private void applyHeads() {
        if (applyHeadQueue.isEmpty())
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
    
    public CLStuff getPlugin() {
        return plugin;
    }
    
    public List<HeroesLocation> getHeroesLocations() {
        return heroesLocations;
    }
    
    public void addHeadUpdate(UUID key, Location location) {
        applyHeadQueue.add(new Tuple<>(key, location));
    }
    
    public AbstractHeroesLocation getHeroLocation(String category, String ranking) {
        Optional<HeroesLocation> optional = heroesLocations.stream()
                .filter(h -> h.getCategory().equals(category) && h.getRanking().equals(ranking))
                .findFirst();
        
        HeroesLocation location;
        if (!optional.isPresent()) {
            location = new HeroesLocation(category, ranking);
            heroesLocations.add(location);
        } else
            location = optional.get();
        
        return location;
    }
    
    public List<HeroesCategory> getCategories() {
        return categories;
    }
    
    @Override
    public String getMessageID() {
        return "clheroes";
    }
}
