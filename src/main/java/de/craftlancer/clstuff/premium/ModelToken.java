package de.craftlancer.clstuff.premium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;

// TODO Anvil name input
// TODO token ID, for give?
/*
 * TokenItem
 * CustomModelData
 * TokenTargetItem
 * 
 * Mainhand Token
 * Offhand Target Item
 * 
 * Type
 * CustomModelData
 * Lore
 * Name
 * 
 * add
 * remove
 * list
 * give
 */
public class ModelToken implements Listener {
    private static final int ITEM_SLOT = 0;
    private static final int TOKEN_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    
    private CLStuff plugin;
    private Map<ItemStack, TokenData> tokenMap = new HashMap<>();
    private Map<Material, List<Integer>> cmdBlacklist = new EnumMap<>(Material.class);
    
    static {
        ConfigurationSerialization.registerClass(TokenData.class);
    }
    
    @SuppressWarnings("unchecked")
    public ModelToken(CLStuff plugin) {
        this.plugin = plugin;
        
        plugin.getCommand("modeltoken").setExecutor(new ModelTokenCommandHandler(plugin, this));
        
        Configuration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modelTokens.yml"));
        
        tokenMap = config.getMapList("modelTokens").stream()
                         .collect(Collectors.toMap(a -> (ItemStack) a.get("tokenItem"), a -> (TokenData) a.get("tokenData")));
        
        ConfigurationSection blacklist = config.getConfigurationSection("blacklist");
        if (blacklist != null)
            cmdBlacklist = blacklist.getValues(false).entrySet().stream()
                                    .collect(Collectors.toMap(a -> Material.getMaterial(a.getKey()), a -> (List<Integer>) a.getValue()));
    }
    
    public void save() {
        File tokenFile = new File(plugin.getDataFolder(), "modelTokens.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        List<Map<String, Object>> mapList = new ArrayList<>();
        tokenMap.forEach((a, b) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("tokenItem", a);
            map.put("tokenData", b);
            mapList.add(map);
        });
        config.set("modelTokens", mapList);
        
        Map<String, List<Integer>> blackListMap = new HashMap<>();
        cmdBlacklist.forEach((a, b) -> blackListMap.put(a.toString(), b));
        config.set("blacklist", blackListMap);
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(tokenFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Token: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    @EventHandler()
    public void onAnvil(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        ItemStack item = inventory.getItem(ITEM_SLOT);
        ItemStack token = inventory.getItem(TOKEN_SLOT);
        
        if (item == null || token == null)
            return;
        
        TokenData tokenData = getToken(token);
        
        if (tokenData == null || item.getType() != tokenData.getTargetItem())
            return;
        
        ItemMeta meta = item.getItemMeta();
        
        if (meta.hasCustomModelData() && cmdBlacklist.getOrDefault(item.getType(), Collections.emptyList()).contains(meta.getCustomModelData()))
            return;
        
        e.setResult(tokenData.apply(item));
    }
    
    @EventHandler
    public void onAnvilTake(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        
        // make sure it's an anvil result slot
        if (inventory.getType() != InventoryType.ANVIL || event.getSlotType() != SlotType.RESULT)
            return;
        
        ItemStack item = inventory.getItem(ITEM_SLOT);
        ItemStack token = inventory.getItem(TOKEN_SLOT);
        HumanEntity player = event.getWhoClicked();
        
        // make sure there are actually items in the input slots
        if (item == null || token == null)
            return;
        
        // get the token data
        TokenData tokenData = getToken(token);
        if (tokenData == null || item.getType() != tokenData.getTargetItem())
            return;
        
        // make sure the input item isn't blacklisted
        ItemMeta meta = item.getItemMeta();
        if (meta.hasCustomModelData() && cmdBlacklist.getOrDefault(item.getType(), Collections.emptyList()).contains(meta.getCustomModelData()))
            return;
        
        ItemStack newitem = tokenData.apply(item);
        ItemStack result = event.getInventory().getItem(RESULT_SLOT);
        
        if (!newitem.equals(result)) {
            plugin.getLogger().info("Custom Token: New Item doesn't equal expected one.");
            return;
        }
        
        ModelTokenApplyEvent applyEvent = new ModelTokenApplyEvent(player, item, token, result);
        Bukkit.getPluginManager().callEvent(applyEvent);
        
        if (applyEvent.isCancelled())
            return;
        
        // reduce item amounts
        if (item.getAmount() == 1)
            inventory.setItem(ITEM_SLOT, null);
        else
            item.setAmount(item.getAmount() - 1);
        
        if (token.getAmount() == 1)
            inventory.setItem(TOKEN_SLOT, null);
        else
            token.setAmount(token.getAmount() - 1);
        
        // set item to cursor, play sound
        
        player.setItemOnCursor(applyEvent.getResult());
        inventory.setItem(RESULT_SLOT, null);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f);
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> token = event.getDrops().stream().filter(a -> tokenMap.keySet().stream().anyMatch(a::isSimilar)).collect(Collectors.toList());
        
        event.getDrops().removeAll(token);
        new LambdaRunnable(() -> event.getEntity().getInventory().addItem(token.toArray(new ItemStack[0]))).runTask(plugin);
    }
    
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (isToken(event.getEntity().getItemStack()))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onItemSpawned(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        
        if (isToken(itemStack)) {
            ItemMeta meta = itemStack.getItemMeta();
            
            item.setInvulnerable(true);
            item.setCustomName(meta.hasDisplayName() ? meta.getDisplayName() : itemStack.getType().name());
            item.setCustomNameVisible(true);
            item.setGlowing(true);
        }
    }
    
    private TokenData getToken(ItemStack token) {
        return tokenMap.get(token);
    }
    
    public boolean isToken(ItemStack token) {
        return tokenMap.containsKey(token);
    }
    
    public Object addItem(ItemStack token, ItemStack item) {
        return tokenMap.put(token, TokenData.fromItemStack(item));
    }
    
    public boolean removeTokenByHash(int hash) {
        return tokenMap.entrySet().removeIf(a -> a.getKey().hashCode() == hash);
    }
    
    public Map<ItemStack, TokenData> getTokens() {
        return Collections.unmodifiableMap(tokenMap);
    }
    
    public ItemStack getTokenByHash(int hash) {
        return tokenMap.keySet().stream().filter(a -> a.hashCode() == hash).findFirst().orElse(null);
    }
    
    public ItemStack getItemByHash(int hash) {
        return tokenMap.entrySet().stream().filter(a -> a.getKey().hashCode() == hash).findFirst().map(a -> a.getValue().toItemStack()).orElse(null);
    }
    
    public boolean addBlacklist(Material mat, List<Integer> cmdList) {
        return cmdBlacklist.computeIfAbsent(mat, a -> new ArrayList<>()).addAll(cmdList);
    }
    
    public boolean removeBlacklist(Material mat, List<Integer> cmdList) {
        boolean result = cmdBlacklist.computeIfAbsent(mat, a -> new ArrayList<>()).removeAll(cmdList);
        cmdBlacklist.computeIfPresent(mat, (a, b) -> b.isEmpty() ? null : b);
        return result;
    }
    
    public Map<Material, List<Integer>> getBlacklist() {
        return Collections.unmodifiableMap(cmdBlacklist);
    }
}
