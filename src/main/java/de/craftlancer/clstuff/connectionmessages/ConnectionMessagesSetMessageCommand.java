package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.premium.DonatorTicketRegistry;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConnectionMessagesSetMessageCommand extends SubCommand {
    
    private ConnectionMessages connectionMessages;
    
    public ConnectionMessagesSetMessageCommand(Plugin plugin, ConnectionMessages connectionMessages) {
        super("", plugin, false);
        
        this.connectionMessages = connectionMessages;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Arrays.asList("LOGIN", "LOGOUT");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return connectionMessages.getPrefix() + "§cYou do not have permission to use this command.";
        
        Player player = (Player) sender;
        int tickets = DonatorTicketRegistry.getInstance().getPoints(player.getUniqueId());
        
        if (tickets < 1)
            return connectionMessages.getPrefix() + "§cYou have §6" + tickets + " tickets §cbut need 1 ticket to use this command.";
        
        String message = (String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
        PendingMessage.MessageType type = PendingMessage.MessageType.valueOf(args[1]);
        
        DonatorTicketRegistry.getInstance().updatePoints(player.getUniqueId(), -1);
        connectionMessages.addPending(new PendingMessage(message, player.getUniqueId(), type));
        
        return connectionMessages.getPrefix() + "§aSuccessfully submitted a request to change your " + type.name().toLowerCase() + " message.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
