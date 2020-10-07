package de.craftlancer.clstuff.adminshop;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public class AdminShopDefaultBroadcastCommand extends SubCommand {

    private AdminShopManager manager;
    
    public AdminShopDefaultBroadcastCommand(Plugin plugin, AdminShopManager manager) {
        super("clstuff.adminshop", plugin, false);
        this.manager = manager;
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You're not allowed to run this command.";
        
        if(args.length < 2)
            return "You must specify a message.";
        
        manager.setDefaultBroadcast(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        return "Default broadcast message set.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
