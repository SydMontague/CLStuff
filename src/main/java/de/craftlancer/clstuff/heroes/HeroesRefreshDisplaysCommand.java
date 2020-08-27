package de.craftlancer.clstuff.heroes;

import de.craftlancer.core.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HeroesRefreshDisplaysCommand extends SubCommand {
    
    private Heroes heroes;
    
    public HeroesRefreshDisplaysCommand(Plugin plugin, Heroes heroes) {
        super("clstuff.heroes.admin", plugin, false);
        
        this.heroes = heroes;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        new BaltopCalculateRunnable(heroes).runTaskAsynchronously(heroes.getPlugin());
        return ChatColor.translateAlternateColorCodes('&', Heroes.Config.PREFIX + "&aRefreshing displays... please wait.");
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
