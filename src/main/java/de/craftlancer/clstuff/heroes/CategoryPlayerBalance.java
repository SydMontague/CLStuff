package de.craftlancer.clstuff.heroes;

import de.craftlancer.clapi.clclans.PluginClans;
import de.craftlancer.clapi.clstuff.heroes.CalculatedPlacement;
import de.craftlancer.clapi.clstuff.heroes.HeroesCategory;
import de.craftlancer.clapi.clstuff.rankings.AbstractRankingsEntry;
import de.craftlancer.clstuff.CLStuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryPlayerBalance implements HeroesCategory {
    
    private CLStuff plugin;
    
    public CategoryPlayerBalance(CLStuff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public List<CalculatedPlacement> calculate() {
        PluginClans clans = Bukkit.getServicesManager().load(PluginClans.class);
        
        return plugin.getRankings().updateScores().values().stream()
                .sorted(Comparator.comparingDouble(AbstractRankingsEntry::getBalance).reversed()).limit(3)
                .map(e ->
                        new CalculatedPlacement(Arrays.asList(
                                "",
                                ChatColor.WHITE + Bukkit.getOfflinePlayer(e.getUUID()).getName(),
                                ChatColor.GOLD + "$" + String.format("%.2f", e.getBalance()),
                                ""
                        ), clans == null ? null : (clans.getClan(Bukkit.getOfflinePlayer(e.getUUID())) == null ? null
                                : clans.getClan(Bukkit.getOfflinePlayer(e.getUUID())).getBanner()),
                                e.getUUID())).collect(Collectors.toList());
    }
    
    @Override
    public String getCategoryName() {
        return "baltop";
    }
    
    @Override
    public boolean isCalculateAsync() {
        return true;
    }
    
    @Override
    public boolean hasHead() {
        return true;
    }
    
    @Override
    public boolean hasBanner() {
        return true;
    }
}
