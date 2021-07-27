package de.craftlancer.clstuff.citizensets;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.citizensets.commands.CitizenSetCommandHandler;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.gui.PageItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CitizenSetsManager implements Listener {
    public static final NamespacedKey KEY = new NamespacedKey(CLStuff.getInstance(), "citizensets");
    public static final String CC_PREFIX = ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen] " + ChatColor.YELLOW;
    
    private final CLStuff plugin;
    private File file;
    private List<CitizenSet> citizenSets;
    private CitizenSetListGUI gui;
    
    public CitizenSetsManager(CLStuff plugin) {
        this.plugin = plugin;
        
        ConfigurationSerialization.registerClass(CitizenSet.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionWaterBreathing.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionFireResistance.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionNightVision.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionHaloParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionTrailParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionAuraParticle.class);
        
        this.file = new File(plugin.getDataFolder(), "citizenSets.yml");
        
        load();
        createGui();

        plugin.getCommand("citizensets").setExecutor(new CitizenSetCommandHandler(plugin, this));
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void createGui() {
        gui = new CitizenSetListGUI(plugin, citizenSets.stream().map(set -> {
            PageItem item = new PageItem(set.getIcon());
            item.setClickAction(player -> set.getGui().display(player));
            return item;
        }).collect(Collectors.toList()));
    }
    
    public void load() {
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        citizenSets = (List<CitizenSet>) config.getList("citizenSets", new ArrayList<>());
    }
    
    public void save() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        config.set("citizenSets", citizenSets);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        verify((Player) event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onArmorEquip(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        List<String> armor = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        
        if (armor.stream().noneMatch(s -> s.contains(inventory.getItemInMainHand().getType().name())
                && s.contains(inventory.getItemInOffHand().getType().name())))
            return;
        
        new LambdaRunnable(() -> verify(player)).runTaskLater(plugin, 1);
    }
    
    @EventHandler()
    public void onHandSwap(PlayerItemHeldEvent event) {
        new LambdaRunnable(() -> verify(event.getPlayer())).runTaskLater(plugin, 1);
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        new LambdaRunnable(() -> verify(player)).runTaskLater(plugin, 1);
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        verify(player);
    }
    
    @EventHandler
    public void onItemPickup(PlayerSwapHandItemsEvent event) {
        new LambdaRunnable(() -> verify(event.getPlayer())).runTaskLater(plugin, 1);
    }
    
    @EventHandler()
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        citizenSets.forEach(set -> {
            if (set.contains(player))
                set.remove(player.getUniqueId());
        });
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        verify(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerRespawnEvent event) {
        verify(event.getPlayer());
    }
    
    private void verify(Player player) {
        PlayerInventory inventory = player.getInventory();
        List<CitizenSet> disabled = new ArrayList<>();
        List<CitizenSet> enabled = new ArrayList<>();
        
        citizenSets.forEach(set -> {
            boolean isCompleteSet = set.isCompleteSet(inventory);
            if (!isCompleteSet && set.contains(player)) {
                disabled.add(set);
                set.remove(player.getUniqueId());
            }
            if (isCompleteSet && !set.contains(player)) {
                enabled.add(set);
                set.run(player);
            }
        });
        
        if (disabled.isEmpty() && enabled.isEmpty())
            return;
        
        ComponentBuilder builder = new ComponentBuilder();
        builder.append("[").color(ChatColor.DARK_GRAY).append("CitizenSets").color(ChatColor.DARK_BLUE).append("] ").color(ChatColor.DARK_GRAY);
        
        if (!disabled.isEmpty()) {
            builder.append("DISABLED ").color(ChatColor.DARK_RED).bold(true).append("[").color(ChatColor.DARK_GRAY).bold(false);
            builder.append(disabled.stream().map(CitizenSet::getName).collect(Collectors.joining(", ")));
            builder.color(ChatColor.DARK_BLUE);
            builder.append("] ").color(ChatColor.DARK_GRAY);
        }
        
        if (!enabled.isEmpty()) {
            builder.append("ENABLED ").color(ChatColor.DARK_GREEN).bold(true).append("[").color(ChatColor.DARK_GRAY).bold(false);
            builder.append(enabled.stream().map(CitizenSet::getName).collect(Collectors.joining(", ")));
            builder.color(ChatColor.DARK_BLUE);
            builder.append("]").color(ChatColor.DARK_GRAY);
        }
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, builder.create());
    }
    
    public List<CitizenSet> getCitizenSets() {
        return citizenSets;
    }
    
    public void addCitizenSet(CitizenSet set) {
        citizenSets.add(set);
        createGui();
    }
    
    public void removeCitizenSet(String citizenSetID) {
        citizenSets.removeIf(set -> set.getId().equals(citizenSetID));
        createGui();
    }
    
    public CitizenSetListGUI getGui() {
        return gui;
    }
}
