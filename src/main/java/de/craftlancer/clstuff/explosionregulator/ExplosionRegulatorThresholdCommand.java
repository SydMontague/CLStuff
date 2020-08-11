package de.craftlancer.clstuff.explosionregulator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorThresholdCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorThresholdCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.threshold", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 3)
            return "Not enough arguments. <id> <threshold>";
        
        String id = args[1];
        int threshold = Utils.parseIntegerOrDefault(args[2], -1);

        if(!getRegulator().hasItemGroup(id))
            return "This ID isn't taken.";
        if(threshold == -1)
            return "You must specify a threshold";
        
        getRegulator().getItemGroup(id).setThreshold(threshold);
        return "New threshold set.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // not implemented
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 2)
            return Utils.getMatches(args[1], getRegulator().getIds());
        
        return super.onTabComplete(sender, args);
    }
}
