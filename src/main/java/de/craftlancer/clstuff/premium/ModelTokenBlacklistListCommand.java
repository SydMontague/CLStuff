package de.craftlancer.clstuff.premium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ModelTokenBlacklistListCommand extends ModelTokenSubCommand {
    
    public ModelTokenBlacklistListCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.blacklist.list", plugin, false, token);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You're not allowed to use this command.";
        
        getToken().getBlacklist().forEach((a, b) -> sender.sendMessage(a.name() + " | " + String.join(" ", Utils.toString(b))));
        
        return null;
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
