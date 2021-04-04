package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class CustomBlockRegistryAddCommand extends SubCommandHandler {
    
    public CustomBlockRegistryAddCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false, 2);
        
        registerSubCommand("noteblock", new CustomBlockRegistryAddNoteBlockCommand(plugin, registry));
        registerSubCommand("tripwire", new CustomBlockRegistryAddTripwireCommand(plugin, registry));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
