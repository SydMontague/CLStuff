package de.craftlancer.clstuff.premium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ModelTokenRemoveCommand extends ModelTokenSubCommand {
    
    public ModelTokenRemoveCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.remove", plugin, false, token);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You're not allowed to use this command.";

        if(args.length < 2)
            return "Not enough arguments.";
        
        int id = Utils.parseIntegerOrDefault(args[1], -1);
        
        return getToken().removeTokenById(id) ? "Item removed" : "No item with given ID found.";
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
