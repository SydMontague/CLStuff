package de.craftlancer.clstuff.rankings;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class RankingsRewardRemoveAllCommand extends SubCommand {
    
    private CLStuff plugin;
    private Rankings rankings;
    
    public RankingsRewardRemoveAllCommand(CLStuff plugin, Rankings rankings) {
        super("clstuff.admin", plugin, false);
        
        this.plugin = plugin;
        this.rankings = rankings;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(rankings, sender, MessageLevel.INFO, "You do not have access to this command!");
            return null;
        }
        
        rankings.clearRewards();
        
        MessageUtil.sendMessage(rankings, sender, MessageLevel.SUCCESS, "Successfully cleared all rewards.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
