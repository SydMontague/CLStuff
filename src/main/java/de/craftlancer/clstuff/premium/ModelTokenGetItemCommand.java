package de.craftlancer.clstuff.premium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ModelTokenGetItemCommand extends ModelTokenSubCommand {
    
    public ModelTokenGetItemCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.giveitem", plugin, false, token);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You're not allowed to use this command.";
        
        if(args.length < 2)
            return "Not enough arguments.";
        
        int id = Utils.parseIntegerOrDefault(args[1], -1);
        
        ItemStack item = getToken().getItemById(id);
        return ((Player) sender).getInventory().addItem(item).isEmpty() ? "Item given." : "Not enough space for item.";
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
