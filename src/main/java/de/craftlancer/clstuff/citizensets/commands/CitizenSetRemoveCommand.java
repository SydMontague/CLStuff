package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetsListener;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CitizenSetRemoveCommand extends SubCommand {
    public CitizenSetRemoveCommand(Plugin plugin) {
        super("clstuff.citizenset.admin", plugin, true);
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], CitizenSetsListener.getInstance().getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return null;
        
        if (args.length < 2)
            return CitizenSetsListener.CC_PREFIX + "You must specify an id!";
        
        CitizenSetsListener.getInstance().removeCitizenSet(args[1]);
        
        return CitizenSetsListener.CC_PREFIX + ChatColor.GREEN + "You have removed all sets going by the id of '" + args[1] + "'.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
