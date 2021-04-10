package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomBlockItem implements ConfigurationSerializable {
    
    private String id;
    private ItemStack item;
    private boolean dropItem;
    
    public CustomBlockItem(String id, ItemStack item) {
        this(id, item, true);
    }
    
    public CustomBlockItem(String id, ItemStack item, boolean dropItem) {
        this.id = id;
        
        ItemStack clone = item.clone();
        clone.setAmount(1);
        this.item = clone;
        this.dropItem = dropItem;
    }
    
    public CustomBlockItem(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.item = (ItemStack) map.get("item");
        this.dropItem = true;
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
    
    public boolean isDropItem() {
        return dropItem;
    }
    
    public abstract Material getItemMaterial();
    
    public abstract Material getBlockMaterial();
    
    public abstract BlockData getBlockData(BlockData data);
    
    public abstract boolean equals(BlockData block);
    
    public boolean compareItem(ItemStack i) {
        if (item.getType() != i.getType())
            return false;
        
        if (!item.getItemMeta().hasCustomModelData() && !i.getItemMeta().hasCustomModelData())
            return true;
        
        if (!item.getItemMeta().hasCustomModelData() && i.getItemMeta().hasCustomModelData())
            return false;
        
        if (item.getItemMeta().hasCustomModelData() && !i.getItemMeta().hasCustomModelData())
            return false;
        
        return item.getItemMeta().getCustomModelData() == i.getItemMeta().getCustomModelData();
    }
}
