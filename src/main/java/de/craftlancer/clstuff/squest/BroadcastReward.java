package de.craftlancer.clstuff.squest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class BroadcastReward implements QuestReward {
    private String message = "";
    private boolean discord = false;
    
    public BroadcastReward(String command, boolean discord) {
        this.message = command;
        this.discord = discord;
    }
    
    public BroadcastReward(Map<String, Object> map) {
        this.message = (String) map.getOrDefault("message", "");
        this.discord = (boolean) map.getOrDefault("discord", false);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("discord", discord);
        return map;
    }
    
    @Override
    public void questCompleted(Quest quest) {
        Bukkit.broadcastMessage(message);
        if(discord)
            DiscordUtil.queueMessage(DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("event"), message);
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
