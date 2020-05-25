package de.craftlancer.clstuff.explosionregulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import de.craftlancer.core.IntRingBuffer;
import de.craftlancer.core.Utils;

class ItemGroup implements ConfigurationSerializable {
    // settings
    private Set<Material> groupMaterials = new HashSet<>();
    private float minimalYield;
    private int threshold;
    private int limit;
    
    // runtime
    private IntRingBuffer buffer = new IntRingBuffer(1440);
    private int currentMinute = 0;
    private int currentTotal = 0;
    private float currentYield;
    
    public ItemGroup(int limit, int freeLimit, float minimalYield, Set<Material> mats) {
        this.limit = limit;
        this.threshold = freeLimit;
        this.groupMaterials = mats;
        this.minimalYield = minimalYield;
        this.currentYield = 1.0f;
    }
    
    @SuppressWarnings("unchecked")
    public ItemGroup(Map<?,?> map) {
        this.groupMaterials = ((List<String>) map.get("groupMaterials")).stream().map(Material::getMaterial).collect(Collectors.toSet());
        this.minimalYield = ((Number) map.get("minimalYield")).floatValue();
        this.threshold = ((Number) map.get("threshold")).intValue();
        this.limit = ((Number) map.get("limit")).intValue();

        this.currentMinute = ((Number) map.get("currentMinute")).intValue();
        this.currentTotal = ((Number) map.get("currentTotal")).intValue();
        this.buffer = new IntRingBuffer(1440, (int[]) map.get("buffer"));
        this.currentYield = 1.0f - Utils.clamp(((float) (currentTotal - threshold) / limit), 0.0f, 1.0f - minimalYield);
    }
    
    public void tick() {
        currentTotal += currentMinute;
        currentTotal -= buffer.get(0);
        buffer.push(currentMinute);
        this.currentYield = 1.0f - Utils.clamp(((float) (currentTotal - threshold) / limit), 0.0f, 1.0f - minimalYield);
    }
    
    public boolean addCount(List<Block> blocks) {
        long count = blocks.stream().map(Block::getType).filter(groupMaterials::contains).count();
        currentMinute += count;
        
        return count != 0;
    }
    
    public float getCurrentYield() {
        return limit > 0 ? currentYield : 1.0f;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupMaterials", groupMaterials.stream().map(Material::name).collect(Collectors.toList()));
        map.put("minimalYield", minimalYield);
        map.put("threshold", threshold);
        map.put("limit", limit);

        map.put("currentMinute", currentMinute);
        map.put("currentTotal", currentTotal);
        map.put("buffer", buffer.stream().toArray());
        return map;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    
    public void setMinimalYield(float minimalYield) {
        this.minimalYield = minimalYield;
    }
    
    public boolean addMaterials(Collection<Material> materials) {
        return groupMaterials.addAll(materials);
    }
    
    public boolean removeMaterials(Collection<Material> materials) {
        return groupMaterials.removeAll(materials);
    }

    public int getThreshold() {
        return threshold;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public int getCurrentTotal() {
        return currentTotal;
    }
    
    public float getMinimalYield() {
        return minimalYield;
    }
}