package de.craftlancer.clstuff.adminshop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AdminShopTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private AdminShopTrade trade;
    private Player player;
    
    public AdminShopTransactionEvent(Player player, AdminShopTrade trade) {
        this.player = player;
        this.trade = trade;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public AdminShopTrade getTrade() {
        return trade;
    }
    
    @Override
    public @Nonnull
    HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
