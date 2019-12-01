package de.craftlancer.clstuff.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

import de.craftlancer.core.command.SubCommand;

public class CCHelpCommand extends SubCommand {
    
    private Consumer<CommandSender> function;
    
    public CCHelpCommand(Plugin plugin, Consumer<CommandSender> function) {
        super("", plugin, true);
        this.function = function;
    }

    @Override
    protected String execute(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        function.accept(arg0);
        return null;
    }
    
    @Override
    public void help(CommandSender arg0) {
    }
    
}
