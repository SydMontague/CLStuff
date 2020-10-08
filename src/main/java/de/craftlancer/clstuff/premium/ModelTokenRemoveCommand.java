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
        
        int hash = Utils.parseIntegerOrDefault(args[1], -1);
        
        return getToken().removeTokenByHash(hash) ? "Item removed" : "No item with given hash found.";
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
