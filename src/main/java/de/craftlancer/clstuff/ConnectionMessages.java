package de.craftlancer.clstuff;

import de.craftlancer.core.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConnectionMessages implements Listener, TabExecutor {
    private String prefix;
    private String defaultLoginMessage;
    private String defaultLogoutMessage;
    private Map<Permission, String> login = new HashMap<>();
    private Map<Permission, String> logout = new HashMap<>();
    private CLStuff plugin;
    private File messagesFile;
    
    public ConnectionMessages(CLStuff plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "connectionMessages.yml");
        
        load();
    }
    
    private void load() {
        if (!messagesFile.exists())
            plugin.saveResource(messagesFile.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        
        ConfigurationSection loginSection = config.getConfigurationSection("loginMessages");
        ConfigurationSection logoutSection = config.getConfigurationSection("logoutMessages");
        
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "&f&l[&4&lC&f&lC] &e"));
        defaultLoginMessage = config.getString("defaultLoginMessage");
        defaultLogoutMessage = config.getString("defaultLogoutMessage");
        
        loginSection.getKeys(false).forEach(key -> {
            Permission permission = new Permission(key.replaceAll("_", "."));
            String message = loginSection.getString(key);
            
            if (Bukkit.getPluginManager().getPermission(permission.getName()) == null)
                Bukkit.getPluginManager().addPermission(permission);
            login.put(permission, message);
        });
        
        logoutSection.getKeys(false).forEach(key -> {
            Permission permission = new Permission(key.replaceAll("_", "."));
            String message = logoutSection.getString(key);
            
            if (Bukkit.getPluginManager().getPermission(permission.getName()) == null)
                Bukkit.getPluginManager().addPermission(permission);
            logout.put(permission, message);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<Map.Entry<Permission, String>> stream = login.entrySet().stream().filter(entry -> player.hasPermission(entry.getKey())).collect(Collectors.toList());
        
        //Gets the last entry (the one with most priority) that the user has.
        Optional<String> optional = stream.stream().skip(stream.size() - 1).map(Map.Entry::getValue).findFirst();
        
        event.setJoinMessage(format(optional.orElse(defaultLoginMessage), player.getDisplayName()));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<Map.Entry<Permission, String>> stream = logout.entrySet().stream().filter(entry -> player.hasPermission(entry.getKey())).collect(Collectors.toList());
        
        //Gets the last entry (the one with most priority) that the user has.
        Optional<String> optional = stream.stream().skip(stream.size() - 1).map(Map.Entry::getValue).findFirst();
        
        event.setQuitMessage(format(optional.orElse(defaultLogoutMessage), player.getDisplayName()));
    }
    
    private String format(String message, String name) {
        return ChatColor.translateAlternateColorCodes('&', message.replaceAll("%player%", name).replaceAll("%prefix%", prefix));
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            load();
            sender.sendMessage(prefix + " §aMessages reloaded.");
        } else
            sender.sendMessage(prefix + " §cPlease enter a valid argument.");
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1)
            return Utils.getMatches(args[0], Collections.singletonList("reload"));
        return Collections.emptyList();
    }
}
