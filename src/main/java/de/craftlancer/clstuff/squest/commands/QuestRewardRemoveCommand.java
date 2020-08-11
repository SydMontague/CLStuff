package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestRewardRemoveCommand extends QuestCommand {
    
    public QuestRewardRemoveCommand(Plugin plugin, ServerQuests quests) {
        super("", plugin, false, quests);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "§2You are not allowed to use this command.";

        if(args.length < 4)
            return "§2Yor must specify the name of the quest and an index.";
        
        String name = args[2];
        int index = Integer.parseInt(args[3]);
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if(!quest.isPresent())
            return "§2A quest with this name doesn't exist.";
        
        if(quest.get().removeReward(index)) {
            getQuests().save();
            return "§2Reward successfully removed.";
        }
        else
            return "§2Couldn't remove reward.";
    }
    
    @Override
    public void help(CommandSender arg0) {
        // not implemented
    }

    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return getQuests().getQuests().stream().map(Quest::getName).collect(Collectors.toList());
        if (args.length == 3)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[2].toLowerCase()))
                              .collect(Collectors.toList());
        
        return Collections.emptyList();
    }
}
