package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class QuestProgressCommand extends QuestCommand {
    
    public QuestProgressCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.progress", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§2You are not allowed to use this command.";
        
        if (args.length < 2)
            return "§2Yor must specify a name of the quest.";
        
        String name = args[1];
        
        if (!getQuests().hasQuest(name))
            return "§2A quest with this name doesn't exist.";
        
        Quest quest = getQuests().getQuest(name).get();
        
        sender.sendMessage("§2Name: " + quest.getName());
        sender.sendMessage("§2Points: " + quest.getCurrentPoints() + " / " + quest.getRequiredPoints());
        sender.sendMessage("§2Item - Remaining - Weight");
        quest.getRequirements().forEach(a -> {
            BaseComponent item = new TextComponent(a.getItem().getType().name());
            item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {
                    new TextComponent(org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(a.getItem()).save(new NBTTagCompound()).toString()) }));
            
            BaseComponent comp = new TextComponent("§2");
            comp.addExtra(item);
            comp.addExtra(" §r");
            comp.addExtra(Integer.toString(a.getCurrentAmount()));
            comp.addExtra(" / ");
            comp.addExtra(Integer.toString(a.getTargetAmount()));
            comp.addExtra(" - ");
            comp.addExtra(Integer.toString(a.getWeight()));
            
            sender.spigot().sendMessage(comp);
        });
        
        return null;
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return getQuests().getQuests().stream().map(Quest::getName).collect(Collectors.toList());
        if (args.length == 2)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                              .collect(Collectors.toList());
        
        return Collections.emptyList();
    }
}
