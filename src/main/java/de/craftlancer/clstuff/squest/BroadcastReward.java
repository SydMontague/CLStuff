package de.craftlancer.clstuff.squest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class BroadcastReward implements QuestReward {
    private String message = "";
    
    public BroadcastReward(String command) {
        this.message = command;
    }
    
    public BroadcastReward(Map<String, Object> map) {
        this.message = (String) map.getOrDefault("message", "");
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
    
    @Override
    public void questCompleted(Quest quest) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void rewardPlayer(Quest quest, Player p) {
        // nothing to reward
    }
    
    @Override
    public String getType() {
        return "broadcast";
    }

    @Override
    public BaseComponent getComponent() {
        BaseComponent base = new TextComponent("[Text]");
        base.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { new TextComponent(message) }));
        return base;
    }
}
