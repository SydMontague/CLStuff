package de.craftlancer.clstuff.heroes;

import de.craftlancer.clapi.LazyService;
import de.craftlancer.clapi.clclans.PluginClans;
import de.craftlancer.clapi.clstuff.heroes.CalculatedPlacement;
import de.craftlancer.clapi.clstuff.heroes.HeroesCategory;
import de.craftlancer.clapi.clstuff.rankings.AbstractRankingsEntry;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryPlayerPlaytime implements HeroesCategory {
    
    private static final LazyService<PluginClans> CLANS = new LazyService<>(PluginClans.class);
    
    private CLStuff plugin;
    
    public CategoryPlayerPlaytime(CLStuff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public List<CalculatedPlacement> calculate() {
        return plugin.getRankings().updateScores().values().stream()
                .sorted(Comparator.comparingInt(AbstractRankingsEntry::getPlaytime).reversed())
                .limit(3)
                .map(a ->
                        new CalculatedPlacement(Arrays.asList(
                                "",
                                ChatColor.WHITE + Bukkit.getOfflinePlayer(a.getUUID()).getName(),
                                ChatColor.GOLD + "" + Utils.ticksToTimeString(a.getPlaytime() * 20L * 60L),
                                ""),
                                !CLANS.isPresent() ? null : (CLANS.get().getClan(Bukkit.getOfflinePlayer(a.getUUID())) == null ? null
                                        : CLANS.get().getClan(Bukkit.getOfflinePlayer(a.getUUID())).getBanner()),
                                a.getUUID()))
                .collect(Collectors.toList());
    }
    
    @Override
    public String getCategoryName() {
        return "playtimetop";
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
