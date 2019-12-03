package de.craftlancer.clstuff.squest.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.ServerQuests;

public class QuestRequirementAddCommand extends QuestCommand {
    
    public QuestRequirementAddCommand(Plugin plugin, ServerQuests quests) {
        super("", plugin, false, quests);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "§2You are not allowed to use this command.";

        if(args.length < 4)
            return "§2Yor must specify the name of the quest and an amount.";
        
        Player player = (Player) sender;
        String name = args[2];
        int amount = Integer.parseInt(args[3]);
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if(!quest.isPresent())
            return "§2A quest with this name doesn't exist.";
        if(item.getType().isAir())
            return "§2You must hold an item in your main hand.";
        
        item.setAmount(amount);
        quest.get().addItem(item);
        getQuests().save();
        
        return null;
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return getQuests().getQuests().stream().map(Quest::getName).collect(Collectors.toList());
        if (args.length == 3)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                              .collect(Collectors.toList());
        
        return Collections.emptyList();
    }
}
