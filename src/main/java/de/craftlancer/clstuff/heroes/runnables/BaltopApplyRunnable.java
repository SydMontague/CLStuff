package de.craftlancer.clstuff.heroes.runnables;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.HeroesLocation;
import de.craftlancer.clstuff.heroes.MaterialUtil;
import de.craftlancer.core.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BaltopApplyRunnable extends BukkitRunnable {
    private List<Tuple<UUID, Double>> top3;
    private Heroes heroes;
    
    public BaltopApplyRunnable(List<Tuple<UUID, Double>> top3, Heroes heroes) {
        this.top3 = top3;
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        
        int counter = 1;
        for (Tuple<UUID, Double> entry : top3) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                counter++;
                continue;
            }
            
            HeroesLocation heroesLocation = heroes.getHeroLocation("baltop", String.valueOf(counter));
            List<Location> signLocationList = heroesLocation.getSignLocations();
            List<Location> headLocationList = heroesLocation.getDisplayLocations().stream().filter(l -> l.getBlock().getType().name().contains("HEAD")).collect(Collectors.toList());
            List<Location> bannerLocationList = heroesLocation.getDisplayLocations().stream().filter(l -> l.getBlock().getType().name().contains("BANNER")).collect(Collectors.toList());
            
            signLocationList.forEach(signLocation -> setBaltopSign(entry.getKey(), signLocation, entry.getValue()));
            bannerLocationList.forEach(bannerLocation -> ClanApplyRunnable.setClanBanner(CLClans.getInstance().getClan(Bukkit.getOfflinePlayer(entry.getKey())), bannerLocation));
            
            for (Location loc : headLocationList)
                heroes.addHeadUpdate(entry.getKey(), loc);
            
            counter++;
        }
        new ClanCalculateRunnable(heroes).runTaskAsynchronously(heroes.getPlugin());
    }
    
    private static void setBaltopSign(UUID uuid, Location signLocation, Double balance) {
        if (signLocation == null || !MaterialUtil.isSign(signLocation.getBlock().getType())
                || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4)) {
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
