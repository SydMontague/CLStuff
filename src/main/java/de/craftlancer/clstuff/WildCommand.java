package de.craftlancer.clstuff;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class WildCommand implements CommandExecutor {
    
    private final Random rng = new Random();
    private final int minRadius;
    private final int maxRadius;
    
    public WildCommand(int minRadius, int maxRadius) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
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
                
                loc = new Location(world, locX, world.getHighestBlockYAt(locX, locZ) + 1D, locZ);
            } while (GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) != null || !world.getHighestBlockAt(loc).getType().isSolid());
            
            player.teleport(loc);
        }
        return true;
    }
    
}
