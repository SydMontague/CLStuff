package de.craftlancer.clstuff.adminshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public class AdminShopRemoveCommand extends SubCommand {
    
    public AdminShopRemoveCommand(Plugin plugin) {
        super("clstuff.adminshop", plugin, false);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You're not allowed to run this command.";
        
        Player p = (Player) sender;
        p.removeMetadata(AdminShopManager.METADATA, plugin);
        p.setMetadata(AdminShopManager.METADATA, new FixedMetadataValue(plugin, AdminShopManager.METADATA_REMOVE));
        return "Right click a shop block to remove it.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
