package de.craftlancer.clstuff.squest;

import java.util.Collections;
import java.util.Map;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class EmptyReward implements QuestReward {
    
    public static EmptyReward deserialize(Map<String, Object> map) {
        return new EmptyReward();
    }
    
    @Override
    public Map<String, Object> serialize() {
        return Collections.emptyMap();
    }
    
    @Override
    public void questCompleted(Quest quest) {
        // nothing to do
    }

    @Override
    public void rewardPlayer(Quest quest, Player p) {
        // nothing to reward
    }
    
    @Override
    public String getType() {
        return "empty";
    }
    
    @Override
    public BaseComponent getComponent() {
        return new TextComponent("Nothing");
    }
}
