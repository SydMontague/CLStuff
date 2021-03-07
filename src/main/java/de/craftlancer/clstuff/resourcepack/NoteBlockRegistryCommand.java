package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class NoteBlockRegistryCommand extends SubCommandHandler {
    public NoteBlockRegistryCommand(CLStuff plugin, NoteBlockRegistry registry) {
        super("clstuff.admin", plugin, false, 1);
        
        registerSubCommand("add", new NoteBlockRegistryAddCommand(plugin, registry));
        registerSubCommand("list", new NoteBlockRegistryListCommand(plugin, registry));
        
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
