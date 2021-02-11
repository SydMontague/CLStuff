package de.craftlancer.clstuff.rewards;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RewardsAddCommand extends SubCommand {
    
    private RewardsManager manager;
    
    public RewardsAddCommand(Plugin plugin, RewardsManager manager) {
        super("clstuff.admin", plugin, false);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], manager.getRegistered().stream().map(RewardRegisterable::getKey).collect(Collectors.toList()));
        if (args.length == 3)
            return Collections.singletonList("key");
        if (args.length == 4)
            return Utils.getMatches(args[3], Arrays.asList("true", "false"));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 2) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a reward manager for the reward");
            return null;
        }
        
        if (args.length < 3) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a key for the reward");
            return null;
        }
        
        if (args.length < 4) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a boolean for the announcement");
            return null;
        }
        
        Optional<RewardRegisterable> optional = manager.getRegisterable(args[1]);
        
        if (!optional.isPresent()) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a valid reward manager (use tab complete).");
            return null;
        }
        
        String key = args[2];
        
        if (manager.getRewards().stream().anyMatch(r -> r.getKey().equals(key))) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "A reward already exists with this key.");
            return null;
        }
        
        manager.addReward(optional.get(), key, Boolean.parseBoolean(args[3]));
        
        MessageUtil.sendMessage(manager, sender, MessageLevel.SUCCESS, "Successfully added reward with key " + ChatColor.DARK_GREEN + key);
        
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
