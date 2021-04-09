package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.resourcepack.ResourcePackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class CustomBlockListCommand extends SubCommand {
    
    private CustomBlockRegistry registry;
    
    public CustomBlockListCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        
        registry.getGui().display((Player) sender,
                ResourcePackManager.getInstance().getStatus(((Player) sender)) == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED ?
                        "resource" : "default");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
