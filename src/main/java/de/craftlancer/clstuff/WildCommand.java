package de.craftlancer.clstuff;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class WildCommand implements CommandExecutor, Listener {
    
    private final Random rng = new Random();
    private final int minRadius;
    private final int maxRadius;
    
    public WildCommand(CLStuff plugin) {
        this.minRadius = plugin.getConfig().getInt("wild.minRadius", 1000);
        this.maxRadius = plugin.getConfig().getInt("wild.maxRadius", 5000);
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;
        
        Block block = event.getClickedBlock();
        
        if (!Tag.SIGNS.isTagged(block.getType()))
            return;
        
        Sign sign = (Sign) block.getState();
        
        if (sign.getLine(1).equals("[Wild]"))
            event.getPlayer().performCommand("wild");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignEdit(SignChangeEvent event) {
        if (event.getLine(1).equalsIgnoreCase("[Wild]") && !event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("clstuff.wild")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions to run this command.");
        }
        else {
            Player player = (Player) sender;
            
            World world = Bukkit.getWorlds().get(0);
            Location loc;
            
            do {
                int distance = minRadius + rng.nextInt(maxRadius - minRadius);
                float rotation = rng.nextFloat() * 2 - 1;
                
                int locX = (int) (distance * rotation);
                int locZ = (int) Math.sqrt((double) distance * distance - locX * locX) * (rng.nextBoolean() ? 1 : -1);
                
                loc = new Location(world, locX + 0.5D, world.getHighestBlockYAt(locX, locZ) + 1D, locZ + 0.5D);
            } while (!isValidLocation(loc));
            
            player.teleport(loc);
        }
        return true;
    }
    
    private boolean isValidLocation(Location loc) {
        if (GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) != null)
            return false;
        
        if (!loc.getWorld().getHighestBlockAt(loc).getType().isSolid())
            return false;
        
        switch (loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ())) {
            case DESERT:
            case DESERT_HILLS:
            case DESERT_LAKES:
            case ICE_SPIKES:
            case BADLANDS:
            case COLD_OCEAN:
            case DEEP_COLD_OCEAN:
            case DEEP_FROZEN_OCEAN:
            case DEEP_LUKEWARM_OCEAN:
            case DEEP_OCEAN:
            case DEEP_WARM_OCEAN:
            case ERODED_BADLANDS:
            case FROZEN_OCEAN:
            case MUSHROOM_FIELD_SHORE:
            case MUSHROOM_FIELDS:
            case WARM_OCEAN:
            case OCEAN:
            case LUKEWARM_OCEAN:
                return false;
            default:
                break;
        }
        
        return true;
    }
    
}
