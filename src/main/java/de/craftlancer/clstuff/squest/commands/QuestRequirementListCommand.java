package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.QuestRequirement;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestRequirementListCommand extends QuestCommand {
    
    public QuestRequirementListCommand(Plugin plugin, ServerQuests quests) {
        super("", plugin, false, quests);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "§2You are not allowed to use this command.";

        if(args.length < 3)
            return "§2Yor must specify the name of the quest and an amount.";
        
        String name = args[2];
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if(!quest.isPresent())
            return "§2A quest with this name doesn't exist.";
        
        sender.sendMessage("§2ID - Item - Amount - Action");
        int i = 0;
        for (QuestRequirement a : quest.get().getRequirements()) {
            BaseComponent delete = new TextComponent("§2[Delete]");
            delete.setColor(ChatColor.RED);
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/squest requirement remove " + name + " " + i));
            
            BaseComponent comp = new TextComponent("§2" + Integer.toString(i++));
            comp.addExtra(" - ");
            comp.addExtra(Utils.getItemComponent(a.getItem()));
            comp.addExtra(" - ");
            comp.addExtra(Integer.toString(a.getTargetAmount()));
            comp.addExtra(" | ");
            comp.addExtra(delete);
            
            sender.spigot().sendMessage(comp);
        }
        
        return null;
    }
    
    @Override
    public void help(CommandSender arg0) {
        // not implemented
    }

    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return getQuests().getQuests().stream().map(Quest::getName).collect(Collectors.toList());
        if (args.length == 3)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[2].toLowerCase()))
                              .collect(Collectors.toList());
        
        return Collections.emptyList();
    }
}
