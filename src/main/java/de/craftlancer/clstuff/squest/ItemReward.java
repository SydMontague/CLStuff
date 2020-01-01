package de.craftlancer.clstuff.squest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class ItemReward implements QuestReward {
    private ItemStack item;
    private int amount;
    private RewardDistributionType distribution;
    private int distrX = 0;
    
    public ItemReward(ItemStack item, int amount, RewardDistributionType distribution, int distrX) {
        this.item = item;
        this.amount = amount;
        this.distribution = distribution;
        this.distrX = distrX;
    }
    
    public ItemReward(Map<String, Object> map) {
        item = (ItemStack) map.get("item");
        amount = (int) map.getOrDefault("amount", 0);
        distribution = RewardDistributionType.valueOf(map.get("distribution").toString());
        this.distrX = (int) map.getOrDefault("distrX", 0);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("amount", amount);
        map.put("distribution", distribution.name());
        map.put("distrX", distrX);
        return map;
    }
    
    @Override
    public void questCompleted(Quest quest) {
        if (distribution == RewardDistributionType.EVERYONE_ONLINE) {
            item.setAmount(amount);
            Bukkit.getOnlinePlayers().forEach(a -> giveItem(a, item));
        }
    }
    
    private void giveItem(Player p, ItemStack giveItem) {
        Map<Integer, ItemStack> full = p.getInventory().addItem(giveItem);
        if (!full.isEmpty())
            full.values().forEach(a -> p.getWorld().dropItem(p.getLocation(), a));
    }
    
    @Override
    public void rewardPlayer(Quest quest, Player p) {
        int totalPoints = quest.getRequirements().stream().map(a -> a.getTargetAmount() * a.getWeight()).collect(Collectors.summingInt(Integer::intValue));
        int playerPoints = quest.getRequirements().stream().map(a -> a.getContribution().getOrDefault(p.getUniqueId(), 0) * a.getWeight())
                                .collect(Collectors.summingInt(Integer::intValue));
        
        switch (distribution) {
            case DONATED_ABOVE:
                if (totalPoints * distrX / 100 < playerPoints) {
                    item.setAmount(amount);
                    giveItem(p, item);
                }
                break;
            case DONATION_SHARE:
                float share = (float) playerPoints / totalPoints;
                if (share * amount >= 1) {
                    item.setAmount((int) (amount * share));
                    giveItem(p, item);
                }
                break;
            case EVERY_DONATOR:
                if (playerPoints != 0) {
                    item.setAmount(amount);
                    giveItem(p, item);
                }
                break;
            case MOST_DONATED:
                Map<UUID, Integer> ranking = new HashMap<>();
                quest.getRequirements().forEach(a -> a.getContribution().forEach((b, c) -> ranking.merge(b, c, (d, e) -> d + e)));

                @SuppressWarnings("unchecked") 
                boolean isEligable = ranking.entrySet().stream().sorted(Comparator.comparingInt(a -> ((Entry<UUID, Integer>) a).getValue()).reversed()).limit(distrX)
                                            .anyMatch(a -> a.getKey().equals(p.getUniqueId()));
                
                if(isEligable) {
                    item.setAmount(amount);
                    giveItem(p, item);
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    public String getType() {
        return "item";
    }
    
    @Override
    public BaseComponent getComponent() {
        BaseComponent base = new TextComponent(item.getType().name());
        base.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {
                new TextComponent(org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(item).save(new NBTTagCompound()).toString()) }));

        base.addExtra(" ");
        base.addExtra(Integer.toString(amount));
        return base;
    }
}
