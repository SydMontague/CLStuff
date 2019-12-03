package de.craftlancer.clstuff.squest;

import net.md_5.bungee.api.ChatColor;

public enum QuestState {
    INACTIVE,
    ACTIVE,
    COMPLETED;
    
    public String getText() {
        switch (this) {
            case ACTIVE:
                return ChatColor.YELLOW + this.toString();
            case INACTIVE:
                return ChatColor.RED + this.toString();
            case COMPLETED:
                return ChatColor.GREEN + this.toString();
        }
        
        return this.toString();
    }
}
