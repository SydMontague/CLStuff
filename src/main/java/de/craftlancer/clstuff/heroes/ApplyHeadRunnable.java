package de.craftlancer.clstuff.heroes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ApplyHeadRunnable extends BukkitRunnable {
    private Location location;
    private UUID uuid;
    
    ApplyHeadRunnable(Location location, UUID uuid) {
        this.location = location;
        this.uuid = uuid;
    }
    
    @Override
    public void run() {
        if (!MaterialUtil.isHead(location.getBlock().getType()))
            return;
        
        //for the fun of it, why not?
        location.getWorld().playSound(location, Sound.ENTITY_TURTLE_EGG_HATCH, 1f, 1f);
        
        Skull skull = (Skull) location.getBlock().getState();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.update();
    }
}
