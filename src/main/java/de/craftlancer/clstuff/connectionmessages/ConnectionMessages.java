package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.premium.DonatorTicketRegistry;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.Tuple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConnectionMessages implements Listener {
    private String prefix;
    private String loginPrefix;
    private String logoutPrefix;
    private String defaultLoginMessage;
    private String defaultLogoutMessage;
    
    private LinkedList<Tuple<Permission, String>> login;
    private LinkedList<Tuple<Permission, String>> logout;
    
    private List<UserMessage> userMessages;
    private List<PendingMessage> pendingMessages;
    
    
    private CLStuff plugin;
    private File messagesFile;
    private File dataFile;
    
    public ConnectionMessages(CLStuff plugin) {
        ConfigurationSerialization.registerClass(PendingMessage.class);
        ConfigurationSerialization.registerClass(UserMessage.class);
        
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "connectionMessages.yml");
        this.dataFile = new File(plugin.getDataFolder(), "connectionMessagesData.yml");
        
        load(true);

        plugin.getCommand("connectionmessages").setExecutor(new ConnectionMessagesCommandHandler(plugin, this));
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    protected void load(boolean data) {
        if (!messagesFile.exists())
            plugin.saveResource(messagesFile.getName(), false);
        if (!dataFile.exists())
            plugin.saveResource(dataFile.getName(), false);
        
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        ConfigurationSection loginSection = messagesConfig.getConfigurationSection("loginMessages");
        ConfigurationSection logoutSection = messagesConfig.getConfigurationSection("logoutMessages");
        
        login = new LinkedList<>();
        logout = new LinkedList<>();
        
        loginPrefix = messagesConfig.getString("joinPrefix", "&8[&a+&8]&e");
        logoutPrefix = messagesConfig.getString("leavePrefix", "&8[&c-&8]&e");
        prefix = "§8[§dConnectionMessages§8]§a ";
        defaultLoginMessage = messagesConfig.getString("defaultLoginMessage", "");
        defaultLogoutMessage = messagesConfig.getString("defaultLogoutMessage", "");
        
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
        
        if (!data)
            return;
        
        userMessages = (List<UserMessage>) dataConfig.getList("users", new ArrayList<>());
        pendingMessages = (List<PendingMessage>) dataConfig.getList("pending", new ArrayList<>());
        
    }
    
    public void save() {
        if (!dataFile.exists())
            plugin.saveResource(dataFile.getName(), false);
        
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        dataConfig.set("users", userMessages);
        dataConfig.set("pending", pendingMessages);
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        //Gets the last entry (the one with most priority) that the user has.
        Iterator<Tuple<Permission, String>> itr = login.descendingIterator();
        String msg = defaultLoginMessage;
        
        Optional<UserMessage> optional = getUserMessage(player.getUniqueId());
        
        if (!optional.isPresent() || !optional.get().hasLogin())
            while (itr.hasNext()) {
                Tuple<Permission, String> next = itr.next();
                
                if (player.hasPermission(next.getKey())) {
                    msg = next.getValue();
                    break;
                }
            }
        else
            msg = optional.get().getLogin();
        
        String finalMsg = msg;
        
        event.setJoinMessage(null);
        new LambdaRunnable(() -> Bukkit.broadcastMessage(format(finalMsg, player.getDisplayName(), loginPrefix))).runTaskLater(plugin, 2L);
        
        if (!player.hasPermission(CLStuff.getAdminPermission()))
            return;
        
        new LambdaRunnable(() -> sendPendingMessages(player)).runTaskLater(plugin, 100);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        //Gets the last entry (the one with most priority) that the user has.
        Iterator<Tuple<Permission, String>> itr = logout.descendingIterator();
        String msg = defaultLogoutMessage;
        
        Optional<UserMessage> optional = getUserMessage(player.getUniqueId());
        
        if (!optional.isPresent() || !optional.get().hasLogout())
            while (itr.hasNext()) {
                Tuple<Permission, String> next = itr.next();
                
                if (player.hasPermission(next.getKey())) {
                    msg = next.getValue();
                    break;
                }
            }
        else
            msg = optional.get().getLogout();
        event.setQuitMessage(format(msg, player.getDisplayName(), logoutPrefix));
    }
    
    public void sendPendingMessages(Player player) {
        if (pendingMessages.size() < 1)
            return;
        player.sendMessage(prefix + "Pending messages: (§2" + pendingMessages.size() + "§a)");
        pendingMessages.forEach(p -> {
            ComponentBuilder builder = new ComponentBuilder(Bukkit.getOfflinePlayer(p.getOwner()).getName())
                    .color(ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connectionmessages message get " + p.getOwner().toString()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Show message for " + Bukkit.getOfflinePlayer(p.getOwner()).getName()).color(ChatColor.GREEN).create()));
            player.spigot().sendMessage(ChatMessageType.CHAT, builder.create());
        });
    }
    
    public void acceptPending(PendingMessage message) {
        Optional<UserMessage> optional = getUserMessage(message.getOwner());
        UserMessage userMessage;
        if (optional.isPresent())
            userMessage = optional.get();
        else {
            userMessage = new UserMessage(message.getOwner());
            userMessages.add(userMessage);
        }
        
        if (message.getType() == PendingMessage.MessageType.LOGIN)
            userMessage.setLogin(message.getMessage());
        else
            userMessage.setLogout(message.getMessage());
        
        pendingMessages.remove(message);
        
        Player player = Bukkit.getPlayer(message.getOwner());
        
        if (player == null)
            return;
        player.sendMessage(prefix + "Your requested " + message.getType().name().toLowerCase() + " has been accepted.");
    }
    
    public void denyPending(PendingMessage message) {
        pendingMessages.remove(message);
        DonatorTicketRegistry.getInstance().updatePoints(message.getOwner(), 1);
        
        Player player = Bukkit.getPlayer(message.getOwner());
        
        if (player == null)
            return;
        player.sendMessage(prefix + "Your requested " + message.getType().name().toLowerCase() + " has been denied.");
    }
    
    protected String format(String message, String name, String prefix) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("%player%", name).replace("%prefix%", prefix));
    }
    
    protected String format(String message, String name, PendingMessage.MessageType type) {
        return ChatColor.translateAlternateColorCodes('&',
                message.replace("%player%", name)
                        .replace("%prefix%", type == PendingMessage.MessageType.LOGIN ? loginPrefix : logoutPrefix));
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void addPending(PendingMessage message) {
        pendingMessages.add(message);
    }
    
    public List<PendingMessage> getPendingMessages() {
        return pendingMessages;
    }
    
    private Optional<UserMessage> getUserMessage(UUID owner) {
        return userMessages.stream().filter(m -> m.getOwner().equals(owner)).findFirst();
    }
}
