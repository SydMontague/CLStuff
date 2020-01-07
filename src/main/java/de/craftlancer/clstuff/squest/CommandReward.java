package de.craftlancer.clstuff.squest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class CommandReward implements QuestReward {
    private String command = "";
    
    // TODO put in super class
    private RewardDistributionType distribution;
    private int distrX = 0;
    
    public CommandReward(String command, RewardDistributionType distribution, int distrX) {
        this.command = command;
        this.distribution = distribution;
        this.distrX = distrX;
    }
    
    public CommandReward(Map<String, Object> map) {
        this.command = map.getOrDefault("command", "").toString();
        this.distribution = RewardDistributionType.valueOf(map.get("distribution").toString());
        this.distrX = (int) map.getOrDefault("distrX", 0);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("command", command);
        map.put("distribution", distribution.name());
        map.put("distrX", distrX);
        return map;
    }
    
    @Override
    public void questCompleted(Quest quest) {
        if (distribution == RewardDistributionType.EVERYONE_ONLINE)
            Bukkit.getOnlinePlayers().forEach(a -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command, a.getName())));
    }

    @Override
    public void rewardPlayer(Quest quest, Player p) {
        int totalPoints = quest.getRequirements().stream().map(a -> a.getTargetAmount() * a.getWeight()).collect(Collectors.summingInt(Integer::intValue));
        int playerPoints = quest.getRequirements().stream().map(a -> a.getContribution().getOrDefault(p.getUniqueId(), 0) * a.getWeight())
                                .collect(Collectors.summingInt(Integer::intValue));
        
        switch (distribution) {
            case DONATED_ABOVE:
                if (totalPoints * distrX / 100 < playerPoints)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command, p.getName()));
                break;
            case DONATION_SHARE:
            case EVERY_DONATOR:
                if (playerPoints != 0)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command, p.getName()));
                break;
            case MOST_DONATED:
                Map<UUID, Integer> ranking = new HashMap<>();
                quest.getRequirements().forEach(a -> a.getContribution().forEach((b, c) -> ranking.merge(b, c * a.getWeight(), (d, e) -> d + e)));

                @SuppressWarnings("unchecked") 
                boolean isEligable = ranking.entrySet().stream().sorted(Comparator.comparingInt(a -> ((Entry<UUID, Integer>) a).getValue()).reversed()).limit(distrX)
                                            .anyMatch(a -> a.getKey().equals(p.getUniqueId()));
                
                if(isEligable) 
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command, p.getName()));
                break;
            default:
                break;
        }
    }
    
    @Override
    public String getType() {
        return "command";
    }

    @Override
    public BaseComponent getComponent() {
        BaseComponent base = new TextComponent("[Text]");
        base.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { new TextComponent(command) }));
        return base;
    }
}
