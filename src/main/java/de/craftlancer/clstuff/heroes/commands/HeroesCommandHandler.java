package de.craftlancer.clstuff.heroes.commands;

import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class HeroesCommandHandler extends CommandHandler {
    public HeroesCommandHandler(Plugin plugin, Heroes heroes) {
        super(plugin);
        
        registerSubCommand("addLocation", new HeroesAddLocationCommand(plugin, heroes));
        registerSubCommand("cleanLocations", new HeroesCleanLocationsCommand(plugin, heroes));
        registerSubCommand("refreshDisplays", new HeroesRefreshDisplaysCommand(plugin, heroes));
    }
}
