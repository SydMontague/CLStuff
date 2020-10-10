package de.craftlancer.clstuff.emotes;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EmoteManager {
    protected static String PREFIX;
    protected static final String ADMIN_PERMISSION = "clstuff.emote.admin";
    protected static int COOLDOWN;
    private static boolean USE_COMMAND_MAP;
    
    private CLStuff plugin;
    private List<Emote> emotes;
    private List<UUID> cooldowns = new ArrayList<>();
    
    public EmoteManager(CLStuff plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(Emote.class);
        load();
        registerPermissions();
        registerCommands();
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "emotes.yml");
        
        if (!file.exists())
            CLStuff.getInstance().saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        USE_COMMAND_MAP = config.getBoolean("useCommandMap");
        COOLDOWN = config.getInt("cooldown");
        emotes = (List<Emote>) config.getList("emotes");
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
    
    private void registerPermissions() {
        emotes.forEach(e -> Bukkit.getPluginManager().addPermission(new Permission(e.getPermission())));
    }
    
    private void registerCommands() {
        if (!USE_COMMAND_MAP)
            return;
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            
            emotes.forEach(e -> commandMap.register(e.getName(), new EmoteAliasCommand(e.getName(), e, this)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public CLStuff getPlugin() {
        return plugin;
    }
    
    public List<Emote> getEmotes() {
        return emotes;
    }
    
    public void addEmote(Emote emote) {
        emotes.add(emote);
        Bukkit.getPluginManager().addPermission(new Permission(emote.getPermission()));
    }
    
    public void removeEmote(String name) {
        Bukkit.getPluginManager().removePermission("clstuff.emote." + name);
        emotes.removeIf(e -> e.getName().equals(name));
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
