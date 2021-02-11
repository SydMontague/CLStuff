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
import java.util.stream.Collectors;

public class RewardsClearAllCommand extends SubCommand {
    
    private RewardsManager manager;
    
    public RewardsClearAllCommand(Plugin plugin, RewardsManager manager) {
        super("clstuff.admin", plugin, false);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], manager.getRegistered().stream().map(RewardRegisterable::getKey).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You do not have access to this command.");
            return null;
        }
        
        if (args.length < 2) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a type for the reward");
            return null;
        }
        
        Optional<RewardRegisterable> optional = manager.getRegisterable(args[1]);
        
        if (!optional.isPresent()) {
            MessageUtil.sendMessage(manager, sender, MessageLevel.INFO, "You must enter a valid reward manager (use tab complete).");
            return null;
        }
        
        manager.clearAll(optional.get());
        
        MessageUtil.sendMessage(manager, sender, MessageLevel.SUCCESS, "Successfully removed all rewards with manager " + optional.get().getKey() + ".");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
