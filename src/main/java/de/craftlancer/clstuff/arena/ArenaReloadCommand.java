package de.craftlancer.clstuff.arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public class ArenaReloadCommand extends SubCommand {
    
    private final ArenaGUI arena;
    
    public ArenaReloadCommand(Plugin plugin, ArenaGUI arena) {
        super("clstuff.arena.reload", plugin, true);
        this.arena = arena;
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command!";
        
        arena.loadConfig();
        return "Arena config reloaded";
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
