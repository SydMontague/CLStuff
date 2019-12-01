package de.craftlancer.clstuff.squest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class BroadcastReward implements QuestReward {
    private String message = "";
    
    public BroadcastReward(String command) {
        this.message = command;
    }
    
    public static BroadcastReward deserialize(Map<String, Object> map) {
        return new BroadcastReward(map.getOrDefault("message", "").toString());
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
    
    @Override
    public void questCompleted() {
        Bukkit.broadcastMessage(message);
    }
    
}
