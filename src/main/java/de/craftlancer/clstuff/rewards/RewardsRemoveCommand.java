package de.craftlancer.clstuff.rewards;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RewardsRemoveCommand extends SubCommand {
    
    private RewardsManager manager;
    
    public RewardsRemoveCommand(Plugin plugin, RewardsManager manager) {
        super("clstuff.admin", plugin, false);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], manager.getRewards().stream().map(Reward::getKey).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 2) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a key for the reward");
            return null;
        }
        
        String key = args[1];
        
        if (manager.getRewards().stream().noneMatch(r -> r.getKey().equals(key))) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "A reward with this key does not exist.");
            return null;
        }
        
        manager.removeReward(key);
        
        MessageUtil.sendMessage(manager, sender, MessageLevel.SUCCESS, "Successfully removed reward with key " + ChatColor.DARK_GREEN + key);
        
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
