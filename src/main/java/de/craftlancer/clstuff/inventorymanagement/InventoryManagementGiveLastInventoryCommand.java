package de.craftlancer.clstuff.inventorymanagement;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManagementGiveLastInventoryCommand extends SubCommand {
    
    private InventoryManagement management;
    
    public InventoryManagementGiveLastInventoryCommand(Plugin plugin, InventoryManagement management) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.management = management;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return management.getPrefix() + "You do not have access to this command.";
        
        if (args.length < 2)
            return management.getPrefix() + "Please enter a player name.";
        
        Player player = Bukkit.getPlayer(args[1]);
        
        if (player == null)
            return management.getPrefix() + "Please enter a valid player name that is online.";
        
        LastInventory lastInventory = management.getLastInventory(player.getUniqueId());
        
        if (lastInventory == null)
            return management.getPrefix() + "This player does not have a last inventory.";
        
        setContents(player, lastInventory);
        
        return management.getPrefix() + "Player's inventory has been updated.";
    }
    
    private void setContents(Player player, LastInventory inventory) {
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.getInventory().setContents(inventory.getContents());
        player.getInventory().setItemInOffHand(inventory.getOffhand());
        player.updateInventory();
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
