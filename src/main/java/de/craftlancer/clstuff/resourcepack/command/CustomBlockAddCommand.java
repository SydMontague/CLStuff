package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class CustomBlockAddCommand extends SubCommandHandler {
    
    public CustomBlockAddCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false, 1);
        
        //registerSubCommand("noteblock", new CustomBlockRegistryAddNoteBlockCommand(plugin, registry));
        //registerSubCommand("tripwire", new CustomBlockRegistryAddTripwireCommand(plugin, registry));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
