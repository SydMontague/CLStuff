package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetFunction;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.ParticleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitizenSetFunctionAddCommand extends SubCommand {
    
    private CitizenSetsManager csets;
    
    public CitizenSetFunctionAddCommand(Plugin plugin, CitizenSetsManager csets) {
        super("clstuff.citizenset.admin", plugin, false);
        
        this.csets = csets;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        //List of all citizensets
        if (args.length == 3)
            return Utils.getMatches(args[2], csets.getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        
        if (args.length == 4)
            return Utils.getMatches(args[3], Arrays.stream(CitizenSetFunction.FunctionType.values()).map(Enum::name).collect(Collectors.toList()));
        if (args.length == 5)
            return Collections.singletonList("id");
        if (args.length == 6 && args[1].equalsIgnoreCase("add") && args[3].toUpperCase().contains("PARTICLE"))
            return Utils.getMatches(args[5], Arrays.asList("AQUA", "BLACK", "BLUE", "FUCHSIA", "GRAY", "GREEN", "LIME",
                    "MAROON", "NAVY", "OLIVE", "ORANGE", "PURPLE", "RED", "SILVER", "TEAL", "WHITE", "YELLOW"));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!checkSender(commandSender))
            return CitizenSetsManager.CC_PREFIX + "§cYou do not have permission to use this command.";
        
        if (args.length < 5)
            return CitizenSetsManager.CC_PREFIX + "Please specify all necessary arguments.";
        if (args[1].equalsIgnoreCase("add") && args.length < 6 && args[3].toUpperCase().contains("PARTICLE"))
            return CitizenSetsManager.CC_PREFIX + "You must specify a color of particle to use.";
        
        Optional<CitizenSet> optional = csets.getCitizenSets().stream().filter(set -> set.getId().equals(args[2])).findFirst();
        
        if (!optional.isPresent())
            return CitizenSetsManager.CC_PREFIX + "You must specify a valid set id!";
        
        if (optional.get().getFunctions().stream().anyMatch(f -> f.getId().equals(args[4])))
            return CitizenSetsManager.CC_PREFIX + ChatColor.YELLOW + "There is a function in this set that is already using this id!";
        
        Color color = null;
        if (args[1].equalsIgnoreCase("add") && args[3].toUpperCase().contains("PARTICLE")) {
            color = ParticleUtil.getColorFromString(args[5]);
        }
        CitizenSetFunction function;
        
        try {
            if (color == null)
                function = CitizenSetFunction.FunctionType.getFunction(args[3], args[4]);
            else
                function = CitizenSetFunction.FunctionType.getFunction(args[3], args[4], color);
        } catch (NullPointerException e) {
            return CitizenSetsManager.CC_PREFIX + "You must specify a valid function type.";
        }
        
        optional.get().addFunction(function);
        return CitizenSetsManager.CC_PREFIX + ChatColor.GREEN + "Function added.";
        
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
