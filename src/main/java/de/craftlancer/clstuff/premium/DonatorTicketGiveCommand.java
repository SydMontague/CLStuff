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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DonatorTicketGiveCommand extends SubCommand {
    
    private DonatorTicketRegistry registry;
    private CLStuff plugin;
    
    public DonatorTicketGiveCommand(Plugin plugin, DonatorTicketRegistry registry) {
        super(CLStuff.getAdminPermission(), plugin, true);
        
        this.registry = registry;
        this.plugin = (CLStuff) plugin;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        if (args.length == 3)
            return Collections.singletonList("amount");
        
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
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return registry.getPrefix() + "You must enter a valid integer.";
        }
        
        registry.updatePoints(uuid, amount);
        
        return registry.getPrefix() + "§a" + Bukkit.getOfflinePlayer(uuid).getName() + "§7 has been given " + amount + " tickets and now has "
                + registry.getPoints(uuid) + " tickets.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
