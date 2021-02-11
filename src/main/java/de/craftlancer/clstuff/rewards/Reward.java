package de.craftlancer.clstuff.rewards;

import de.craftlancer.core.CLCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Reward implements ConfigurationSerializable {
    
    private RewardRegisterable registerable;
    private String key;
    private List<ItemStack> itemRewards = new ArrayList<>();
    private List<String> commandsRewards = new ArrayList<>();
    private List<String> titleRewards = new ArrayList<>();
    private List<UUID> received = new ArrayList<>();
    
    private List<String> information = new ArrayList<>();
    private String titleMessage = "";
    private String subtitleMessage = "";
    private String chatMessage = "";
    private boolean publicAnnouncement;
    
    public Reward(RewardRegisterable registerable, String key, boolean publicAnnouncement) {
        this.registerable = registerable;
        this.key = key;
        this.publicAnnouncement = publicAnnouncement;
    }
    
    public Reward(Map<String, Object> map) {
        Optional<RewardRegisterable> optional = RewardsManager.getInstance().getRegisterable((String) map.get("registerable"));
        this.registerable = optional.orElse(null);
        this.key = (String) map.get("key");
        this.itemRewards = (List<ItemStack>) map.get("itemRewards");
        this.commandsRewards = (List<String>) map.get("commandsRewards");
        this.titleRewards = (List<String>) map.get("titleRewards");
        this.received = ((List<String>) map.get("received")).stream().map(UUID::fromString).collect(Collectors.toList());
        this.titleMessage = (String) map.get("titleMessage");
        this.subtitleMessage = (String) map.get("subtitleMessage");
        this.chatMessage = (String) map.get("chatMessage");
        this.publicAnnouncement = (boolean) map.get("publicAnnouncement");
        this.information = (List<String>) map.getOrDefault("information", new ArrayList<>());
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("registerable", registerable.getKey());
        map.put("key", key);
        map.put("itemRewards", itemRewards);
        map.put("commandsRewards", commandsRewards);
        map.put("titleRewards", titleRewards);
        map.put("titleMessage", titleMessage);
        map.put("received", received.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("subtitleMessage", subtitleMessage);
        map.put("chatMessage", chatMessage);
        map.put("publicAnnouncement", publicAnnouncement);
        map.put("information", information);
        
        return map;
    }
    
    /**
     * Rewards a player with everything in the given reward.
     *
     * @param save If true, player will be saved into a list of players who have received the reward.
     * @return True if player is online and is rewarded successfully.
     */
    public boolean reward(Player player, boolean save) {
        if (!player.isOnline()) return false;
        
        //Add items to player's inventory and drop any extras
        if (!itemRewards.isEmpty())
            itemRewards.forEach(item -> player.getInventory().addItem(item).forEach((k, v) -> player.getWorld().dropItemNaturally(player.getLocation(), v)));
        //Execute commands
        commandsRewards.stream().map(s -> s.replace("%player%", player.getName())).forEach(s -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s));
        
        if (publicAnnouncement)
            Bukkit.getOnlinePlayers().forEach(p -> sendMessages(player, p));
        else
            sendMessages(player, player);
        
        if (save)
            addAsReceived(player.getUniqueId());
        
        return true;
    }
    
    private void sendMessages(Player player, Player to) {
        if (!titleMessage.isEmpty() || !subtitleMessage.isEmpty())
            CLCore.getInstance().getPlayerTaskScheduler().schedule(player, () -> {
                to.sendTitle(titleMessage.replaceAll("%player%", player.getDisplayName()), subtitleMessage.replaceAll("%player%", player.getDisplayName()), 10, 70, 20);
            }, 100);
        if (!chatMessage.isEmpty()) to.sendMessage(chatMessage.replaceAll("%player%", player.getDisplayName()));
    }
    
    public String getKey() {
        return key;
    }
    
    public void addAsReceived(UUID uuid) {
        if (!received.contains(uuid))
            received.add(uuid);
    }
    
    public boolean hasReceived(UUID uuid) {
        return received.contains(uuid);
    }
    
    public List<ItemStack> getItemRewards() {
        return itemRewards;
    }
    
    public List<String> getCommandsRewards() {
        return commandsRewards;
    }
    
    public List<String> getTitleRewards() {
        return titleRewards;
    }
    
    public List<UUID> getReceived() {
        return received;
    }
    
    public String getTitleMessage() {
        return titleMessage;
    }
    
    public String getSubtitleMessage() {
        return subtitleMessage;
    }
    
    public String getChatMessage() {
        return chatMessage;
    }
    
    public List<String> getInformation() {
        return information;
    }
    
    public void setTitleMessage(String titleMessage) {
        this.titleMessage = titleMessage;
    }
    
    public void setSubtitleMessage(String subtitleMessage) {
        this.subtitleMessage = subtitleMessage;
    }
    
    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
    
    public RewardRegisterable getRegisterable() {
        return registerable;
    }
    
    public boolean removeReceived(UUID uuid) {
        return received.remove(uuid);
    }
    
    public BaseComponent[] toBaseComponent() {
        ComponentBuilder builder = new ComponentBuilder();
        
        builder.append("------------------------------------").color(ChatColor.DARK_GRAY).strikethrough(true);
        if (information.size() == 0) {
            builder.append("\n");
            builder.append("There is no information here yet, ask an admin about this reward.", ComponentBuilder.FormatRetention.NONE).color(ChatColor.AQUA);
        }
        for (String i : information) {
            builder.append("\n");
            builder.append(" " + i, ComponentBuilder.FormatRetention.NONE);
        }
        builder.append("\n");
        builder.append("------------------------------------").color(ChatColor.DARK_GRAY).strikethrough(true);
        
        return builder.create();
    }
}
