package de.craftlancer.clstuff;

import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Tablist implements Listener, CommandExecutor {
    
    private List<String> headers;
    private List<String> footers;
    
    private int lastHeaderIndex = 0;
    private int lastFooterIndex = 0;
    private String header;
    private String footer;
    private long period;
    
    private CLStuff plugin;
    
    public Tablist(CLStuff plugin) {
        this.plugin = plugin;
        
        load();
        new LambdaRunnable(this::run).runTaskTimer(plugin, 0, period);
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "tablist.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        headers = config.getStringList("headers");
        footers = config.getStringList("footers");
        period = config.getLong("period");
    }
    
    private void run() {
        
        setNextHeader();
        setNextFooter();
        
        Bukkit.getOnlinePlayers().forEach(this::apply);
    }
    
    private void apply(Player player) {
        player.setPlayerListName(player.getDisplayName());
        player.setPlayerListHeaderFooter(replace(header, player), replace(footer, player));
    }
    
    private void setNextHeader() {
        lastHeaderIndex = (++lastHeaderIndex) % headers.size();
        header = headers.get(lastHeaderIndex);
    }
    
    private void setNextFooter() {
        lastFooterIndex = (++lastFooterIndex) % footers.size();
        footer = footers.get(lastFooterIndex);
    }
    
    private String replace(String string, Player player) {
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%player%", player.getName())
                .replace("%ping%", String.valueOf(NMSUtils.getPing(player))));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.apply(player);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        
        if (!sender.hasPermission(CLStuff.getAdminPermission())) {
            sender.sendMessage("§cYou do not have access to this command.");
            return true;
        }
        
        load();
        sender.sendMessage("§aTablist has been reloaded.");
        
        return true;
    }
}
