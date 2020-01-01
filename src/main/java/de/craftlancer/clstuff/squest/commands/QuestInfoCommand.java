package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class QuestInfoCommand extends QuestCommand {
    
    public QuestInfoCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.info", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§eYou are not allowed to use this command.";
        
        if (args.length < 2)
            return "§eYor must specify a name of the quest.";
        
        String name = args[1];
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if (!quest.isPresent())
            return "§eA quest with this name doesn't exist.";
        
        
        Location loc = quest.get().getChestLocation();
        
        sender.sendMessage("§eName: §r" + quest.get().getName());
        sender.sendMessage("§eDescription: §r" + quest.get().getDescription());
        sender.sendMessage("§eState: §r" + quest.get().getState());
        sender.sendMessage("§eRequired Points: §r" + quest.get().getRequiredPoints());
        sender.sendMessage("§eCurrent Points: §r" + quest.get().getCurrentPoints());
        sender.sendMessage(String.format("§eLocation: §r%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        
        sender.sendMessage("§eItem - Progress");
        quest.get().getRequirements().forEach(a -> {
            BaseComponent item = new TextComponent(a.getItem().getType().name());
            item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {
                    new TextComponent(org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(a.getItem()).save(new NBTTagCompound()).toString()) }));
            
            BaseComponent comp = new TextComponent("§e");
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
