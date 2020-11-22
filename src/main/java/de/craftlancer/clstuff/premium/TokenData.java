package de.craftlancer.clstuff.premium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import de.craftlancer.clstuff.citizensets.CitizenSetsManager;

public class TokenData implements ConfigurationSerializable {
    private Material targetItem;
    private int itemModelData;
    private String itemName;
    private List<String> lore;
    private String citizenSetData;
    
    public TokenData(Material targetItem, int modelData, String name, List<String> lore, String citizenSetData) {
        this.targetItem = targetItem;
        this.itemModelData = modelData;
        this.itemName = name;
        this.lore = lore;
        this.citizenSetData = citizenSetData;
    }
    
    @SuppressWarnings("unchecked")
    public TokenData(Map<?, ?> map) {
        this.targetItem = Material.getMaterial(map.get("targetItem").toString());
        this.lore = (List<String>) map.get("lore");
        this.itemName = (String) map.get("name");
        this.itemModelData = ((Number) map.get("modelData")).intValue();
        this.citizenSetData = (String) map.get("citizenSetData");
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("targetItem", targetItem.name());
        map.put("modelData", itemModelData);
        map.put("name", itemName);
        map.put("lore", lore);
        map.put("citizenSetData", citizenSetData);
        return map;
    }
    
    public ItemStack apply(@Nonnull ItemStack item) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (itemModelData != -1)
            meta.setCustomModelData(itemModelData);
        if (lore != null && !lore.isEmpty())
            meta.setLore(lore);
        if (itemName != null)
            meta.setDisplayName(itemName);
        if (citizenSetData != null)
            meta.getPersistentDataContainer().set(CitizenSetsManager.KEY, PersistentDataType.STRING, citizenSetData);
        
        newItem.setItemMeta(meta);
        newItem.setAmount(1);
        return newItem;
    }
    
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(targetItem);
        ItemMeta meta = item.getItemMeta();
        if (itemModelData != -1)
            meta.setCustomModelData(itemModelData);
        if (lore != null && !lore.isEmpty())
            meta.setLore(lore);
        if (itemName != null)
            meta.setDisplayName(itemName);
        if (citizenSetData != null)
            meta.getPersistentDataContainer().set(CitizenSetsManager.KEY, PersistentDataType.STRING, citizenSetData);
        
        item.setItemMeta(meta);
        return item;
    }
    
    public static TokenData fromItemStack(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        int modelData = meta != null && meta.hasCustomModelData() ? meta.getCustomModelData() : -1;
        String name = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : null;
        List<String> lore = meta != null && meta.hasLore() ? meta.getLore() : new ArrayList<>();
        String citizenSetData = meta != null ? meta.getPersistentDataContainer().get(CitizenSetsManager.KEY, PersistentDataType.STRING) : null;
        
        return new TokenData(item.getType(), modelData, name, lore, citizenSetData);
    }
}
