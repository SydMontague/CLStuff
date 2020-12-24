package de.craftlancer.clstuff.heroes.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.MaterialUtil;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HeroesCleanLocationsCommand extends SubCommand {
    
    private Heroes heroes;
    
    public HeroesCleanLocationsCommand(Plugin plugin, Heroes heroes) {
        super(CLStuff.getAdminPermission(), plugin, true);
        
        this.heroes = heroes;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        heroes.getHeroesLocations().forEach(heroLocation -> {
            heroLocation.getSignLocations().removeIf(signLocation -> !MaterialUtil.isSign(signLocation.getBlock().getType()));
            heroLocation.getDisplayLocations().removeIf(displayLocation -> !MaterialUtil.isBanner(displayLocation.getBlock().getType()) && !MaterialUtil.isHead(displayLocation.getBlock().getType()));
        });
        
        return heroes.getPrefix() + "§aSuccessfully cleaned locations.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
