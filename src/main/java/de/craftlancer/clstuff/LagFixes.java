package de.craftlancer.clstuff;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.craftlancer.core.NMSUtils;

public class LagFixes implements Listener {
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GUARDIAN && NMSUtils.getServerTick() % 2 == 0) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getEntityType() == EntityType.PILLAGER && event.getLocation().getBlockY() < 62) {
            event.setCancelled(true);
            return;
        }
    }
    
}
