package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ConnectionMessagesReloadCommand extends SubCommand {
    
    private ConnectionMessages connectionMessages;
    
    public ConnectionMessagesReloadCommand(Plugin plugin, ConnectionMessages connectionMessages) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.connectionMessages = connectionMessages;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return connectionMessages.getPrefix() + " §eYou do not have permission to use this command.";
        
        connectionMessages.load(false);
        return connectionMessages.getPrefix() + " §aMessages reloaded.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
