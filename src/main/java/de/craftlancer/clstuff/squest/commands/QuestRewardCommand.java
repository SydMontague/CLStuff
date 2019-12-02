package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.BroadcastReward;
import de.craftlancer.clstuff.squest.CommandReward;
import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.Utils;

public class QuestRewardCommand extends QuestCommand {
    
    public QuestRewardCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.description", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You are not allowed to use this command.";
        
        if (args.length < 3)
            return "Yor must specify a name of the quest, a reward type.";
        
        String name = args[1];
        String rewardType = args[2];
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if (!quest.isPresent())
            return "A quest with this name doesn't exist.";
        
        switch (rewardType.toLowerCase()) {
            case "command":
                if (args.length < 4)
                    return "You must specify a command string to use.";
                
                quest.get().setReward(new CommandReward(args[3]));
                break;
            case "broadcast":
                if (args.length < 4)
                    return "You must specify a broadcast string to use.";
                
                quest.get().setReward(new BroadcastReward(args[3]));
                break;
            default:
                return "A reward type with this name doesn't exist.";
        }
        
        getQuests().save();
        return "Reward set.";
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return getQuests().getQuests().stream().map(Quest::getName).collect(Collectors.toList());
        if (args.length == 2)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                              .collect(Collectors.toList());
        if (args.length == 3)
            return Utils.getMatches(args[2], new String[] { "command", "broadcast" });
        
        return Collections.emptyList();
    }
}
