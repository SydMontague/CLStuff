package de.craftlancer.clstuff.connectionmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConnectionMessagesMessageGetCommand extends SubCommand {
    
    private ConnectionMessages connectionMessages;
    
    public ConnectionMessagesMessageGetCommand(Plugin plugin, ConnectionMessages connectionMessages) {
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
        
        UUID owner = optional.get().getOwner();
        String message = optional.get().getMessage();
        PendingMessage.MessageType type = optional.get().getType();
        
        ComponentBuilder builder = new ComponentBuilder()
                .append("Message type: " + type.toString()).color(ChatColor.GRAY)
                .append("\nMessage owner: " + Bukkit.getOfflinePlayer(owner).getName() + " (" + owner + ")").color(ChatColor.GRAY)
                .append("\nMessage: ").color(ChatColor.GRAY)
                .append(connectionMessages.format(message, Bukkit.getOfflinePlayer(owner).getName(), type))
                .append("\n[YES] ").bold(true).color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connectionmessages message accept " + owner.toString()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Accept request").color(ChatColor.GREEN).create()))
                .append("[NO]").bold(true).color(ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connectionmessages message deny " + owner.toString()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Deny request").color(ChatColor.RED).create()));
        
        sender.spigot().sendMessage(builder.create());
        
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
