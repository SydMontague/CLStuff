package de.craftlancer.clstuff.explosionregulator;

import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;

public class ExplosionRegulatorCommandHandler extends CommandHandler {

    public ExplosionRegulatorCommandHandler(Plugin plugin, ExplosionRegulator regulator) {
        super(plugin);
        
        registerSubCommand("info", new ExplosionRegulatorInfoCommand(plugin, regulator));
        registerSubCommand("create", new ExplosionRegulatorCreateCommand(plugin, regulator));
        registerSubCommand("delete", new ExplosionRegulatorDeleteCommand(plugin, regulator));
        registerSubCommand("add", new ExplosionRegulatorAddCommand(plugin, regulator));
        registerSubCommand("remove", new ExplosionRegulatorRemoveCommand(plugin, regulator));
        registerSubCommand("limit", new ExplosionRegulatorLimitCommand(plugin, regulator));
        registerSubCommand("threshold", new ExplosionRegulatorThresholdCommand(plugin, regulator));
        registerSubCommand("minyield", new ExplosionRegulatorMinimalYieldCommand(plugin, regulator));
    }
    
}
