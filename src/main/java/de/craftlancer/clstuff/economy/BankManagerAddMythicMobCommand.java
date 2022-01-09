package de.craftlancer.clstuff.economy;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BankManagerAddMythicMobCommand extends SubCommand {
    
    private final BankManager manager;
    private final CLStuff plugin;
    
    public BankManagerAddMythicMobCommand(CLStuff plugin, BankManager manager) {
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
        
        manager.getMobNames().add(args[1]);
        
        MessageUtil.sendMessage(plugin, sender, MessageLevel.SUCCESS, "Added mythic mob to the list of mob names.");
        return null;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Collections.singletonList("<mythicMobName>");
        
        return Collections.emptyList();
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
