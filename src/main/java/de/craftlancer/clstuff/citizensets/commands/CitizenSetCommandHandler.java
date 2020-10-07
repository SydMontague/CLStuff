package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class CitizenSetCommandHandler extends CommandHandler {
    public CitizenSetCommandHandler(Plugin plugin) {
        super(plugin);
        
        registerSubCommand("add", new CitizenSetAddCommand(plugin));
        registerSubCommand("remove", new CitizenSetRemoveCommand(plugin));
        registerSubCommand("list", new CitizenSetListCommand(plugin));
        registerSubCommand("setIcon", new CitizenSetSetIconCommand(plugin));
        registerSubCommand("function", new CitizenSetFunctionCommand(plugin));
    }
}
