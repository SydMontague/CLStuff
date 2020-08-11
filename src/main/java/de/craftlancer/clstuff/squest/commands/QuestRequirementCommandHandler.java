package de.craftlancer.clstuff.squest.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.command.SubCommandHandler;

public class QuestRequirementCommandHandler extends SubCommandHandler {
    
    public QuestRequirementCommandHandler(Plugin plugin, ServerQuests quest) {
        super("clstuff.squest.requirements", plugin, true, 1);
        
        registerSubCommand("add", new QuestRequirementAddCommand(plugin, quest));
        registerSubCommand("list", new QuestRequirementListCommand(plugin, quest));
        registerSubCommand("remove", new QuestRequirementRemoveCommand(plugin, quest));
    }

    @Override
    public void help(CommandSender arg0) {
        // not implemented
    }
    
}
