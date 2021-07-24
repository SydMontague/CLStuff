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
import java.util.List;
import java.util.Optional;

public class RewardsManager implements MessageRegisterable, Listener {
    
    public static NamespacedKey REWARD_KEY;
    private static RewardsManager instance;
    
    private List<Reward> rewards = new ArrayList<>();
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
    
    public List<Reward> getRewards() {
        return rewards;
    }
    
    public Optional<Reward> getReward(String key) {
        return rewards.stream().filter(r -> r.getKey().equals(key)).findFirst();
    }
    
    public void addReward(Reward reward) {
        rewards.add(reward);
    }
    
    public void addReward(String key, boolean publicAnnouncement) {
        rewards.add(new Reward(key, publicAnnouncement));
    }
    
    @Override
    public String getMessageID() {
        return "Rewards";
    }
    
    public static RewardsManager getInstance() {
        return instance;
    }
}
