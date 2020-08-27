package de.craftlancer.clstuff.heroes;

import de.craftlancer.core.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HeroesCleanLocationsCommand extends SubCommand {
    
    private Heroes heroes;
    
    public HeroesCleanLocationsCommand(Plugin plugin, Heroes heroes) {
        super("clstuff.heroes.admin", plugin, true);
        
        this.heroes = heroes;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        heroes.getHeroesLocations().forEach(heroLocation -> {
            heroLocation.getSignLocations().removeIf(signLocation -> !MaterialUtil.isSign(signLocation.getBlock().getType()));
            heroLocation.getDisplayLocations().removeIf(displayLocation -> !MaterialUtil.isBanner(displayLocation.getBlock().getType()) && !MaterialUtil.isHead(displayLocation.getBlock().getType()));
        });
        
        return ChatColor.translateAlternateColorCodes('&', Heroes.Config.PREFIX + "&aSuccessfully cleaned locations.");
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
