package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CitizenSetListCommand extends SubCommand {
    
    private CitizenSetsManager csets;
    
    public CitizenSetListCommand(Plugin plugin, CitizenSetsManager csets) {
        super("", plugin, false);
        
        this.csets = csets;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!checkSender(commandSender))
            return CitizenSetsManager.CC_PREFIX + "§cYou do not have permission to use this command.";
        
        Player player = (Player) commandSender;
        
        csets.getGui().display(player);
        
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
