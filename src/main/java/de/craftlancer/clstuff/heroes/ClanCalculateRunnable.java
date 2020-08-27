package de.craftlancer.clstuff.heroes;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ClanCalculateRunnable extends BukkitRunnable {
    private Heroes heroes;
    
    public ClanCalculateRunnable(Heroes heroes) {
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        new ClanApplyRunnable(CLClans.getInstance().getClans().stream().sorted(Comparator.comparingDouble(Clan::calculateClanScore).reversed()).limit(3).collect(Collectors.toList()), heroes).runTask(heroes.getPlugin());
    }
}
