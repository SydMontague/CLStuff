package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CitizenSetFunctionCommand extends SubCommandHandler {
    public CitizenSetFunctionCommand(Plugin plugin) {
        super("clstuff.citizenset.admin", plugin, true, 1);
        
        registerSubCommand("add", new CitizenSetFunctionAddCommand(plugin));
        registerSubCommand("remove", new CitizenSetFunctionRemoveCommand(plugin));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
