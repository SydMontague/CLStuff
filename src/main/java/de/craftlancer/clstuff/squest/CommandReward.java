package de.craftlancer.clstuff.squest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class CommandReward implements QuestReward {
    private String command = "";
    
    public CommandReward(String command) {
        this.command = command;
    }
    
    public static CommandReward deserialize(Map<String, Object> map) {
        return new CommandReward(map.getOrDefault("command", "").toString());
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("command", command);
        return map;
    }
    
    @Override
    public void questCompleted() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    
}
