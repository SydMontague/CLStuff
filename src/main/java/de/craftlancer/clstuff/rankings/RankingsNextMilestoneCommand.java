package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.rewards.Reward;
import de.craftlancer.clstuff.rewards.RewardsManager;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public class RankingsNextMilestoneCommand extends SubCommand {
    private Rankings rankings;
    
    public RankingsNextMilestoneCommand(CLStuff plugin, Rankings rankings) {
        super("", plugin, false);
        
        this.rankings = rankings;
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You do not have access to this command!");
            return null;
        }
        
        Player player = (Player) sender;
        
        double score = rankings.getScore(player);
        
        Optional<String> optionalKey = rankings.getRewardMap().entrySet().stream().filter(e -> e.getKey() > score).map(Map.Entry::getValue).findFirst();
        
        if (!optionalKey.isPresent()) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "There are no more rewards for you to get! Maybe ask an admin to make more? :)");
            return null;
        }
        
        Optional<Reward> optionalReward = RewardsManager.getInstance().getReward(optionalKey.get());
        
        if (!optionalReward.isPresent()) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "Error: the reward doesn't exist. Please report this error to an admin!");
            return null;
        }
        
        player.spigot().sendMessage(optionalReward.get().toBaseComponent());
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
