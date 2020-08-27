package de.craftlancer.clstuff.heroes.runnables;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.util.Tuple;

public class PlayerScoreCalculateRunnable extends BukkitRunnable {
    private Heroes heroes;
    
    public PlayerScoreCalculateRunnable(Heroes heroes) {
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        List<Tuple<UUID, Integer>> entries = heroes.getPlugin().getRankings().updateScores().entrySet().stream()
                .map(a -> new Tuple<>(a.getKey(), a.getValue().getScore()))
                .sorted(Comparator.comparingInt(Tuple<UUID, Integer>::getValue).reversed()).limit(3).collect(Collectors.toList());
        
        new PlayerScoreApplyRunnable(entries, heroes).runTask(heroes.getPlugin());
    }
    
}
