package de.craftlancer.clstuff;

import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.Tuple;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConnectionMessages implements Listener, TabExecutor {
    private String prefix;
    private String defaultLoginMessage;
    private String defaultLogoutMessage;
    
    private LinkedList<Tuple<Permission, String>> login;
    private LinkedList<Tuple<Permission, String>> logout;
    
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
        
        login = new LinkedList<>();
        logout = new LinkedList<>();
        
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "&f&l[&4&lC&f&lC] &e"));
        defaultLoginMessage = config.getString("defaultLoginMessage");
        defaultLogoutMessage = config.getString("defaultLogoutMessage");
        
        loginSection.getKeys(false).forEach(key -> {
            Permission permission = new Permission(key.replace("_", "."));
            String message = loginSection.getString(key);
            
            if (Bukkit.getPluginManager().getPermission(permission.getName()) == null)
                Bukkit.getPluginManager().addPermission(permission);
            login.add(new Tuple<>(permission, message));
        });
        
        logoutSection.getKeys(false).forEach(key -> {
            Permission permission = new Permission(key.replace("_", "."));
            String message = logoutSection.getString(key);
            
            if (Bukkit.getPluginManager().getPermission(permission.getName()) == null)
                Bukkit.getPluginManager().addPermission(permission);
            logout.add(new Tuple<>(permission, message));
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Gets the last entry (the one with most priority) that the user has.
        Iterator<Tuple<Permission, String>> itr = login.descendingIterator();
        String msg = defaultLoginMessage;

        while(itr.hasNext()) {
            Tuple<Permission, String> next = itr.next();
            
            if(player.hasPermission(next.getKey())) {
                msg = next.getValue();
                break;
            }
        }
        
        String finalMsg = msg;
        
        event.setJoinMessage(null);
        new LambdaRunnable(() -> Bukkit.broadcastMessage(format(finalMsg, player.getDisplayName()))).runTaskLater(plugin, 1L);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        //Gets the last entry (the one with most priority) that the user has.
        Iterator<Tuple<Permission, String>> itr = logout.descendingIterator();
        String msg = defaultLogoutMessage;

        while(itr.hasNext()) {
            Tuple<Permission, String> next = itr.next();
            
            if(player.hasPermission(next.getKey())) {
                msg = next.getValue();
                break;
            }
        }
        
        event.setQuitMessage(format(msg, player.getDisplayName()));
    }
    
    private String format(String message, String name) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("%player%", name).replace("%prefix%", prefix));
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
