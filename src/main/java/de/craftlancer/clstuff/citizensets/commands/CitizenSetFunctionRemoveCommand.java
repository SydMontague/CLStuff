package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetFunction;
import de.craftlancer.clstuff.citizensets.CitizenSetsListener;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitizenSetFunctionRemoveCommand extends SubCommand {
    public CitizenSetFunctionRemoveCommand(Plugin plugin) {
        super("clstuff.citizenset.admin", plugin, true);
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Utils.getMatches(args[2], CitizenSetsListener.getInstance().getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        
        Optional<CitizenSet> optional = CitizenSetsListener.getInstance().getCitizenSets().stream().filter(set -> set.getId().equals(args[2])).findFirst();
        if (args.length == 4)
            if (optional.isPresent())
                return Utils.getMatches(args[3], optional.get().getFunctions().stream().map(CitizenSetFunction::getId).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return null;
        
        if (args.length < 4)
            return CitizenSetsListener.CC_PREFIX + "Please specify all necessary arguments.";
        
        Optional<CitizenSet> optional = CitizenSetsListener.getInstance().getCitizenSets().stream().filter(set -> set.getId().equals(args[2])).findFirst();
        
        if (!optional.isPresent())
            return CitizenSetsListener.CC_PREFIX + "You must specify a valid set id!";
        
        if (optional.get().getFunctions().stream().noneMatch(f -> !f.getId().equals(args[3])))
            return CitizenSetsListener.CC_PREFIX + ChatColor.YELLOW + "There are no functions using this id.";
        
        optional.get().removeFunction(args[3]);
        return CitizenSetsListener.CC_PREFIX + ChatColor.GREEN + "Function removed.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
