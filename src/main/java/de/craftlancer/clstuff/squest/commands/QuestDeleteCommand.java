package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestDeleteCommand extends QuestCommand {
    
    public QuestDeleteCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.delete", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§eYou are not allowed to use this command.";
        
        if (args.length < 2)
            return "§eYor must specify a name of the quest.";
        
        String name = args[1];
        
        if (!getQuests().hasQuest(name))
            return "§eA quest with this name doesn't exist.";
        
        getQuests().removeQuest(name);
        return "§eQuest removed.";
    }
    
    @Override
    public void help(CommandSender arg0) {
        // not implemented
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
