package de.craftlancer.clstuff.citizensets;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.gui.PageItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CitizenSetsListener implements Listener {
    
    public static final String CC_PREFIX = ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen] " + ChatColor.YELLOW;
    
    private static CitizenSetsListener instance;
    
    private CLStuff plugin;
    private File file;
    private List<CitizenSet> citizenSets;
    private CitizenSetListGUI gui;
    
    public CitizenSetsListener(CLStuff plugin) {
        ConfigurationSerialization.registerClass(CitizenSet.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionWaterBreathing.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionFireResistance.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionNightVision.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionHaloParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionTrailParticle.class);
        ConfigurationSerialization.registerClass(CitizenSetFunction.FunctionAuraParticle.class);
        instance = this;
        
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "citizenSets.yml");
        
        load();
        createGui();
    }
    
    public void createGui() {
        gui = new CitizenSetListGUI(plugin, citizenSets.stream().map(set -> {
            PageItem item = new PageItem(set.getIcon());
            item.setClickAction(player -> set.getGui().display(player));
            return item;
        }).collect(Collectors.toList()));
    }
    
    public void load() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        citizenSets = (List<CitizenSet>) config.getList("citizenSets", new ArrayList<>());
    }
    
    public void save() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        config.set("citizenSets", citizenSets);
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public CLStuff getPlugin() {
        return plugin;
    }
    
    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        verify(player, 0);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onArmorEquip(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        List<String> armor = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        
        if (armor.stream().noneMatch(s -> s.contains(inventory.getItemInMainHand().getType().name()))
                && armor.stream().noneMatch(s -> s.contains(inventory.getItemInOffHand().getType().name())))
            return;
        
        verify(player, 1);
    }
    
    @EventHandler()
    public void onHandSwap(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        verify(player, 1);
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        verify(player, 1);
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        verify(player, 0);
    }
    
    @EventHandler
    public void onItemPickup(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        verify(player, 1);
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
        Player player = event.getPlayer();
        verify(player, 0);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        verify(player, 0);
    }
    
    private void verify(Player player, int runLater) {
        new LambdaRunnable(() -> {
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
            
            if (disabled.size() == 0 && enabled.size() == 0)
                return;
            
            ComponentBuilder builder = new ComponentBuilder();
            builder.append("[").color(ChatColor.DARK_GRAY).append("CitizenSets").color(ChatColor.DARK_BLUE).append("] ").color(ChatColor.DARK_GRAY);
            if (disabled.size() > 0)
                builder.append("DISABLED ").color(ChatColor.DARK_RED).bold(true).append("[").color(ChatColor.DARK_GRAY).bold(false);
            disabled.forEach(set -> builder.append(disabled.get(0).equals(set) ? set.getName() : ", " + set.getId()).color(ChatColor.DARK_BLUE));
            if (disabled.size() > 0)
                builder.append("] ").color(ChatColor.DARK_GRAY);
            if (enabled.size() > 0)
                builder.append("ENABLED ").color(ChatColor.DARK_GREEN).bold(true).append("[").color(ChatColor.DARK_GRAY).bold(false);
            enabled.forEach(set -> builder.append(enabled.get(0).equals(set) ? set.getName() : ", " + set.getId()).color(ChatColor.DARK_BLUE));
            if (enabled.size() > 0)
                builder.append("]").color(ChatColor.DARK_GRAY);
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, builder.create());
        }).runTaskLater(plugin, runLater);
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
    
    public static CitizenSetsListener getInstance() {
        return instance;
    }
}
