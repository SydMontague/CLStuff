package de.craftlancer.clstuff.squest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.squest.commands.QuestCommandHandler;

/*
 * squest desc <name> <desc>
 * squest req add <name> <amount> (takes item from hand)
 * squest req list <name>
 * squest req remove <name> <id>
 * squest reward <name> <value>
 * squest status <name> <value>
 * 
 * squest list
 * squest progress <name>
 * squest info <name>
 */

public class ServerQuests implements Listener {
    public static final String METADATA_KEY = "squestCreate";
    
    static {
        ConfigurationSerialization.registerClass(EmptyReward.class);
        ConfigurationSerialization.registerClass(CommandReward.class);
        ConfigurationSerialization.registerClass(BroadcastReward.class);
        ConfigurationSerialization.registerClass(QuestRequirement.class);
        ConfigurationSerialization.registerClass(PotionEffectReward.class);
        ConfigurationSerialization.registerClass(ItemReward.class);
    }
    
    private final CLStuff plugin;
    private final File questFile;
    private List<Quest> quests = new ArrayList<>();
    
    public ServerQuests(CLStuff plugin) {
        this.plugin = plugin;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("squest").setExecutor(new QuestCommandHandler(plugin, this));
        questFile = new File(plugin.getDataFolder(), "quests.yml");
        
        Configuration config = YamlConfiguration.loadConfiguration(questFile);
        config.getKeys(false).forEach(a -> quests.add(new Quest(plugin, config.getConfigurationSection(a))));
    }
    
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        quests.forEach(a -> a.save(config));
        try {
            config.save(questFile);
        }
        catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while saving quests.", e);
        }
    }
    
    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;
        
        if (!event.getPlayer().hasMetadata(METADATA_KEY))
            return;
        
        Block b = event.getClickedBlock();
        
        if (b.getType() != Material.CHEST)
            return;
        
        String name = event.getPlayer().getMetadata(METADATA_KEY).get(0).asString();
        event.getPlayer().removeMetadata(METADATA_KEY, plugin);
        Player p = event.getPlayer();
        
        if (hasQuest(name))
            p.sendMessage("A quest with this name already exists.");
        else {
            addQuest(new Quest(plugin, name, b.getLocation()));
            p.sendMessage("Quest created");
        }
    }
    
    public boolean addQuest(Quest quest) {
        boolean result = quests.add(quest);
        save();
        return result;
    }
    
    public Optional<Quest> getQuest(String name) {
        return quests.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst();
    }
    
    public boolean hasQuest(String name) {
        return quests.stream().anyMatch(a -> a.getName().equalsIgnoreCase(name));
    }
    
    public boolean removeQuest(String name) {
        quests.stream().filter(a -> a.getName().equalsIgnoreCase(name)).forEach(HandlerList::unregisterAll);
        boolean result = quests.removeIf(a -> a.getName().equalsIgnoreCase(name));
        
        save();
        return result;
    }
    
    public List<Quest> getQuests() {
        return Collections.unmodifiableList(quests);
    }
    
}
