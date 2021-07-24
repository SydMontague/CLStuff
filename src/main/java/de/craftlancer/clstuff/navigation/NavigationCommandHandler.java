package de.craftlancer.clstuff.navigation;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.CommandHandler;

public class NavigationCommandHandler extends CommandHandler {
    public NavigationCommandHandler(CLStuff plugin, NavigationManager manager) {
        super(plugin);
        
        registerSubCommand("set", new NavigationSetCommand(plugin, manager));
        registerSubCommand("stop", new NavigationStopCommand(plugin, manager));
    }
}
