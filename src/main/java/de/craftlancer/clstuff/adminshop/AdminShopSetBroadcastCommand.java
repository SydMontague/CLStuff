package de.craftlancer.clstuff.adminshop;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;

public class AdminShopSetBroadcastCommand extends SubCommand {

    public AdminShopSetBroadcastCommand(Plugin plugin) {
        super("clstuff.adminshop", plugin, false);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You're not allowed to run this command.";
        
        if(args.length < 3)
            return "You must specify an index and a message.";
        
        int id = Utils.parseIntegerOrDefault(args[1], 0);
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        
        Player p = (Player) sender;
        p.setMetadata(AdminShopManager.METADATA_BROADCAST_ID, new FixedMetadataValue(plugin, id));
        p.setMetadata(AdminShopManager.METADATA_BROADCAST_MSG, new FixedMetadataValue(plugin, message));
        return "Right click a shop to set the message for.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
