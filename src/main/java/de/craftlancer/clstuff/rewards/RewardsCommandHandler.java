package de.craftlancer.clstuff.rewards;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class RewardsCommandHandler extends CommandHandler {
    public RewardsCommandHandler(Plugin plugin, RewardsManager manager) {
        super(plugin);
        
        registerSubCommand("add", new RewardsAddCommand(plugin, manager));
        registerSubCommand("remove", new RewardsRemoveCommand(plugin, manager));
        registerSubCommand("editor", new RewardsEditorCommand(plugin, manager));
        registerSubCommand("give", new RewardsGiveCommand(plugin, manager));
        registerSubCommand("removeReceived", new RewardsRemoveReceivedCommand(plugin, manager));
    }
}
