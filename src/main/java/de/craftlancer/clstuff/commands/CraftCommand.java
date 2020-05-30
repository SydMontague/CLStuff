package de.craftlancer.clstuff.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("clstuff.craft"))
            return false;
        
        if (!player.getInventory().contains(Material.CRAFTING_TABLE))
            return false;
        
        player.openWorkbench(player.getLocation(), true);
        
        return true;
    }
}