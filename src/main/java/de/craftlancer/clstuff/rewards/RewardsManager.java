package de.craftlancer.clstuff.rewards;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MessageRegisterable;
import de.craftlancer.core.util.MessageUtil;
import me.sizzlemcgrizzle.blueprints.api.BlueprintPostPasteEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RewardsManager implements MessageRegisterable, Listener {
    
    public static NamespacedKey REWARD_KEY;
    private static RewardsManager instance;
    
    private List<RewardRegisterable> registered = new ArrayList<>();
    private List<Reward> rewards = new ArrayList<>();
    private Map<Reward, RewardEditor> editors = new HashMap<>();
    private CLStuff plugin;
    
    public RewardsManager(CLStuff plugin) {
        ConfigurationSerialization.registerClass(Reward.class);
        this.plugin = plugin;
        instance = this;
        REWARD_KEY = new NamespacedKey(plugin, "clstuff_rewards");
        
        MessageUtil.register(this, new TextComponent("§8[§fRewards§8]"), ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED,
                ChatColor.DARK_AQUA, ChatColor.GREEN);
        
        //Run later to let all plugins register rewards
        new LambdaRunnable(this::load).runTaskLater(plugin, 40);
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlueprintPaste(BlueprintPostPasteEvent event) {
        RewardsManager.getInstance().getReward(event.getType()).ifPresent(r -> r.reward(event.getPlayer(), true));
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "rewards.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        this.rewards = (List<Reward>) config.getList("rewards", new ArrayList<>());
        
        rewards.forEach(r -> editors.put(r, new RewardEditor(this, r)));
    }
    
    public void save() {
        try {
            File file = new File(plugin.getDataFolder(), "rewards.yml");
            
            if (!file.exists())
                plugin.saveResource(file.getName(), false);
            
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            config.set("rewards", rewards);
            
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public RewardEditor getEditor(Reward reward) {
        return editors.getOrDefault(reward, new RewardEditor(this, reward));
    }
    
    public List<Reward> getRewards() {
        return rewards;
    }
    
    public Optional<Reward> getReward(String key) {
        return rewards.stream().filter(r -> r.getKey().equals(key)).findFirst();
    }
    
    public void removeReward(String key) {
        rewards.removeIf(r -> {
            if (r.getKey().equals(key)) {
                editors.remove(r);
                return true;
            }
            
            return false;
        });
    }
    
    public void addReward(Reward reward) {
        rewards.add(reward);
    }
    
    public void addReward(RewardRegisterable registerable, String key, boolean publicAnnouncement) {
        rewards.add(new Reward(registerable, key, publicAnnouncement));
    }
    
    @Override
    public String getMessageID() {
        return "Rewards";
    }
    
    public static RewardsManager getInstance() {
        return instance;
    }
    
    public void register(RewardRegisterable registerable) {
        if (registered.stream().anyMatch(r -> r.getKey().equalsIgnoreCase(registerable.getKey())))
            throw new IllegalArgumentException("A class has already been registered with this RewardRegisterable key");
        
        registered.add(registerable);
    }
    
    public Optional<RewardRegisterable> getRegisterable(String key) {
        return registered.stream().filter(r -> r.getKey().equalsIgnoreCase(key)).findFirst();
    }
    
    public List<RewardRegisterable> getRegistered() {
        return registered;
    }
    
    public List<Reward> getRewards(RewardRegisterable registerable) {
        return rewards.stream().filter(r -> r.getRegisterable().getKey().equalsIgnoreCase(registerable.getKey())).collect(Collectors.toList());
    }
    
    public void clearAll(RewardRegisterable registerable) {
        rewards.removeIf(r -> r.getRegisterable().equals(registerable));
    }
}
