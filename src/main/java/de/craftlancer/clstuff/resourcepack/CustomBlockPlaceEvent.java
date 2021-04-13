package de.craftlancer.clstuff.resourcepack;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;

import javax.annotation.Nonnull;

public class CustomBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    
    private CustomBlockItem customBlockItem;
    private BlockPlaceEvent blockPlaceEvent;
    private boolean isCancelled = false;
    
    public CustomBlockPlaceEvent(BlockPlaceEvent blockPlaceEvent, CustomBlockItem customBlockItem) {
        this.blockPlaceEvent = blockPlaceEvent;
        this.customBlockItem = customBlockItem;
    }
    
    public BlockPlaceEvent getBlockPlaceEvent() {
        return blockPlaceEvent;
    }
    
    public CustomBlockItem getCustomBlockItem() {
        return customBlockItem;
    }
    
    @Override
    public @Nonnull
    HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    
    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
