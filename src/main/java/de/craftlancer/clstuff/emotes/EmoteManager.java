package de.craftlancer.clstuff.emotes;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class EmoteManager {
    
    private String prefix;
    private int cooldown;
    private boolean useCommandMap;
    
    private CLStuff plugin;
    private Map<String, Emote> emotes = new HashMap<>();
    private List<UUID> cooldowns = new ArrayList<>();
    
    public EmoteManager(CLStuff plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(Emote.class);
        load();
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "emotes.yml");
        
        if (!file.exists())
            CLStuff.getInstance().saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", ""));
        useCommandMap = config.getBoolean("useCommandMap", true);
        cooldown = config.getInt("cooldown", 60);
        
        ((List<Emote>) config.getList("emotes", new ArrayList<>())).forEach(this::addEmote);
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public void save() {
        File file = new File(plugin.getDataFolder(), "emotes.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        config.set("emotes", emotes);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(file);
            } catch (IOException e) {
                CLStuff.getInstance().getLogger().log(Level.SEVERE, "Error while saving: ", e);
            }
        });
        
        if (CLStuff.getInstance().isEnabled())
            saveTask.runTaskAsynchronously(CLStuff.getInstance());
        else
            saveTask.run();
    }
    
    public CLStuff getPlugin() {
        return plugin;
    }
    
    public Map<String, Emote> getEmotes() {
        return emotes;
    }
    
    public Emote getEmote(String emote) {
        return emotes.get(emote);
    }
    
    public boolean hasEmote(String emote) {
        return emotes.containsKey(emote);
    }
    
    public void addEmote(Emote emote) {
        emotes.put(emote.getName(), emote);
        
        if (Bukkit.getPluginManager().getPermission(emote.getPermission()) == null)
            Bukkit.getPluginManager().addPermission(new Permission(emote.getPermission()));
        
        if (useCommandMap)
            NMSUtils.getCommandMap().register(emote.getName(), new EmoteAliasCommand(emote, this));
    }
    
    public boolean removeEmote(String name) {
        Bukkit.getPluginManager().removePermission("clstuff.emote." + name);
        return emotes.remove(name) != null;
    }
    
    public void addCooldown(UUID uuid) {
        cooldowns.add(uuid);
    }
    
    public void removeCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }
    
    public boolean hasCooldown(UUID uuid) {
        return !Bukkit.getPlayer(uuid).hasPermission("clstuff.emote.bypasscooldown") && cooldowns.contains(uuid);
    }
}
