package de.craftlancer.clstuff.squest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PotionEffectReward implements QuestReward {
    private PotionEffectType type;
    private int level;
    private int duration;
    private RewardDistributionType distribution;
    private int distrX = 0;
    
    public PotionEffectReward(PotionEffectType type, int level, int duration, RewardDistributionType distribution, int distrX) {
        this.type = type;
        this.level = level;
        this.duration = duration;
        this.distribution = distribution;
        this.distrX = distrX;
    }
    
    public PotionEffectReward(Map<String, Object> map) {
        type = PotionEffectType.getByName(map.get("type").toString());
        level = (int) map.getOrDefault("level", 0);
        duration = (int) map.getOrDefault("duration", 0);
        distribution = RewardDistributionType.valueOf(map.get("distribution").toString());
        distrX = (int) map.getOrDefault("distrX", 0);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type.getName());
        map.put("level", level);
        map.put("duration", duration);
        map.put("distribution", distribution.name());
        map.put("distrX", distrX);
        return map;
    }
    
    @Override
    public void questCompleted(Quest quest) {
        if (distribution == RewardDistributionType.EVERYONE_ONLINE)
            Bukkit.getOnlinePlayers().forEach(a -> a.addPotionEffect(type.createEffect(duration, level), true));
    }
    
    @Override
    public void rewardPlayer(Quest quest, Player p) {
        int totalPoints = quest.getRequirements().stream().map(a -> a.getTargetAmount() * a.getWeight()).collect(Collectors.summingInt(Integer::intValue));
        int playerPoints = quest.getRequirements().stream().map(a -> a.getContribution().getOrDefault(p.getUniqueId(), 0) * a.getWeight())
                                .collect(Collectors.summingInt(Integer::intValue));
        
        switch (distribution) {
            case DONATED_ABOVE:
                if (totalPoints * distrX / 100 < playerPoints)
                    p.addPotionEffect(type.createEffect(duration, level), true);
                break;
            case DONATION_SHARE:
            case EVERY_DONATOR:
                if (playerPoints != 0)
                    p.addPotionEffect(type.createEffect(duration, level), true);
                break;
            case MOST_DONATED:
                Map<UUID, Integer> ranking = new HashMap<>();
                quest.getRequirements().forEach(a -> a.getContribution().forEach((b, c) -> ranking.merge(b, c * a.getWeight(), (d, e) -> d + e)));

                @SuppressWarnings("unchecked") 
                boolean isEligable = ranking.entrySet().stream().sorted(Comparator.comparingInt(a -> ((Entry<UUID, Integer>) a).getValue()).reversed()).limit(distrX)
                                            .anyMatch(a -> a.getKey().equals(p.getUniqueId()));
                
                if(isEligable) 
                    p.addPotionEffect(type.createEffect(duration, level), true);
                break;
            default:
                break;
        }
    }
    
    @Override
    public String getType() {
        return "potion";
    }

    @Override
    public BaseComponent getComponent() {
        BaseComponent base = new TextComponent(type.getName());
        base.addExtra(" ");
        base.addExtra(Integer.toString(level));
        base.addExtra(" ");
        base.addExtra(Integer.toString(duration));
        return base;
    }
}
