package de.craftlancer.clstuff.inventorymanagement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LastInventory implements ConfigurationSerializable {
    
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    
    private List<ItemStack> armorContents;
    private List<ItemStack> contents;
    private ItemStack offhand;
    
    private UUID owner;
    private long timeCreated;
    private Location location;
    private String date;
    
    public LastInventory(PlayerInventory inventory, Player player) {
        this.owner = player.getUniqueId();
        this.timeCreated = System.currentTimeMillis();
        this.offhand = inventory.getItemInOffHand() == null ? AIR : inventory.getItemInOffHand();
        this.contents = Arrays.stream(inventory.getContents()).map(i -> i == null || i.getType() == Material.AIR ? AIR : i).collect(Collectors.toList());
        this.armorContents = Arrays.stream(inventory.getArmorContents()).map(i -> i == null || i.getType() == Material.AIR ? AIR : i).collect(Collectors.toList());
        this.location = player.getLocation();
        
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy-hh:mm:ss");
        this.date = formatter.format(date);
    }
    
    public LastInventory(Map<String, Object> map) {
        this.owner = UUID.fromString((String) map.get("owner"));
        this.timeCreated = (long) map.get("timeCreated");
        this.armorContents = (List<ItemStack>) map.get("armor");
        this.contents = (List<ItemStack>) map.get("contents");
        this.offhand = (ItemStack) map.get("offhand");
        this.location = (Location) map.get("location");
        this.date = (String) map.get("date");
        
        
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("owner", owner.toString());
        map.put("timeCreated", timeCreated);
        map.put("armor", armorContents);
        map.put("contents", contents);
        map.put("offhand", offhand);
        map.put("location", location);
        map.put("date", date);
        
        return map;
    }
    
    public long getTimeCreated() {
        return timeCreated;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public ItemStack[] getContents() {
        ItemStack[] array = new ItemStack[contents.size()];
        
        for (int i = 0; i < contents.size(); i++)
            array[i] = contents.get(i);
        
        return array;
    }
    
    public ItemStack[] getArmorContents() {
        ItemStack[] array = new ItemStack[armorContents.size()];
        
        for (int i = 0; i < armorContents.size(); i++)
            array[i] = armorContents.get(i);
        
        return array;
    }
    
    public ItemStack getOffhand() {
        return offhand;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public String getDate() {
        return date;
    }
}
