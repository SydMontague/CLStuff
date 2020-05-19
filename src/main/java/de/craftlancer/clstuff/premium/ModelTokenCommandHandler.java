package de.craftlancer.clstuff.premium;

import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;

public class ModelTokenCommandHandler extends CommandHandler {

    public ModelTokenCommandHandler(Plugin plugin, ModelToken token) {
        super(plugin);
        
        registerSubCommand("add", new ModelTokenAddCommand(plugin, token));
        registerSubCommand("remove", new ModelTokenRemoveCommand(plugin, token));
        registerSubCommand("list", new ModelTokenListCommand(plugin, token));
        registerSubCommand("getitem", new ModelTokenGetItemCommand(plugin, token));
        registerSubCommand("gettoken", new ModelTokenGetTokenCommand(plugin, token));
    }
    
}
