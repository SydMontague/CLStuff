package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.menu.ConditionalPagedMenu;
import de.craftlancer.core.menu.MenuItem;
import de.craftlancer.core.resourcepack.TranslateSpaceFont;
import de.craftlancer.core.util.ItemBuilder;
import de.craftlancer.core.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomBlockRegistry implements Listener {
    
    private static CustomBlockRegistry instance;
    
    private final EnumSet<Material> blockTypes = EnumSet.of(Material.NOTE_BLOCK, Material.TRIPWIRE);
    private final EnumSet<Material> itemTypes = EnumSet.of(Material.NOTE_BLOCK, Material.STRING);
    private CLStuff plugin;
    private ConditionalPagedMenu gui;
    private List<CustomBlockItem> customBlockItems;
    
    private Map<Location, BlockState> runTimeCustomBlocks = new HashMap<>();
    
    public CustomBlockRegistry(CLStuff plugin) {
        ConfigurationSerialization.registerClass(CustomBlockItem.class);
        ConfigurationSerialization.registerClass(CustomTripwireItem.class);
        ConfigurationSerialization.registerClass(CustomNoteBlockItem.class);
        instance = this;
        this.plugin = plugin;
        
        File file = new File(plugin.getDataFolder(), "customBlockRegistry.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        customBlockItems = (List<CustomBlockItem>) config.getList("customBlockItems", new ArrayList<>());
    }
    
    public void save() {
        File file = new File(plugin.getDataFolder(), "customBlockRegistry.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        config.set("customBlockItems", customBlockItems);
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCustomBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!itemTypes.contains(item.getType()))
            return;
        
        if (!item.getItemMeta().hasCustomModelData()) {
            player.sendMessage("§cThis item cannot be placed.");
            event.setCancelled(true);
            return;
        }
        
        Optional<CustomBlockItem> optional = customBlockItems.stream()
                .filter(n -> n.getItemMaterial() == item.getType() &&
                        n.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()).
                        findFirst();
        
        if (!optional.isPresent())
            return;
        
        CustomBlockItem customBlockItem = optional.get();
        
        customBlockItem.setBlockData(block);
        
        runTimeCustomBlocks.put(block.getLocation(), block.getState());
        new LambdaRunnable(() -> runTimeCustomBlocks.get(block.getLocation()).update(true)).runTaskLater(plugin, 1);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCustomBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        
        if (!blockTypes.contains(block.getType()))
            return;
        
        runTimeCustomBlocks.remove(block.getLocation());
        
        if (dropCustomBlockIfPresent(block))
            event.setDropItems(false);
    }
    
    //Prevent
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking())
            return;
        
        Block block = event.getClickedBlock();
        
        if (block.getType() != Material.NOTE_BLOCK)
            return;
        
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Material material = item.getType();
        
        if (item.getType().isAir() || !item.getType().isBlock())
            return;
        
        BlockData tempData = material.createBlockData();
        
        if (tempData instanceof Attachable
                || tempData instanceof Rotatable
                || tempData instanceof Directional)
            return;
        
        Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
        BlockState state = placed.getState();
        Block against = event.getClickedBlock();
        
        placed.setType(item.getType());
        
        runTimeCustomBlocks.remove(placed.getLocation());
        
        BlockPlaceEvent e = new BlockPlaceEvent(placed, state, against, item, player, true, EquipmentSlot.HAND);
        
        Bukkit.getPluginManager().callEvent(e);
        
        if (e.isCancelled()) {
            placed.setType(Material.AIR);
            return;
        }
        
        if (player.getGameMode() != GameMode.CREATIVE)
            item.setAmount(item.getAmount() - 1);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPower(BlockRedstoneEvent event) {
        if (blockTypes.contains(event.getBlock().getType()))
            event.setNewCurrent(event.getOldCurrent());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockDestroy(BlockFromToEvent event) {
        final Block block = event.getToBlock();
        
        if (event.getBlock().getType() == Material.LAVA)
            return;
        
        if (!blockTypes.contains(block.getType()))
            return;
        
        runTimeCustomBlocks.remove(block.getLocation());
        
        if (dropCustomBlockIfPresent(block)) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
    
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        
        if (!blockTypes.contains(block.getType()))
            return;
        
        if (!runTimeCustomBlocks.containsKey(block.getLocation()))
            runTimeCustomBlocks.put(block.getLocation(), block.getState());
        
        runTimeCustomBlocks.get(block.getLocation()).update(true);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> {
            if (!blockTypes.contains(b.getType()))
                return false;
            
            Optional<CustomBlockItem> optional = getCustomBlockItem(b);
            
            if (!optional.isPresent())
                return false;
            
            runTimeCustomBlocks.remove(b.getLocation());
            b.getLocation().getBlock().setType(Material.AIR);
            if (Math.random() < (double) event.getYield())
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), optional.get().getItem());
            return true;
        });
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        
        BlockFace face = event.getDirection();
        Map<Location, BlockState> map = new HashMap<>();
        for (Block block : event.getBlocks()) {
            if (block.getType() == Material.NOTE_BLOCK)
                map.put(block.getLocation().add(face.getModX(), face.getModY(), face.getModZ()),
                        runTimeCustomBlocks.getOrDefault(block.getLocation(), block.getState()));
            if (block.getType() == Material.TRIPWIRE)
                if (dropCustomBlockIfPresent(block))
                    block.setType(Material.AIR);
            
        }
        
        for (Block key : event.getBlocks())
            runTimeCustomBlocks.remove(key.getLocation());
        
        map.forEach((loc, run) -> runTimeCustomBlocks.put(loc, run));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        BlockFace face = event.getDirection();
        Map<Location, BlockState> map = new HashMap<>();
        for (Block block : event.getBlocks()) {
            if (block.getType() == Material.NOTE_BLOCK)
                map.put(block.getLocation().add(face.getModX(), face.getModY(), face.getModZ()),
                        runTimeCustomBlocks.getOrDefault(block.getLocation(), block.getState()));
            if (block.getType() == Material.TRIPWIRE)
                dropCustomBlockIfPresent(block);
        }
        
        for (Block key : event.getBlocks())
            runTimeCustomBlocks.remove(key.getLocation());
        
        map.forEach((loc, run) -> runTimeCustomBlocks.put(loc, run));
    }
    
    public void createInventory() {
        gui = new ConditionalPagedMenu(plugin, 6, getPageItems(), true, true,
                Arrays.asList(new Tuple<>("default", "Custom Blocks"),
                        new Tuple<>("resource", "§f" + TranslateSpaceFont.TRANSLATE_NEGATIVE_8 + "\uE300")));
    }
    
    private List<MenuItem> getPageItems() {
        return customBlockItems.stream().map(item -> {
            MenuItem pageItem = new MenuItem(new ItemBuilder(item.getItem()).addLore("", "§6§lLEFT CLICK §eto recieve item.", "§6§lSHIFT RIGHT CLICK §eto remove item.").build());
            pageItem.addClickAction(c -> c.getPlayer().getInventory().addItem(item.getItem().clone()), ClickType.LEFT);
            pageItem.addClickAction(c -> {
                removeCustomBlockItem(item);
                gui.display(c.getPlayer(), c.getMenuKey());
            }, ClickType.SHIFT_RIGHT);
            return pageItem;
        }).collect(Collectors.toList());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onNotePlay(NotePlayEvent event) {
        event.setCancelled(true);
    }
    
    public Optional<CustomBlockItem> getCustomBlockItem(Block block) {
        return customBlockItems.stream().filter(b -> b.equals(block)).findFirst();
    }
    
    public boolean dropCustomBlockIfPresent(Block block) {
        Optional<CustomBlockItem> optional = getCustomBlockItem(block);
        
        if (!optional.isPresent())
            return false;
        
        block.getWorld().dropItemNaturally(block.getLocation(), optional.get().getItem());
        
        return true;
    }
    
    public List<CustomBlockItem> getCustomBlockItems() {
        return customBlockItems;
    }
    
    public ConditionalPagedMenu getGui() {
        if (gui == null)
            createInventory();
        
        return gui;
    }
    
    public void removeCustomBlockItem(CustomBlockItem item) {
        customBlockItems.remove(item);
        
        if (gui == null)
            createInventory();
        
        gui.setPageItems(getPageItems());
    }
    
    public void addCustomBlockItem(CustomBlockItem item) {
        customBlockItems.add(item);
        
        if (gui == null)
            createInventory();
        
        gui.setPageItems(getPageItems());
    }
    
    protected static CustomBlockRegistry getInstance() {
        return instance;
    }
    
}