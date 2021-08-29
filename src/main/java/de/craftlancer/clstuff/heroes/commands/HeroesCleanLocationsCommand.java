package de.craftlancer.clstuff.heroes.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MaterialUtil;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
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
        if (!checkSender(commandSender)) {
            MessageUtil.sendMessage(heroes, commandSender, MessageLevel.WARNING, "You do not have access to this command.");
            return null;
        }
        heroes.getHeroesLocations().forEach(heroLocation -> {
            heroLocation.getSignLocations().removeIf(signLocation -> !MaterialUtil.isSign(signLocation.getBlock().getType()));
            heroLocation.getDisplayLocations().removeIf(displayLocation -> !MaterialUtil.isBanner(displayLocation.getBlock().getType()) && !MaterialUtil.isHead(displayLocation.getBlock().getType()));
        });
        
        MessageUtil.sendMessage(heroes, commandSender, MessageLevel.SUCCESS, "Successfully cleaned locations.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
