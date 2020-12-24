package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetFunction;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitizenSetFunctionRemoveCommand extends SubCommand {
    
    private CitizenSetsManager csets;
    
    public CitizenSetFunctionRemoveCommand(Plugin plugin, CitizenSetsManager csets) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.csets = csets;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Utils.getMatches(args[2], csets.getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        
        Optional<CitizenSet> optional = csets.getCitizenSets().stream().filter(set -> set.getId().equals(args[2])).findFirst();
        if (args.length == 4)
            if (optional.isPresent())
                return Utils.getMatches(args[3], optional.get().getFunctions().stream().map(CitizenSetFunction::getId).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!checkSender(commandSender))
            return CitizenSetsManager.CC_PREFIX + "§cYou do not have permission to use this command.";
        
        if (args.length < 4)
            return CitizenSetsManager.CC_PREFIX + "Please specify all necessary arguments.";
        
        Optional<CitizenSet> optional = csets.getCitizenSets().stream().filter(set -> set.getId().equals(args[2])).findFirst();
        
        if (!optional.isPresent())
            return CitizenSetsManager.CC_PREFIX + "You must specify a valid set id!";
        
        if (optional.get().getFunctions().stream().noneMatch(f -> !f.getId().equals(args[3])))
            return CitizenSetsManager.CC_PREFIX + ChatColor.YELLOW + "There are no functions using this id.";
        
        optional.get().removeFunction(args[3]);
        return CitizenSetsManager.CC_PREFIX + ChatColor.GREEN + "Function removed.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
