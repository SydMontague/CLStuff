package de.craftlancer.clstuff.heroes.runnables;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import de.craftlancer.clstuff.heroes.Heroes;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClanCalculateRunnable extends BukkitRunnable {
    private Heroes heroes;
    
    public ClanCalculateRunnable(Heroes heroes) {
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        List<Clan> list = CLClans.getInstance().getClans().stream().sorted(Comparator.comparingDouble(Clan::calculateClanScore).reversed()).limit(3)
                                 .collect(Collectors.toList());
        new ClanApplyRunnable(list, heroes).runTask(heroes.getPlugin());
    }
}
