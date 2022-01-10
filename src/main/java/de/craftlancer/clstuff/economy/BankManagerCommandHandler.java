package de.craftlancer.clstuff.economy;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.CommandHandler;

public class BankManagerCommandHandler extends CommandHandler {
    public BankManagerCommandHandler(CLStuff plugin, BankManager manager) {
        super(plugin);
        
        registerSubCommand("addMythicMob", new BankManagerAddMythicMobCommand(plugin, manager));
        registerSubCommand("removeMythicMob", new BankManagerRemoveMythicMobCommand(plugin, manager));
    }
}
