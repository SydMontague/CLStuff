package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.rewards.Reward;
import de.craftlancer.clstuff.rewards.RewardsManager;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RankingsRewardAddCommand extends SubCommand {
    
    private CLStuff plugin;
    private Rankings rankings;
    
    public RankingsRewardAddCommand(CLStuff plugin, Rankings rankings) {
        super("clstuff.admin", plugin, false);
        
        this.plugin = plugin;
        this.rankings = rankings;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Utils.getMatches(args[2], RewardsManager.getInstance().getRewards().stream().map(Reward::getKey).collect(Collectors.toList()));
        if (args.length == 4)
            return Collections.singletonList("score");
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You do not have access to this command!");
            return null;
        }
        
        if (args.length < 3) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a reward key to assign to this reward!");
            return null;
        }
        
        if (args.length < 4) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a score for this reward to be given at!");
            return null;
        }
        
        Optional<Reward> optional = RewardsManager.getInstance().getReward(args[2]);
        
        if (!optional.isPresent()) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a valid reward key.");
            return null;
        }
        
        int score;
        try {
            score = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You must enter a valid integer.");
            return null;
        }
        
        rankings.addReward(score, optional.get());
        MessageUtil.sendMessage(rankings, sender, MessageLevel.SUCCESS, "Successfully added a reward at §2" + score + "§a points.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
