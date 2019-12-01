package de.craftlancer.clstuff.squest;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface QuestReward extends ConfigurationSerializable {
    public void questCompleted();
}