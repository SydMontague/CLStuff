package de.craftlancer.clstuff.squest.commands;

import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.command.CommandHandler;


/*
 * squest desc <name> <desc>
 * squest req add <name> <amount> (takes item from hand)
 * squest req list <name>
 * squest req remove <name> <id>
 * squest reward <name> <value>
 * squest status <name> <value>
 * 
 * squest list
 * squest progress <name>
 * squest info <name>
 */
public class QuestCommandHandler extends CommandHandler {

    public QuestCommandHandler(Plugin plugin, ServerQuests quests) {
        super(plugin);
        
        registerSubCommand("create", new QuestCreateCommand(plugin, quests));
        registerSubCommand("delete", new QuestDeleteCommand(plugin, quests));
        registerSubCommand("reward", new QuestRewardCommandHandler(plugin, quests));
        registerSubCommand("start", new QuestStartCommand(plugin, quests));
        registerSubCommand("description", new QuestDescriptionCommand(plugin, quests));
        registerSubCommand("requirement", new QuestRequirementCommandHandler(plugin, quests));
        registerSubCommand("requiredPoints", new QuestRequiredPointsCommand(plugin, quests));
        
        registerSubCommand("list", new QuestListCommand(plugin, quests));
        registerSubCommand("progress", new QuestProgressCommand(plugin, quests));
        registerSubCommand("info", new QuestInfoCommand(plugin, quests));
    }
    
}
