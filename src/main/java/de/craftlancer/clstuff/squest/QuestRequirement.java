package de.craftlancer.clstuff.squest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestRequirement implements ConfigurationSerializable {
    private final ItemStack item;
    private final int targetAmount;
    private int currentAmount = 0;
    private int weight = 1;
    private Map<UUID, Integer> contribution = new HashMap<>();
    
    public QuestRequirement(ItemStack item, int weight) {
        this.item = item;
        this.targetAmount = item.getAmount();
        this.weight = weight;
    }
    
    public QuestRequirement(ItemStack item) {
        this(item, 1);
    }
    
    @SuppressWarnings("unchecked")
    public QuestRequirement(Map<String, Object> input) {
        this.item = (ItemStack) input.get("item");
        this.targetAmount = item.getAmount();
        this.currentAmount = (int) input.get("current");
        this.weight = (int) input.get("weight");
        this.contribution = ((Map<Object, Object>) input.get("contribution")).entrySet().stream()
                                                                             .collect(Collectors.toMap(a -> UUID.fromString(a.getKey().toString()),
                                                                                                       a -> (int) a.getValue()));
    }
    
    public Map<UUID, Integer> getContribution() {
        return contribution;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("current", currentAmount);
        map.put("weight", weight);
        map.put("contribution", contribution.entrySet().stream().collect(Collectors.toMap(a -> a.getKey().toString(), Entry::getValue)));
        return map;
    }
    
    public boolean isRequiredItem(ItemStack otherItem) {
        return currentAmount < targetAmount && this.item.isSimilar(otherItem);
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public int getCurrentAmount() {
        return currentAmount;
    }
    
    public int getTargetAmount() {
        return targetAmount;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public void contribute(Player p, ItemStack contribItem) {
        if(!isRequiredItem(contribItem))
            return;

        int remainingAmount = targetAmount - currentAmount;
        int givenAmount = Math.min(contribItem.getAmount(), remainingAmount);
        
        contribution.merge(p.getUniqueId(), givenAmount, (a, b) -> a.intValue() + b.intValue());
        currentAmount += givenAmount;
        contribItem.setAmount(Math.max(0, contribItem.getAmount() - givenAmount));
    }
    
    public boolean isFinished() {
        return targetAmount <= currentAmount;
    }
}