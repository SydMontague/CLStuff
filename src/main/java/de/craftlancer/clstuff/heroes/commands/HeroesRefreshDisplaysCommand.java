package de.craftlancer.clstuff.heroes.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HeroesRefreshDisplaysCommand extends SubCommand {
    
    private Heroes heroes;
    
    public HeroesRefreshDisplaysCommand(Plugin plugin, Heroes heroes) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.heroes = heroes;
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!checkSender(commandSender)) {
            MessageUtil.sendMessage(heroes, commandSender, MessageLevel.WARNING, "You do not have access to this command.");
            return null;
        }
        
        heroes.refreshDisplays();
        MessageUtil.sendMessage(heroes, commandSender, MessageLevel.SUCCESS, "Refreshing displays. Heads will be updated once every minute.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
