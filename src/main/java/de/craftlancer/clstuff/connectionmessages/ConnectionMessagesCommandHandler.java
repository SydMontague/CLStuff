package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class ConnectionMessagesCommandHandler extends CommandHandler {
    public ConnectionMessagesCommandHandler(Plugin plugin, ConnectionMessages connectionMessages) {
        super(plugin);
        
        registerSubCommand("reload", new ConnectionMessagesReloadCommand(plugin, connectionMessages));
        registerSubCommand("setMessage", new ConnectionMessagesSetMessageCommand(plugin, connectionMessages));
        registerSubCommand("message", new ConnectionMessagesMessageCommand(plugin, connectionMessages));
    }
}
