package de.craftlancer.clstuff.premium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ModelTokenAddCommand extends ModelTokenSubCommand {
    
    public ModelTokenAddCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.add", plugin, false, token);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        Player player = (Player) sender;
        ItemStack token = player.getInventory().getItemInMainHand().clone();
        ItemStack item = player.getInventory().getItemInOffHand().clone();
        
        if(token.getType().isAir() || item.getType().isAir())
            return "You must hold an item in both hands.";
        
        if(getToken().addItem(token, item) == null)
            return "Token successfully added.";
        else
            return "Token successfully replaced.";
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
