package de.craftlancer.clstuff.premium;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ModelTokenApplyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private final HumanEntity player;
    private final ItemStack input;
    private final ItemStack token;
    private ItemStack result;
    
    public ModelTokenApplyEvent(HumanEntity player, ItemStack item, ItemStack token, ItemStack result) {
        this.player = player;
        this.input = item;
        this.token = token;
        this.result = result;
    }

    /**
     * Gets the player applying the token.
     * 
     * @return the player applying the token
     */
    public HumanEntity getPlayer() {
        return player;
    }
    
    /**
     * Gets a copy of the input item. Modification won't apply.
     * 
     * @return a copy of the input item
     */
    public ItemStack getInput() {
        return input.clone();
    }

    /**
     * Gets a copy of the token. Modification won't apply.
     * 
     * @return a copy of the token
     */
    public ItemStack getToken() {
        return token.clone();
    }
    
    /**
     * Gets the result item. 
     * 
     * @return the result item
     */
    public ItemStack getResult() {
        return result;
    }
    
    public void setResult(ItemStack result) {
        this.result = result;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
