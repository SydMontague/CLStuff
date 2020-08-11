package de.craftlancer.clstuff.arena;

import java.util.Collections;
import java.util.List;

class ArenaMob {
    private final String name;
    private final List<String> description;
    private final String head;
    private final List<ArenaCost> costs;
    private final List<String> mobs;
    private final boolean teleportPlayer;
    
    public ArenaMob(String name, List<String> description, String head, List<String> mobs, List<ArenaCost> costs, boolean teleportPlayer) {
        this.name = name;
        this.description = description;
        this.head = head;
        this.costs = costs;
        this.mobs = mobs;
        this.teleportPlayer = teleportPlayer;
    }

    public String getName() {
        return name;
    }
    
    public List<String> getDescription() {
        return description;
    }
    
    public String getHead() {
        return head;
    }
    
    public List<ArenaCost> getCosts() {
        return Collections.unmodifiableList(costs);
    }
    
    public List<String> getMobs() {
        return Collections.unmodifiableList(mobs);
    }
    
    public boolean isTeleportPlayer() {
        return teleportPlayer;
    }
}