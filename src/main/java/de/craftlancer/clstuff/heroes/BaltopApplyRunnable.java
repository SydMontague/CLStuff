package de.craftlancer.clstuff.heroes;

import de.craftlancer.core.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class BaltopApplyRunnable extends BukkitRunnable {
    private List<Tuple<String, Double>> top3;
    private Heroes heroes;
    
    public BaltopApplyRunnable(List<Tuple<String, Double>> top3, Heroes heroes) {
        this.top3 = top3;
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        
        int counter = 1;
        for (Tuple<String, Double> entry : top3) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                counter++;
                continue;
            }
            UUID uuid = UUID.fromString(entry.getKey());
            HeroesLocation heroesLocation = heroes.getHeroLocation("baltop", String.valueOf(counter));
            
            List<Location> signLocationList = heroesLocation.getSignLocations();
            List<Location> headLocationList = heroesLocation.getDisplayLocations();
            
            signLocationList.forEach(signLocation -> setBaltopSign(uuid, signLocation, entry.getValue()));
            
            int i = 0;
            for (Location location : headLocationList) {
                new ApplyHeadRunnable(location, uuid).runTaskLater(heroes.getPlugin(), counter * 80 * (i + 1));
                i++;
            }
            
            counter++;
        }
        new ClanCalculateRunnable(heroes).runTaskAsynchronously(heroes.getPlugin());
    }
    
    private static void setBaltopSign(UUID uuid, Location signLocation, Double balance) {
        if (signLocation == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4)) {
            return;
        }
        double i = balance;
        int e = (int) i;
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
        sign.setLine(1, ChatColor.WHITE + Bukkit.getOfflinePlayer(uuid).getName());
        sign.setLine(2, ChatColor.GOLD + "$" + e);
        sign.update();
    }
}
