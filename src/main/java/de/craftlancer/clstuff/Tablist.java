package de.craftlancer.clstuff;

import de.craftlancer.core.LambdaRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class Tablist {
    
    private List<String> headers;
    private List<String> footers;
    
    private int lastHeaderIndex = 0;
    private int lastFooterIndex = 0;
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
        String nextHeader = getNextHeader();
        String nextFooter = getNextFooter();
        
        for (Player player : Bukkit.getOnlinePlayers())
            player.setPlayerListHeaderFooter(replace(nextHeader, player), replace(nextFooter, player));
    }
    
    private String getNextHeader() {
        lastHeaderIndex = (++lastHeaderIndex) % headers.size();
        return headers.get(lastHeaderIndex);
    }
    
    private String getNextFooter() {
        lastFooterIndex = (++lastFooterIndex) % footers.size();
        return footers.get(lastFooterIndex);
    }
    
    private String replace(String string, Player player) {
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%player%", player.getName()));
    }
}
