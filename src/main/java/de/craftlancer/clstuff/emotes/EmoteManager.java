package de.craftlancer.clstuff.emotes;

import de.craftlancer.clstuff.CLStuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class EmoteManager {
    protected static String PREFIX;
    protected static final String ADMIN_PERMISSION = "clstuff.emote.admin";
    private static boolean USE_COMMAND_MAP;
    
    private CLStuff plugin;
    private List<Emote> emotes;
    
    public EmoteManager(CLStuff plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(Emote.class);
        load();
        registerPermissions();
        registerCommands();
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "emotes.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        USE_COMMAND_MAP = config.getBoolean("useCommandMap");
        emotes = (List<Emote>) config.getList("emotes");
    }
    
    public void save() {
        try {
            File file = new File(plugin.getDataFolder(), "emotes.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            config.set("emotes", emotes);
            
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            
            emotes.forEach(e -> commandMap.register(e.getName(), new EmoteAliasCommand(e.getName(), e)));
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
}
