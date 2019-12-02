package de.craftlancer.clstuff.squest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestCreateCommand extends QuestCommand {
    
    public QuestCreateCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.create", plugin, false, quests);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You are not allowed to use this command.";
        
        if(args.length < 2)
            return "Yor must specify a name for the quest.";
        
        String name = args[1];
        
        if(getQuests().hasQuest(name))
            return "A quest with this name already exists.";
        
        Player p = (Player) sender;
        p.setMetadata(ServerQuests.METADATA_KEY, new FixedMetadataValue(getPlugin(), name));
        return "Right click a chest to create a server quest based on it.";
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
