package de.craftlancer.clstuff.heroes;

import de.craftlancer.core.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class PlayerScoreApplyRunnable extends BukkitRunnable {
    
    private List<Tuple<UUID, Integer>> top3;
    private YamlConfiguration config = new YamlConfiguration();
    private Heroes heroes;
    
    PlayerScoreApplyRunnable(List<Tuple<UUID, Integer>> top3, Heroes heroes) {
        this.top3 = top3;
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        int counter = 1;
        for (Tuple<UUID, Integer> entry : top3) {
            UUID uuid = entry.getKey();
            HeroesLocation heroesLocation = heroes.getHeroLocation("playertop", String.valueOf(counter));
            
            List<Location> signLocationList = heroesLocation.getSignLocations();
            List<Location> headLocationList = heroesLocation.getDisplayLocations();
            
            signLocationList.forEach(signLocation -> setSign(signLocation, entry.getValue(), entry.getKey()));
            
            int i = 0;
            for (Location location : headLocationList) {
                new ApplyHeadRunnable(location, uuid).runTaskLater(heroes.getPlugin(), counter * 80 * (i + 1));
                i++;
            }
            
            counter++;
        }
    }
    
    private void setSign(Location signLocation, int score, UUID playerUUID) {
        if (signLocation == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4))
            return;
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
        sign.setLine(1, ChatColor.WHITE + Bukkit.getOfflinePlayer(playerUUID).getName());
        sign.setLine(2, ChatColor.GOLD + "" + score);
        sign.update();
    }
}


