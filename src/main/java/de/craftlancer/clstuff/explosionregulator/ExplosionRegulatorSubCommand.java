package de.craftlancer.clstuff.explosionregulator;

import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public abstract class ExplosionRegulatorSubCommand extends SubCommand {
    
    private ExplosionRegulator regulator;
    
    public ExplosionRegulatorSubCommand(String permission, Plugin plugin, boolean console, ExplosionRegulator regulator) {
        super(permission, plugin, console);
        this.regulator = regulator;
    }
    
    public ExplosionRegulator getRegulator() {
        return regulator;
    }
}
