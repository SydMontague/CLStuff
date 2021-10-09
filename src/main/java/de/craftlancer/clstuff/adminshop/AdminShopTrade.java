package de.craftlancer.clstuff.adminshop;

import de.craftlancer.clapi.clstuff.adminshop.AbstractAdminShopTrade;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminShopTrade implements ConfigurationSerializable, AbstractAdminShopTrade {
    private ItemStack[] input = new ItemStack[7];
    private ItemStack output;
    private boolean broadcast = false;
    private String broadcastString;
    
    public AdminShopTrade() {
    }
    
    public AdminShopTrade(Map<?, ?> map) {
        this.output = (ItemStack) map.get("output");
        this.broadcast = (Boolean) map.get("broadcast");
        this.broadcastString = (String) map.get("broadcastString");
        this.input[0] = (ItemStack) map.get("input1");
        this.input[1] = (ItemStack) map.get("input2");
        this.input[2] = (ItemStack) map.get("input3");
        this.input[3] = (ItemStack) map.get("input4");
        this.input[4] = (ItemStack) map.get("input5");
        this.input[5] = (ItemStack) map.get("input6");
        this.input[6] = (ItemStack) map.get("input7");
    }
    
    @Override
    @Nonnull
    public ItemStack[] getInput() {
        return input;
    }
    
    @Override
    public boolean isValid() {
        return output != null && Arrays.stream(input).anyMatch(Objects::nonNull);
    }
    
    @Override
    public void setInput(int j, @Nullable ItemStack item) {
        input[j] = item;
    }
    
    @Override
    public void setOutput(@Nullable ItemStack output) {
        this.output = output;
    }
    
    @Override
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
    
    @Override
    public ItemStack getOutput() {
        return output;
    }
    
    @Override
    public boolean isBroadcast() {
        return broadcast;
    }
    
    @Override
    @Nullable
    public String getBroadcastString() {
        return broadcastString;
    }
    
    public void setBroadcastString(@Nullable String broadcastString) {
        this.broadcastString = broadcastString;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("output", output);
        map.put("broadcastString", broadcastString);
        map.put("broadcast", broadcast);
        map.put("input1", input[0]);
        map.put("input2", input[1]);
        map.put("input3", input[2]);
        map.put("input4", input[3]);
        map.put("input5", input[4]);
        map.put("input6", input[5]);
        map.put("input7", input[6]);
        
        return map;
    }
}