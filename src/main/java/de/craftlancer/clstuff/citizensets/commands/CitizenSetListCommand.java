package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSetsListener;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CitizenSetListCommand extends SubCommand {
    public CitizenSetListCommand(Plugin plugin) {
        super("", plugin, true);
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return null;
        
        Player player = (Player) commandSender;
        
        CitizenSetsListener.getInstance().getGui().display(player);
        
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
