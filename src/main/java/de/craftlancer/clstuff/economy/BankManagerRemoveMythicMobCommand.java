package de.craftlancer.clstuff.economy;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BankManagerRemoveMythicMobCommand extends SubCommand {
    
    private final BankManager manager;
    private final CLStuff plugin;
    
    public BankManagerRemoveMythicMobCommand(CLStuff plugin, BankManager manager) {
        super("clstuff.admin", plugin, true);
        
        this.plugin = plugin;
        this.manager = manager;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(plugin, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 2) {
            MessageUtil.sendMessage(plugin, sender, MessageLevel.INFO, "You must enter a mythic mob name.");
            return null;
        }
        
        if (manager.getMobNames().remove(args[1])) {
            MessageUtil.sendMessage(plugin, sender, MessageLevel.SUCCESS, "Successfully removed mythic mob.");
            return null;
        } else {
            
            MessageUtil.sendMessage(plugin, sender, MessageLevel.WARNING, "Could not find a mythic mob by this name.");
            return null;
        }
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], manager.getMobNames());
        
        return Collections.emptyList();
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
