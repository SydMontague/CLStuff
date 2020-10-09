package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class CitizenSetCommandHandler extends CommandHandler {
    public CitizenSetCommandHandler(Plugin plugin, CitizenSetsManager csets) {
        super(plugin);
        
        registerSubCommand("add", new CitizenSetAddCommand(plugin, csets));
        registerSubCommand("remove", new CitizenSetRemoveCommand(plugin, csets));
        registerSubCommand("list", new CitizenSetListCommand(plugin, csets));
        registerSubCommand("setIcon", new CitizenSetSetIconCommand(plugin, csets));
        registerSubCommand("function", new CitizenSetFunctionCommand(plugin, csets));
    }
}
