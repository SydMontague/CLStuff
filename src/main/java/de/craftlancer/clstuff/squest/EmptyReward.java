package de.craftlancer.clstuff.squest;

import java.util.Collections;
import java.util.Map;

public class EmptyReward implements QuestReward {
    
    
    public static EmptyReward deserialize(Map<String, Object> map) {
        return new EmptyReward();
    }
    
    @Override
    public Map<String, Object> serialize() {
        return Collections.emptyMap();
    }
    
    @Override
    public void questCompleted() {
    }
    
}
