package de.craftlancer.clstuff.premium;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommandHandler;

public class ModelTokenBlacklistCommandHandler extends SubCommandHandler {

    public ModelTokenBlacklistCommandHandler(Plugin plugin, int depth, ModelToken token) {
        super("clstuff.modeltoken.blacklist", plugin, true, depth);
        registerSubCommand("add", new ModelTokenBlacklistAddCommand(plugin, token));
        registerSubCommand("remove", new ModelTokenBlacklistRemoveCommand(plugin, token));
        registerSubCommand("list", new ModelTokenBlacklistListCommand(plugin, token));
    }

    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }

}
