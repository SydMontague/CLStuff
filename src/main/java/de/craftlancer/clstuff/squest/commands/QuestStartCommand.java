package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.QuestState;
import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestStartCommand extends QuestCommand {
    
    public QuestStartCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.start", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§2You are not allowed to use this command.";
        
        if (args.length < 2)
            return "§2Yor must specify a name of the quest.";
        
        String name = args[1];
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if (!quest.isPresent())
            return "§2A quest with this name doesn't exist.";
        
        if (quest.get().getState() != QuestState.INACTIVE)
            return "§2This quest is not inactive and thus can't be started anymore.";
        
        quest.get().startQuest();
        return "§2Quest started.";
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
        
        return Collections.emptyList();
    }
}
