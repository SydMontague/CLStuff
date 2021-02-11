package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommandHandler;
import org.bukkit.command.CommandSender;

public class RankingsRewardCommand extends SubCommandHandler {
    public RankingsRewardCommand(CLStuff plugin, Rankings rankings) {
        super("clstuff.admin", plugin, false, 1);
        
        registerSubCommand("add", new RankingsRewardAddCommand(plugin, rankings));
        registerSubCommand("remove", new RankingsRewardRemoveCommand(plugin, rankings));
        registerSubCommand("removeAll", new RankingsRewardRemoveAllCommand(plugin, rankings));
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
