package de.craftlancer.clstuff.arena;

import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;

public class ArenaCommand extends CommandHandler {

    public ArenaCommand(Plugin plugin, ArenaGUI arena) {
        super(plugin);
        
        registerSubCommand("create", new ArenaCreateCommand(plugin));
        registerSubCommand("reload", new ArenaReloadCommand(plugin, arena));
    }
    
}
