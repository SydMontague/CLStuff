package de.craftlancer.clstuff;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.api.ICombatLogX;

import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.logging.PluginFileLogger;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.ess3.api.events.TPARequestEvent;
import net.ess3.api.events.UserTeleportHomeEvent;

public class CLAntiCheat implements Listener {
    private Logger logger;
    private Plugin plugin;
    
    private ICombatLogX combatLogPlugin = null;
    
    public CLAntiCheat(Plugin plugin) {
        this.plugin = plugin;
        this.logger = new PluginFileLogger(CLAntiCheat.class.getCanonicalName(), plugin, "anticheat.log");
        
        if (Bukkit.getPluginManager().isPluginEnabled("CombatLogX"))
            combatLogPlugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
    }
    
    /*
     * Prevent Donkey dupe
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Entity vehicle = p.getVehicle();
        
        if (vehicle instanceof InventoryHolder)
            new ArrayList<HumanEntity>((((InventoryHolder) vehicle).getInventory().getViewers())).forEach(HumanEntity::closeInventory);
        
        if (vehicle != null)
            vehicle.getPassengers().forEach(a -> {
                if (a instanceof InventoryHolder)
                    new ArrayList<HumanEntity>((((InventoryHolder) a).getInventory().getViewers())).forEach(HumanEntity::closeInventory);
            });
    }
    
    /*
     * Prevent block glitching to open inventories
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        
        if (!isCheckedInventory(inventory.getType()))
            return;
        
        HumanEntity player = event.getPlayer();
        Block block = player.getTargetBlockExact(8);
        InventoryHolder holder = inventory.getHolder();
        
        if (block == null || holder == null)
            return;
        
        double distBlock = block.getLocation().distanceSquared(player.getLocation());
        if (holder instanceof DoubleChest) {
            BlockInventoryHolder holderLeft = (BlockInventoryHolder) ((DoubleChest) holder).getLeftSide();
            BlockInventoryHolder holderRight = (BlockInventoryHolder) ((DoubleChest) holder).getRightSide();
            
            if (holderLeft.getBlock().equals(block) || holderRight.getBlock().equals(block))
                return;
            
            double distLeft = holderLeft.getBlock().getLocation().distanceSquared(player.getLocation());
            double distRight = holderRight.getBlock().getLocation().distanceSquared(player.getLocation());
            
            if (distBlock > distLeft + 1 || distBlock > distRight + 1)
                return;
        }
        else if (holder instanceof BlockInventoryHolder) {
            BlockInventoryHolder bHolder = (BlockInventoryHolder) holder;
            if (bHolder.getBlock().equals(block) || distBlock > bHolder.getBlock().getLocation().distanceSquared(player.getLocation()) + 1)
                return;
        }
        else
            return;
        
        logger.info(() -> String.format("%s may have tried to block glitch at: %d %d %d | %s",
                                        player.getName(),
                                        block.getX(),
                                        block.getY(),
                                        block.getZ(),
                                        block.getType().name()));
        event.setCancelled(true);
    }
    
    private static boolean isCheckedInventory(InventoryType type) {
        switch (type) {
            case BARREL:
            case BEACON:
            case BLAST_FURNACE:
            case BREWING:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case FURNACE:
            case HOPPER:
            case LECTERN:
            case SHULKER_BOX:
            case SMOKER:
            case ENDER_CHEST:
                return true;
            default:
                return false;
        }
    }
    
    private static boolean hasClaimPermission(Player player, Claim claim, ClaimPermission permission) {
        if (claim == null)
            return true;
        
        if (player.getUniqueId().equals(claim.ownerID))
            return true;
        
        return claim.hasExplicitPermission(player, permission);
    }
    
    /*
     * Log logging out in enemy claims
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getPlayer().getLocation();
        
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        
        if (claim != null && !claim.isAdminClaim() && !hasClaimPermission(p, claim, ClaimPermission.Access)) {
            logger.info(() -> String.format("%s has logged out inside a claim of %s at: %d %d %d",
                                            p.getName(),
                                            claim.getOwnerName(),
                                            loc.getBlockX(),
                                            loc.getBlockY(),
                                            loc.getBlockZ()));
        }
    }
    
    /*
     * Prevent settings home in enemy claims
     */
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!(e.getMessage().startsWith("/sethome") || e.getMessage().startsWith("/ecreatehome")))
            return;
        
        Player p = e.getPlayer();
        Location loc = e.getPlayer().getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        
        if (claim == null || claim.isAdminClaim() || !hasClaimPermission(p, claim, ClaimPermission.Access)) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't use /sethome here, you must be in a claim you can build in.");
            logger.info(() -> String.format("%s tried setting a home at %d %d %d.", p.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
    }
    
    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        
        if (claim == null || claim.isAdminClaim() || !hasClaimPermission(p, claim, ClaimPermission.Access)) {
            e.setCancelled(true);
            e.setUseBed(Result.DENY);
            p.sendMessage(ChatColor.RED + "You can't use /sethome here, you must be in a claim you have access to.");
            logger.info(() -> String.format("%s tried setting a home at %d %d %d.", p.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
    }

    /*
     * Prevent entering enemy claims via /tpa, /home or respawn
     */
    @EventHandler(ignoreCancelled = true)
    public void onTPA(TPARequestEvent event) {
        @SuppressWarnings("deprecation")
        Player target = event.getTarget().getBase();
        Player sender = event.getRequester().getPlayer();
        
        if(target.isOp() || sender.isOp())
            return;
        
        Location loc = event.isTeleportHere() ? sender.getLocation() : target.getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);

        if(claim == null)
            return;
        if(claim.isAdminClaim())
            return;
        if(hasClaimPermission(target, claim, ClaimPermission.Access))
            return;
        if(hasClaimPermission(sender, claim, ClaimPermission.Access))
            return;
        
        event.setCancelled(true);
        event.getRequester().sendMessage(ChatColor.RED + "You can't use /tpa commands to places neither player has access to.");
        
        logger.info(() -> String.format("%s requested /tpa %s %s, %d %d %d without claim access to %s's claim.",
                                        sender.getName(),
                                        event.isTeleportHere() ? "of" : "to",
                                        target.getName(),
                                        loc.getBlockX(),
                                        loc.getBlockY(),
                                        loc.getBlockZ(),
                                        claim.getOwnerName()));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onTeleportHome(UserTeleportHomeEvent event) {
        @SuppressWarnings("deprecation")
        Player sender = event.getUser().getBase();
        Location loc = sender.getLocation();

        if(sender.isOp())
            return;
        
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        
        if(claim == null)
            return;
        if(claim.isAdminClaim())
            return;
        if(hasClaimPermission(sender, claim, ClaimPermission.Access))
            return;
        
        event.setCancelled(true);
        sender.sendMessage(ChatColor.RED + "Your requested home is on a claim you have no access to. Aborting.");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getRespawnLocation();

        if(player.isOp())
            return;
        
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        if(claim == null)
            return;
        if(claim.isAdminClaim())
            return;
        if(hasClaimPermission(player, claim, ClaimPermission.Access))
            return;
        
        player.sendMessage(ChatColor.RED + "Your respawn location is on a claim you have no access to. Please reset your respawn location.");
        event.setRespawnLocation(loc.getWorld().getSpawnLocation());
    }
    
    @EventHandler
    public void onClaimExplosionCommand(PlayerCommandPreprocessEvent e) {
        if (!(e.getMessage().startsWith("/claimexplosion")))
            return;
        
        Player p = e.getPlayer();
        Location loc = e.getPlayer().getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        
        if (claim.isAdminClaim()) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't use /claimexplosions in an admin claim. Don't try it again!");
            logger.info(() -> String.format("%s tried running /claimexplosions in an admin claim.", p.getName()));
        }
    }
    
    @EventHandler
    public void onSpawnerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || !event.hasItem())
            return;
        
        if (event.getPlayer().isOp())
            return;
        
        if (event.getClickedBlock().getType() != Material.SPAWNER)
            return;
        
        if (event.getItem().getType().name().endsWith("SPAWN_EGG")) {
            event.setCancelled(true);
            logger.info(() -> String.format("%s may have tried to change a spawner to %s", event.getPlayer().getName(), event.getItem().getType().name()));
        }
    }
    
    /*
     * Prevent boat glitching and block glitching into vehicles
     */
    @EventHandler
    public void onVehicleLeave(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        LivingEntity passenger = event.getExited();
        new LambdaRunnable(() -> {
            Location start = passenger.getLocation();
            Vector direction = vehicle.getLocation().clone().subtract(start).toVector().normalize();
            double maxDistance = vehicle.getLocation().distance(start);
            
            if (!Double.isFinite(direction.length()))
                return;
            
            RayTraceResult result = passenger.getWorld().rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, true);
            if (result != null) {
                passenger.teleport(vehicle);
                
                logger.info(() -> String.format("%s may have tried to vehicle glitch at: %d %d %d | %s",
                                                passenger.getName(),
                                                start.getBlockX(),
                                                start.getBlockY(),
                                                start.getBlockZ(),
                                                vehicle.getType().name()));
            }
        }).runTask(plugin);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        
        if (player.getVehicle() == entity)
            return;
        
        Location start = player.getEyeLocation();
        double maxDistance = entity.getLocation().distance(start);
        
        RayTraceResult result = player.getWorld().rayTrace(start, start.getDirection(), maxDistance, FluidCollisionMode.NEVER, true, 0.0D, a -> a != player);
        
        if (result == null || result.getHitBlock() != null) {
            event.setCancelled(true);
            
            logger.info(() -> String.format("%s may have tried to block glitch at: %d %d %d | %s | %s",
                                            player.getName(),
                                            start.getBlockX(),
                                            start.getBlockY(),
                                            start.getBlockZ(),
                                            entity.getType().name(),
                                            event.getHand().name()));
        }
    }
}
