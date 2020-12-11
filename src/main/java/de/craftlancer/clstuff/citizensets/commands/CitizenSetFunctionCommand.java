package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CitizenSetFunctionCommand extends SubCommandHandler {
    public CitizenSetFunctionCommand(Plugin plugin, CitizenSetsManager csets) {
        super(CLStuff.getAdminPermission(), plugin, true, 1);
        
        registerSubCommand("add", new CitizenSetFunctionAddCommand(plugin, csets));
        registerSubCommand("remove", new CitizenSetFunctionRemoveCommand(plugin, csets));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
