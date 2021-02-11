package de.craftlancer.clstuff.premium;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DonatorTicketAccount implements ConfigurationSerializable {
    
    private UUID owner;
    private long lastPointClaimTime;
    private int points;
    private boolean isPremium;
    
    public DonatorTicketAccount(UUID owner, int points, long lastPointClaimTime) {
        this.owner = owner;
        this.points = points;
        this.lastPointClaimTime = lastPointClaimTime;
        this.isPremium = false;
    }
    
    public DonatorTicketAccount(Map<String, Object> map) {
        this.owner = UUID.fromString((String) map.get("owner"));
        this.points = (int) map.get("points");
        this.lastPointClaimTime = (long) map.get("lastPointClaimTime");
        this.isPremium = (boolean) map.get("isLifeTime");
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("owner", owner.toString());
        map.put("lastPointClaimTime", lastPointClaimTime);
        map.put("points", points);
        map.put("isLifeTime", isPremium);
        
        return map;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public long getLastPointClaimTime() {
        return lastPointClaimTime;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void updatePoints(int amount) {
        lastPointClaimTime = System.currentTimeMillis();
        points += amount;
        
        if (Bukkit.getPlayer(owner) != null)
            Bukkit.getPlayer(owner).sendMessage(DonatorTicketRegistry.getInstance().getPrefix() + "§aYou have gained " + amount + " donator tokens.");
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
