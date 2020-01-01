package de.craftlancer.clstuff.squest.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.command.SubCommandHandler;

public class QuestRewardCommandHandler extends SubCommandHandler {

    public QuestRewardCommandHandler(Plugin plugin, ServerQuests quest) {
        super("clstuff.squest.reward", plugin, true, 1);
        
        registerSubCommand("add", new QuestRewardAddCommand(plugin, quest));
        registerSubCommand("list", new QuestRewardListCommand(plugin, quest));
        registerSubCommand("remove", new QuestRewardRemoveCommand(plugin, quest));
    }

    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
    
}
