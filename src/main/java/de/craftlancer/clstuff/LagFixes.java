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
    private static final String IRON_GOLEM_MOB_META = "ironGolemMob";
    private static final int SPAWNER_MOB_TIMEOUT = 2400; // 2 minutes
    private static final int PILLAGER_MOB_TIMEOUT = 6000; // 5 minutes
    private static final int IRON_GOLEM_MOB_TIMEOUT = 12000; // 10 minutes
    
    private List<LivingEntity> spawnerEntities = new LinkedList<>();
    private List<LivingEntity> pillagerEntities = new LinkedList<>();
    private List<LivingEntity> golemEntities = new LinkedList<>();
    
    private CLStuff plugin;
    
    private static boolean checkEntity(LivingEntity a, int timeout) {
        if(!a.isValid())
            return true;
        
        if(a.getCustomName() == null && a.getTicksLived() > timeout) {
            a.remove();
            return true;
        }
        
        return false;
    }
    
    public LagFixes(CLStuff plugin) {
        this.plugin = plugin;
        
        new LambdaRunnable(() -> {
            spawnerEntities.removeIf(a -> checkEntity(a, SPAWNER_MOB_TIMEOUT));
            pillagerEntities.removeIf(a -> checkEntity(a, PILLAGER_MOB_TIMEOUT));
            golemEntities.removeIf(a -> checkEntity(a, IRON_GOLEM_MOB_TIMEOUT));
        }).runTaskTimer(plugin, SPAWNER_MOB_TIMEOUT, 100);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        // only spawn ever 4th guardian, fish or bat
        switch(event.getEntityType()) {
            case GUARDIAN:
            case COD:
            case TROPICAL_FISH:
            case PUFFERFISH:
            case BAT:
            case SALMON:
                if(event.getSpawnReason() == SpawnReason.NATURAL && event.getEntity().getEntityId() % 4 != 0) {
                    event.setCancelled(true);
                    return;
                }
                break;
            default:
                break;
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
        if (event.getEntityType() == EntityType.GUARDIAN && (recentTPS[0] < 16D || recentTPS[1] < 17D || recentTPS[2] < 18D))
            event.setCancelled(true);
        if (event.getEntityType() == EntityType.PILLAGER && (recentTPS[0] < 17D || recentTPS[1] < 18D || recentTPS[2] < 19D))
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
        if (event.getSpawnReason() == SpawnReason.RAID || event.getEntityType() == EntityType.PILLAGER) {
            event.getEntity().setMetadata(PILLAGER_MOB_META, new FixedMetadataValue(plugin, 0));
            pillagerEntities.add(event.getEntity());
        }
        if (event.getSpawnReason() != SpawnReason.BUILD_IRONGOLEM && event.getEntityType() == EntityType.IRON_GOLEM) {
            event.getEntity().setMetadata(IRON_GOLEM_MOB_META, new FixedMetadataValue(plugin, 0));
            golemEntities.add(event.getEntity());
        }
    }
}
