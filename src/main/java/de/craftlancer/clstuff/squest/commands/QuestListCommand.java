package de.craftlancer.clstuff.squest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestListCommand extends QuestCommand {
    
    public QuestListCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.list", plugin, false, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§2You are not allowed to use this command.";
        
        sender.sendMessage("§2Name - #Items");
        getQuests().getQuests().forEach(a -> sender.sendMessage("§2" + a.getName() + " " + a.getRequirements().size() + " " + a.getState()));
        
        return null;
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
