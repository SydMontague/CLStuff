package de.craftlancer.clstuff.explosionregulator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorInfoCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorInfoCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.info", plugin, true, regulator);
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

        ItemGroup group = getRegulator().getItemGroup(id);
        
        sender.sendMessage("§f[§4Craft§fCitizen]§e Explosion Regulator Status");
        sender.sendMessage(String.format("  §eID: %s", id));
        sender.sendMessage(String.format("  §eLimit: %d/%d+%d", group.getCurrentTotal(), group.getLimit(), group.getThreshold()));
        sender.sendMessage(String.format("  §eYield: %f | Min: %f", group.getCurrentYield(), group.getMinimalYield()));
        return null;
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
