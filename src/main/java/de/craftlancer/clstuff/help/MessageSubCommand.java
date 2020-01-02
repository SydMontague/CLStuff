package de.craftlancer.clstuff.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.SubCommand;

public class MessageSubCommand extends SubCommand {

    private final String message;
    
    public MessageSubCommand(String permission, Plugin plugin, String text) {
        super(permission, plugin, true);
        this.message = text;
    }

    @Override
    protected String execute(CommandSender sender, Command arg1, String arg2, String[] arg3) {
        return message;
    }
    
    @Override
    public void help(CommandSender arg0) {
    }
    
}
