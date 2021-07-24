package de.craftlancer.clstuff.navigation;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.navigation.NavigationManager;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class NavigationSetCommand extends SubCommand {
    
    private CLStuff plugin;
    private NavigationManager manager;
    
    public NavigationSetCommand(CLStuff plugin, NavigationManager manager) {
        super("", plugin, false);
        
        this.plugin = plugin;
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Collections.singletonList("<x>");
        
        if (args.length == 3)
            return Collections.singletonList("<y>");
        
        if (args.length == 4)
            return Collections.singletonList("<z>");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 4) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter three coordinates.");
            return null;
        }
        
        double x, y, z;
        
        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter valid coordinates.");
            return null;
        }
        
        manager.register((Player) sender,
                new NavigationManager.NavigationGoal("§6Continue to location.",
                        new Location(((Player) sender).getWorld(), x, y, z), manager));
        MessageUtil.sendMessage(manager, sender, MessageLevel.SUCCESS, "Navigation set.");
        return null;
    }
    
    @Override
    public void help(CommandSender sender) {
    
    }
}
