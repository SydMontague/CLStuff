package de.craftlancer.clstuff.squest.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import de.craftlancer.clstuff.squest.BroadcastReward;
import de.craftlancer.clstuff.squest.CommandReward;
import de.craftlancer.clstuff.squest.ItemReward;
import de.craftlancer.clstuff.squest.PotionEffectReward;
import de.craftlancer.clstuff.squest.Quest;
import de.craftlancer.clstuff.squest.RewardDistributionType;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.Utils;
import net.md_5.bungee.api.ChatColor;

///squest reward add <quest> <type> <distribution key> <extra data>
public class QuestRewardAddCommand extends QuestCommand {
    
    public QuestRewardAddCommand(Plugin plugin, ServerQuests quests) {
        super("clstuff.squest.reward.add", plugin, true, quests);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§2You are not allowed to use this command.";
        
        if (args.length < 5)
            return "§2Yor must specify a name of the quest, a reward type and a distribution key.";
        
        String name = args[2];
        String rewardType = args[3];
        String distributionKey = args[4];
        String[] tmp = distributionKey.split(":");
        
        RewardDistributionType distributionType = RewardDistributionType.getByName(tmp[0]);
        int x = tmp.length == 2 ? Integer.parseInt(tmp[1]) : 0;
        
        if(distributionType == null)
            return "You must specify a valid distribution type.";
        
        Optional<Quest> quest = getQuests().getQuest(name);
        
        if (!quest.isPresent())
            return "§2A quest with this name doesn't exist.";
        
        switch (rewardType.toLowerCase()) {
            case "command":
                if (args.length < 6)
                    return "§2You must specify a command string to use.";
                
                quest.get().addReward(new CommandReward(args[5], distributionType, x));
                break;
            case "broadcast":
                if (args.length < 6)
                    return "§2You must specify a broadcast string to use.";
                
                String message = ChatColor.translateAlternateColorCodes('&', args[5]);
                boolean discord = args.length >= 7 ? Boolean.parseBoolean(args[6]) : false;
                
                quest.get().addReward(new BroadcastReward(message, discord));
                break;
            case "potion":
                if(args.length < 8)
                    return "§2You must specify an effect type, a level and a duration.";
                
                PotionEffectType type = PotionEffectType.getByName(args[5]);
                int level = Integer.parseInt(args[6]);
                int duration = Integer.parseInt(args[7]);
                quest.get().addReward(new PotionEffectReward(type, level, duration, distributionType, x));
                break;
            case "item":
                if(args.length < 6)
                    return "§2You must specify an amount";
                
                Player p = (Player) sender;
                ItemStack item = p.getInventory().getItemInMainHand();
                
                if(item.getType().isAir())
                    return "§2You must have an item in hand to add it as a reward.";
                
                int amount = Integer.parseInt(args[5]);
                quest.get().addReward(new ItemReward(item, amount, distributionType, x));
                break;
            default:
                return "§2A reward type with this name doesn't exist.";
        }
        
        getQuests().save();
        return "§2Reward added.";
    }
    
    @Override
    public void help(CommandSender arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return getQuests().getQuests().stream().map(Quest::getName).filter(a -> a.toLowerCase().startsWith(args[2].toLowerCase()))
                              .collect(Collectors.toList());
        if (args.length == 4)
            return Utils.getMatches(args[3], new String[] { "command", "broadcast", "item", "potion" });
        if (args.length == 5)
            return Utils.getMatches(args[4], new String[] { "EVERYONE_ONLINE", "MOST_DONATED:", "DONATED_ABOVE:", "EVERY_DONATOR", "DONATION_SHARE" });
        
        switch(args[3]) {
            case "broadcast":
                if(args.length == 7)
                    return Utils.getMatches(args[6], new String[] { "true", "false" });
                break;
            case "potion":
                if(args.length == 6) {
                    return Utils.getMatches(args[5], Arrays.stream(PotionEffectType.values()).map(a -> a.getName()).collect(Collectors.toList()));
                }
                break;
            default:
                break;
        }
        
        return Collections.emptyList();
    }
}
