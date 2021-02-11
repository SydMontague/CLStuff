package de.craftlancer.clstuff.citizensets;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.gui.PageItem;
import de.craftlancer.core.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CitizenSet implements ConfigurationSerializable {
    private String name;
    private String id;
    private ItemStack icon;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;
    
    private List<ItemStack> others;
    private List<CitizenSetFunction> functions;
    private CitizenSetPageGUI gui;
    private Map<UUID, BukkitTask> playerRunnableMap = new HashMap<>();
    
    public CitizenSet(String name, String id, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack mainHand, ItemStack offHand, List<ItemStack> others) {
        this.icon = new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatColor.AQUA + id).setLore("", ChatColor.GRAY + "This is an example icon").build();
        this.id = id;
        this.name = name;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.others = others;
        this.functions = new ArrayList<>();
    }
    
    public CitizenSet(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.id = (String) map.get("id");
        this.icon = (ItemStack) map.getOrDefault("icon", null);
        this.helmet = (ItemStack) map.getOrDefault("helmet", null);
        this.chestplate = (ItemStack) map.getOrDefault("chestplate", null);
        this.leggings = (ItemStack) map.getOrDefault("leggings", null);
        this.boots = (ItemStack) map.getOrDefault("boots", null);
        this.mainHand = (ItemStack) map.getOrDefault("mainHand", null);
        this.offHand = (ItemStack) map.getOrDefault("offHand", null);
        this.others = (List<ItemStack>) map.getOrDefault("others", new ArrayList<>());
        this.functions = (List<CitizenSetFunction>) map.getOrDefault("functions", new ArrayList<>());
    }
    
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("name", name);
        map.put("id", id);
        map.put("icon", icon);
        if (helmet != null)
            map.put("helmet", helmet);
        if (chestplate != null)
            map.put("chestplate", chestplate);
        if (leggings != null)
            map.put("leggings", leggings);
        if (boots != null)
            map.put("boots", boots);
        if (mainHand != null)
            map.put("mainHand", mainHand);
        if (offHand != null)
            map.put("offHand", offHand);
        map.put("others", others);
        map.put("functions", functions);
        
        return map;
    }
    
    public boolean isCompleteSet(PlayerInventory inventory) {
        if (!areSimilar(helmet, inventory.getHelmet()))
            return false;
        if (!areSimilar(chestplate, inventory.getChestplate()))
            return false;
        if (!areSimilar(leggings, inventory.getLeggings()))
            return false;
        if (!areSimilar(boots, inventory.getBoots()))
            return false;
        if (!areSimilar(mainHand, inventory.getItemInMainHand()))
            return false;
        if (!areSimilar(offHand, inventory.getItemInOffHand()))
            return false;
        return others.stream().allMatch(item -> inventory.containsAtLeast(item, item.getAmount()));
    }
    
    private void createGUI() {
        List<PageItem> list = new ArrayList<>();
        
        if (!isAir(helmet))
            list.add(new PageItem(new ItemBuilder(helmet).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "HELMET").build()));
        if (!isAir(chestplate))
            list.add(new PageItem(new ItemBuilder(chestplate).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "CHESTPLATE").build()));
        if (!isAir(leggings))
            list.add(new PageItem(new ItemBuilder(leggings).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "LEGGINGS").build()));
        if (!isAir(boots))
            list.add(new PageItem(new ItemBuilder(boots).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BOOTS").build()));
        if (!isAir(mainHand))
            list.add(new PageItem(new ItemBuilder(mainHand).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "MAIN HAND").build()));
        if (!isAir(offHand))
            list.add(new PageItem(new ItemBuilder(offHand).addLore("", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "OFF HAND").build()));
        others.forEach(i -> list.add(new PageItem(i.clone())));
        
        gui = new CitizenSetPageGUI(CLStuff.getInstance(), list, CLStuff.getInstance().getCitizenSets());
    }
    
    /**
     * Compares the material and lore of items to see if they are similar or not.
     *
     * @return true if they are similar, false if not
     */
    private boolean areSimilar(ItemStack setItem, ItemStack item) {
        if (isAir(setItem))
            return true;
        if (isAir(item))
            return false;
        if (item.getType() != setItem.getType())
            return false;
        if (!item.getItemMeta().getPersistentDataContainer().has(CitizenSetsManager.KEY, PersistentDataType.STRING))
            return false;
        if (!setItem.getItemMeta().getPersistentDataContainer().has(CitizenSetsManager.KEY, PersistentDataType.STRING))
            return false;
        return setItem.getItemMeta().getPersistentDataContainer().get(CitizenSetsManager.KEY, PersistentDataType.STRING)
                .equals(item.getItemMeta().getPersistentDataContainer().get(CitizenSetsManager.KEY, PersistentDataType.STRING));
    }
    
    private boolean isAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
    
    public boolean contains(Player player) {
        return playerRunnableMap.containsKey(player.getUniqueId());
    }
    
    public void run(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1F, 1F);
        
        remove(player.getUniqueId());
        playerRunnableMap.put(player.getUniqueId(),
                new FunctionRunnable(player, this).runTaskTimer(CLStuff.getInstance(), 0, 1));
    }
    
    
    public void remove(UUID playerUUID) {
        playerRunnableMap.computeIfPresent(playerUUID, (k, v) -> {
            v.cancel();
            return null;
        });
    }
    
    public void addFunction(CitizenSetFunction function) {
        functions.add(function);
    }
    
    public void removeFunction(String functionId) {
        functions.removeIf(f -> f.getId().equals(functionId));
    }
    
    public List<CitizenSetFunction> getFunctions() {
        return functions;
    }
    
    public CitizenSetPageGUI getGui() {
        if (gui == null)
            createGUI();
        return gui;
    }
    
    public void setIcon(ItemStack icon) {
        this.icon = icon;
        CLStuff.getInstance().getCitizenSets().createGui();
    }
    
    public ItemStack getIcon() {
        return icon;
    }
    
    public String getId() {
        return id;
    }
    
    public ItemStack getHelmet() {
        return helmet;
    }
    
    public ItemStack getChestplate() {
        return chestplate;
    }
    
    public ItemStack getLeggings() {
        return leggings;
    }
    
    public ItemStack getBoots() {
        return boots;
    }
    
    public ItemStack getMainHand() {
        return mainHand;
    }
    
    public ItemStack getOffHand() {
        return offHand;
    }
    
    public List<ItemStack> getOthers() {
        return others;
    }
    
    private static class FunctionRunnable extends BukkitRunnable {
        
        private Player player;
        private CitizenSet set;
        private long tickId;
        
        public FunctionRunnable(Player player, CitizenSet set) {
            this.player = player;
            this.tickId = 0;
            this.set = set;
        }
        
        @Override
        public void run() {
            set.getFunctions().forEach(f -> f.run(player, tickId));
            tickId++;
        }
    }
    
    public String getName() {
        return name;
    }
}
