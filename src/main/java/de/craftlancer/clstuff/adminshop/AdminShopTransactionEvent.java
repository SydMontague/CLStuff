package de.craftlancer.clstuff.adminshop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AdminShopTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private Location location;
    private AdminShopTrade trade;
    private Player player;
    private int row;
    
    public AdminShopTransactionEvent(Player player, Location location, AdminShopTrade trade, int row) {
        this.player = player;
        this.trade = trade;
        this.row = row;
        this.location = location;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public int getRow() {
        return row;
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
