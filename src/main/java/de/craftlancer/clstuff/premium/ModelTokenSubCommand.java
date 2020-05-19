package de.craftlancer.clstuff.premium;

import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public abstract class ModelTokenSubCommand extends SubCommand {

    private ModelToken token;
    
    public ModelTokenSubCommand(String permission, Plugin plugin, boolean console, ModelToken token) {
        super(permission, plugin, console);
        this.token = token;
    }
    
    public ModelToken getToken() {
        return token;
    }
    
}
