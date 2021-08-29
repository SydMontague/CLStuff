package de.craftlancer.clstuff.deathmessages;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.CommandHandler;

public class DeathMessageCommandHandler extends CommandHandler {
    public DeathMessageCommandHandler(CLStuff plugin) {
        super(plugin);
        
        registerSubCommand("reload", new DeathMessageReloadCommand(plugin));
    }
}
