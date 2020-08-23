package de.craftlancer.clstuff.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

public class CenterMapCommand implements CommandExecutor {
    
    private CLStuff plugin;
    
    public CenterMapCommand(CLStuff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("clstuff.centermap"))
            return false;
        
        if (!(sender instanceof Player))
            return false;
        
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        Location loc = player.getLocation();
        
        if (item.getType() != Material.FILLED_MAP)
            return false;
        
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.getMapView().setCenterX(loc.getBlockX());
        meta.getMapView().setCenterZ(loc.getBlockZ());
        item.setItemMeta(meta);
        
        MessageUtil.sendMessage(plugin, sender, MessageLevel.NORMAL, String.format("Map center set to %s, %s", loc.getBlockX(), loc.getBlockZ()));
        return true;
    }
    
}
