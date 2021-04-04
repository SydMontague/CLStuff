package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomBlockItem implements ConfigurationSerializable {
    
    private String id;
    private ItemStack item;
    
    public CustomBlockItem(String id, ItemStack item) {
        this.id = id;
        
        ItemStack clone = item.clone();
        clone.setAmount(1);
        this.item = clone;
    }
    
    public CustomBlockItem(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.item = (ItemStack) map.get("item");
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", id);
        map.put("item", item);
        
        return map;
    }
    
    public String getId() {
        return id;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public abstract Material getItemMaterial();
    
    public abstract Material getBlockMaterial();
    
    public abstract void setBlockData(Block block);
    
    public abstract boolean equals(Block block);
}
