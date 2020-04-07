package de.craftlancer.clstuff;

import java.util.Arrays;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.craftlancer.core.NMSUtils;

public class LagFixes implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GUARDIAN && NMSUtils.getServerTick() % 2 == 0) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getEntityType() == EntityType.PILLAGER && event.getLocation().getBlockY() < 62) {
            event.setCancelled(true);
            return;
        }
        
        switch (event.getSpawnReason()) {
            case BREEDING:
            case DEFAULT:
            case EGG:
            case DISPENSE_EGG:
            case NATURAL:
                break;
            default:
                return;
        }
        
        if (Arrays.stream(event.getLocation().getChunk().getEntities()).filter(a -> a instanceof LivingEntity).count() > 50)
            event.setCancelled(true);
    }
    
}
