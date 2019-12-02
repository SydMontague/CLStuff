package de.craftlancer.clstuff.squest.commands;

import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.command.SubCommand;

public abstract class QuestCommand extends SubCommand {
    
    private ServerQuests quests;
    
    public QuestCommand(String permission, Plugin plugin, boolean console, ServerQuests quests) {
        super(permission, plugin, console);
        this.quests = quests;
    }

    public ServerQuests getQuests() {
        return quests;
    }
}
