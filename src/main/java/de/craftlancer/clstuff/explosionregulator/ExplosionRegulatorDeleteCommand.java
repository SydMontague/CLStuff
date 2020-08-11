package de.craftlancer.clstuff.explosionregulator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorDeleteCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorDeleteCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.delete", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 2)
            return "Not enough arguments. <id>";
        
        String id = args[1];
        
        if(!getRegulator().hasItemGroup(id))
            return "This ID isn't taken.";
        
        if(getRegulator().removeItemGroup(id))
            return "Group successfully removed.";
        else
            return "Couldn't remove group.";
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
