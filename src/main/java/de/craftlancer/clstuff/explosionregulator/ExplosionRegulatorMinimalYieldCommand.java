package de.craftlancer.clstuff.explosionregulator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorMinimalYieldCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorMinimalYieldCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.minyield", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 3)
            return "Not enough arguments. <id> <minYield>";
        
        String id = args[1];
        float limit = Utils.parseFloatOrDefault(args[2], -1);

        if(!getRegulator().hasItemGroup(id))
            return "This ID isn't taken.";
        if(limit == -1)
            return "You must specify a minimal yield";
        
        getRegulator().getItemGroup(id).setMinimalYield(limit);
        return "New minimal yield set.";
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
