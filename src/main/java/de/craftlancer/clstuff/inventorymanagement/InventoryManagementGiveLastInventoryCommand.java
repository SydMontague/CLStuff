package de.craftlancer.clstuff.inventorymanagement;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        if (args.length == 3)
            return Utils.getMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        if (args.length == 4) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player != null)
                return Utils.getMatches(args[3], management.getLastInventories(player.getUniqueId()).stream().map(LastInventory::getDate).collect(Collectors.toList()));
        }
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return management.getPrefix() + "You do not have access to this command.";
        
        if (args.length < 2)
            return management.getPrefix() + "Please enter a player name to give the inventory to.";
        if (args.length < 3)
            return management.getPrefix() + "Please enter the name of the player who died.";
        if (args.length < 4)
            return management.getPrefix() + "Please enter a date for the inventory.";
        
        UUID uuid = null;
        try {
            uuid = UUID.fromString(args[2]);
        } catch (IllegalArgumentException e) {
            Player player = Bukkit.getPlayer(args[2]);
            
            if (player != null)
                uuid = player.getUniqueId();
        }
        
        Player to = Bukkit.getPlayer(args[1]);
        
        if (to == null)
            return management.getPrefix() + "Please enter a valid player name that is online.";
        if (uuid == null)
            return management.getPrefix() + "Please enter a valid player name or UUID of the player who died.";
        
        String date = (String.join(" ", Arrays.copyOfRange(args, 3, args.length)));
        
        Optional<LastInventory> optional = management.getLastInventories(uuid).stream().filter(l -> l.getDate().equals(date)).findFirst();
        
        if (!optional.isPresent())
            return management.getPrefix() + "Please enter a valid date for the inventory (use tab complete).";
        
        setContents(to, optional.get());
        
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
