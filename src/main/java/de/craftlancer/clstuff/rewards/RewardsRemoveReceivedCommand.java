package de.craftlancer.clstuff.rewards;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RewardsRemoveReceivedCommand extends SubCommand {
    
    private RewardsManager manager;
    
    public RewardsRemoveReceivedCommand(Plugin plugin, RewardsManager manager) {
        super("clstuff.admin", plugin, false);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], manager.getRewards().stream().map(Reward::getKey).collect(Collectors.toList()));
        if (args.length == 3) {
            Optional<Reward> optional = manager.getReward(args[1]);
            if (optional.isPresent())
                return Utils.getMatches(args[2], optional.get().getReceived().stream().map(UUID::toString).collect(Collectors.toList()));
        }
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
        
        if (args.length < 3) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a player UUID to remove from.");
            return null;
        }
        
        Optional<Reward> optional = manager.getReward(args[1]);
        
        if (!optional.isPresent()) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "A reward with this key does not exist.");
            return null;
        }
        
        UUID uuid;
        try {
            uuid = UUID.fromString(args[2]);
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a valid player UUID.");
            return null;
        }
        
        optional.get().removeReceived(uuid);
        
        MessageUtil.sendMessage(manager, sender, MessageLevel.SUCCESS, "Successfully removed UUID from received.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
