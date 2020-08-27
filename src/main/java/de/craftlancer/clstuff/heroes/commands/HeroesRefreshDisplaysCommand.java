package de.craftlancer.clstuff.heroes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.runnables.BaltopCalculateRunnable;
import de.craftlancer.core.command.SubCommand;

public class HeroesRefreshDisplaysCommand extends SubCommand {
    
    private Heroes heroes;
    
    public HeroesRefreshDisplaysCommand(Plugin plugin, Heroes heroes) {
        super("clstuff.heroes.admin", plugin, false);
        
        this.heroes = heroes;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        new BaltopCalculateRunnable(heroes).runTaskAsynchronously(heroes.getPlugin());
        return heroes.getPrefix() + "§aRefreshing displays... please wait.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
