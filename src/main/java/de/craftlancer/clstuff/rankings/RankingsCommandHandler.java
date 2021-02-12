package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RankingsCommandHandler extends CommandHandler {
    private CLStuff plugin;
    private Rankings rankings;
    
    public RankingsCommandHandler(CLStuff plugin, Rankings rankings) {
        super(plugin);
        
        this.rankings = rankings;
        this.plugin = plugin;
        
        registerSubCommand("reward", new RankingsRewardCommand(plugin, rankings));
        registerSubCommand("nextMilestone", new RankingsNextMilestoneCommand(plugin, rankings));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || !getCommands().keySet().contains(args[0]))
            return rankings.onCommand(sender, cmd, label, args);
        else
            return super.onCommand(sender, cmd, label, args);
    }
}
