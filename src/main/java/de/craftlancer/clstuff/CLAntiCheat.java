package de.craftlancer.clstuff;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class CLAntiCheat implements Listener {
    private Logger logger;
    
    public CLAntiCheat(Plugin plugin) {
        this.logger = plugin.getLogger();
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Entity vehicle = p.getVehicle();
        
        if (vehicle instanceof InventoryHolder)
            new ArrayList<HumanEntity>((((InventoryHolder) vehicle).getInventory().getViewers())).forEach(HumanEntity::closeInventory);
    }
    
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
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(!e.getMessage().startsWith("sethome"))
            return;
        
        Player p = e.getPlayer();
        Location loc = e.getPlayer().getLocation();
        
        if (GriefPrevention.instance.dataStore.getClaims().stream()
                .noneMatch(a -> a.contains(loc, true, false) && a.allowBuild(p, Material.STONE) == null)) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't use /sethome here, you must be in a claim you can build in.");
        }
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
                return true;
            default:
                return false;
        }
    }
}
