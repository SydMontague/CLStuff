package de.craftlancer.clstuff.heroes.runnables;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.util.Tuple;

public class BaltopCalculateRunnable extends BukkitRunnable {
    private Heroes heroes;
    
    public BaltopCalculateRunnable(Heroes heroes) {
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        List<Tuple<UUID, Double>> top3 = heroes.getPlugin().getRankings().updateScores().entrySet().stream().map(a -> new Tuple<UUID, Double>(a.getKey(), a.getValue().getBalance()))
                     .sorted(Comparator.comparingDouble(Tuple<UUID, Double>::getValue).reversed()).limit(3).collect(Collectors.toList());
        
        new BaltopApplyRunnable(top3, heroes).runTask(heroes.getPlugin());
    }
}
