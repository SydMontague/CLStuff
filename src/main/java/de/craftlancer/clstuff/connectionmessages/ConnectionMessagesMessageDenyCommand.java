package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConnectionMessagesMessageDenyCommand extends SubCommand {
    
    private ConnectionMessages connectionMessages;
    
    public ConnectionMessagesMessageDenyCommand(Plugin plugin, ConnectionMessages connectionMessages) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.connectionMessages = connectionMessages;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Utils.getMatches(args[2], connectionMessages.getPendingMessages().stream().map(p -> p.getOwner().toString()).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return connectionMessages.getPrefix() + " §eYou do not have permission to use this command.";
        
        Optional<PendingMessage> optional = connectionMessages.getPendingMessages().stream().filter(p -> p.getOwner().toString().equals(args[2])).findFirst();
        
        if (!optional.isPresent())
            return connectionMessages.getPrefix() + " §ePlease enter a valid UUID.";
        
        connectionMessages.denyPending(optional.get());
        
        return connectionMessages.getPrefix() + " §cMessages denied.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
