package de.craftlancer.clstuff;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;

import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.NMSUtils;

// TODO tickrate based mob limits
public class LagFixes implements Listener {
    private static final String SPAWNER_MOB_META = "spawnerMob";
    private static final String PILLAGER_MOB_META = "pillagerMob";
    private static final int SPAWNER_MOB_TIMEOUT = 2400; // 2 minutes
    private static final int PILLAGER_MOB_TIMEOUT = 6000; // 2 minutes
    
    private List<LivingEntity> spawnerEntities = new LinkedList<>();
    private List<LivingEntity> pillagerEntities = new LinkedList<>();
    
    private CLStuff plugin;
    
    public LagFixes(CLStuff plugin) {
        this.plugin = plugin;
        
        new LambdaRunnable(() -> {
            spawnerEntities.removeIf(a -> {
                if(a.getCustomName() == null && a.getTicksLived() > SPAWNER_MOB_TIMEOUT) {
                    a.remove();
                    return true;
                }
                
                return a.isDead();
            });
            pillagerEntities.removeIf(a -> {
                if(a.getCustomName() == null && a.getTicksLived() > PILLAGER_MOB_TIMEOUT) {
                    a.remove();
                    return true;
                }
                
                return a.isDead();
            });
        }).runTaskTimer(plugin, SPAWNER_MOB_TIMEOUT, 100);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        // only every 4th Guardian should be spawned
        if (event.getEntityType() == EntityType.GUARDIAN && event.getEntity().getEntityId() % 4 != 0) {
            event.setCancelled(true);
            return;
        }
        
        // disable underground pillager spawns
        if (event.getEntityType() == EntityType.PILLAGER && event.getLocation().getBlockY() < 62) {
            event.setCancelled(true);
            return;
        }
        
        // disable spawners when server is lagging
        double[] recentTPS = NMSUtils.getRecentTPS();
        if (event.getSpawnReason() == SpawnReason.SPAWNER && (recentTPS[0] < 16D || recentTPS[1] < 17D || recentTPS[2] < 18D))
            event.setCancelled(true);
        
        // prevent chunks from being overcrowded
        switch (event.getSpawnReason()) {
            case BREEDING:
            case DEFAULT:
            case EGG:
            case DISPENSE_EGG:
            case NATURAL:
            case SPAWNER:
            case NETHER_PORTAL:
                break;
            default:
                return;
        }
        
        if (Arrays.stream(event.getLocation().getChunk().getEntities()).filter(a -> a instanceof LivingEntity).count() > 50)
            event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.NETHER_PORTAL) {
            event.getEntity().setMetadata(SPAWNER_MOB_META, new FixedMetadataValue(plugin, 0));
            spawnerEntities.add(event.getEntity());
        }
        if (event.getSpawnReason() != SpawnReason.RAID || event.getEntityType() == EntityType.PILLAGER) {
            event.getEntity().setMetadata(PILLAGER_MOB_META, new FixedMetadataValue(plugin, 0));
            pillagerEntities.add(event.getEntity());
        }
    }
}
