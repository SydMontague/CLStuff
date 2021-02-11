package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RankingsRewardRemoveCommand extends SubCommand {
    
    private CLStuff plugin;
    private Rankings rankings;
    
    public RankingsRewardRemoveCommand(CLStuff plugin, Rankings rankings) {
        super("clstuff.admin", plugin, false);
        
        this.plugin = plugin;
        this.rankings = rankings;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return rankings.getRewardMap().keySet().stream().map(String::valueOf).collect(Collectors.toList());
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 3) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a score for this reward to be removed from.");
            return null;
        }
        
        int score;
        try {
            score = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a valid integer.");
            return null;
        }
        
        if (rankings.removeReward(score))
            MessageUtil.sendMessage(rankings, sender, MessageLevel.SUCCESS, "Successfully removed reward at §2" + score + "§a points.");
        else
            MessageUtil.sendMessage(rankings, sender, MessageLevel.SUCCESS, "A reward at the score you entered does not exist.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
