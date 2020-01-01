package de.craftlancer.clstuff.squest;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public interface QuestReward extends ConfigurationSerializable {
    public void questCompleted(Quest quest);

    public void rewardPlayer(Quest quest, Player p);
    
    public String getType();
    
    public BaseComponent getComponent();
}