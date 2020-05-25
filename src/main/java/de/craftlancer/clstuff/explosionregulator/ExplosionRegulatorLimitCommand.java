package de.craftlancer.clstuff.explosionregulator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorLimitCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorLimitCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.setlimit", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 3)
            return "Not enough arguments. <id> <limit>";
        
        String id = args[1];
        int limit = Utils.parseIntegerOrDefault(args[2], -1);

        if(!getRegulator().hasItemGroup(id))
            return "This ID isn't taken.";
        if(limit == -1)
            return "You must specify a limit";
        
        getRegulator().getItemGroup(id).setLimit(limit);
        return "New limit set.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 2)
            return Utils.getMatches(args[1], getRegulator().getIds());
        
        return super.onTabComplete(sender, args);
    }
}
