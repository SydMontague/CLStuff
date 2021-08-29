package de.craftlancer.clstuff.deathmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathMessageReloadCommand extends SubCommand {
    
    private CLStuff plugin;
    
    public DeathMessageReloadCommand(CLStuff plugin) {
        super("clstuff.admin", plugin, true);
        
        this.plugin = plugin;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return "You do not have access to this command.";
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("clstuff.admin"))
            return DeathMessageSettings.PREFIX + "You do not have permission to use this command.";
        
        DeathMessageSettings.load(plugin);
        
        return ChatColor.translateAlternateColorCodes('&', DeathMessageSettings.PREFIX + "&aReloaded death messages.");
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
