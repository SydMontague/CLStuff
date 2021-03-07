package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoteBlockRegistryListCommand extends SubCommand {
    
    private NoteBlockRegistry registry;
    
    public NoteBlockRegistryListCommand(CLStuff plugin, NoteBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        registry.getGui().display((Player) sender);
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
