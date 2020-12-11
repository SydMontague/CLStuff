package de.craftlancer.clstuff.premium;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DonatorTicketSetPremiumCommand extends SubCommand {
    
    private DonatorTicketRegistry registry;
    private CLStuff plugin;
    
    public DonatorTicketSetPremiumCommand(Plugin plugin, DonatorTicketRegistry registry) {
        super(CLStuff.getAdminPermission(), plugin, true);
        
        this.registry = registry;
        this.plugin = (CLStuff) plugin;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        if (args.length == 3)
            return Utils.getMatches(args[2], Arrays.asList("true", "false"));
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return registry.getPrefix() + "You do not have access to this command.";
        
        UUID uuid = null;
        try {
            uuid = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            Player player = Bukkit.getPlayer(args[1]);
            
            if (player != null)
                uuid = player.getUniqueId();
        }
        
        if (uuid == null)
            return registry.getPrefix() + "You must enter a valid player name or UUID.";
        
        if (args.length < 3)
            return registry.getPrefix() + "Please enter the correct amount of arguments.";
        
        if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false"))
            return registry.getPrefix() + "You must enter a valid boolean.";
        
        boolean premium = args[2].equalsIgnoreCase("true");
        
        registry.setPremium(uuid, premium);
        
        return registry.getPrefix() + Bukkit.getOfflinePlayer(uuid).getName() + " is now a " + (premium ? "premium" : "normal") + " account.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
