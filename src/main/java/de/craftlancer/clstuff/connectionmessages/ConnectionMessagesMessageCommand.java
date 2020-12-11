package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ConnectionMessagesMessageCommand extends SubCommandHandler {
    public ConnectionMessagesMessageCommand(Plugin plugin, ConnectionMessages connectionMessages) {
        super(CLStuff.getAdminPermission(), plugin, false, 1);
        
        registerSubCommand("accept", new ConnectionMessagesMessageAcceptCommand(plugin, connectionMessages));
        registerSubCommand("deny", new ConnectionMessagesMessageDenyCommand(plugin, connectionMessages));
        registerSubCommand("get", new ConnectionMessagesMessageGetCommand(plugin, connectionMessages));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
