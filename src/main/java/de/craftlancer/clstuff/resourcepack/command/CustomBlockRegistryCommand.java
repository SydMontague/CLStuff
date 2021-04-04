package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class CustomBlockRegistryCommand extends SubCommandHandler {
    public CustomBlockRegistryCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false, 1);
        
        registerSubCommand("add", new CustomBlockRegistryAddCommand(plugin, registry));
        registerSubCommand("list", new CustomBlockRegistryListCommand(plugin, registry));
        
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
