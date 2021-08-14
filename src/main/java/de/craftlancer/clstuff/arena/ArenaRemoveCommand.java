package de.craftlancer.clstuff.arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public class ArenaRemoveCommand extends SubCommand {

    public ArenaRemoveCommand(Plugin plugin) {
        super("clstuff.arena.remove", plugin, false);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command!";
        
        ((Player) sender).setMetadata("arena.remove", new FixedMetadataValue(getPlugin(), ""));
        
        return "Right click the block that shall be the GUI.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // not implemented
    }
    
}
