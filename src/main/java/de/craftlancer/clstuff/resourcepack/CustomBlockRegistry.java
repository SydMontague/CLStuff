package de.craftlancer.clstuff.resourcepack;

import com.google.common.collect.Sets;
import de.craftlancer.clapi.LazyService;
import de.craftlancer.clapi.clfeatures.AbstractFeature;
import de.craftlancer.clapi.clfeatures.AbstractManualPlacementFeature;
import de.craftlancer.clapi.clfeatures.PluginCLFeatures;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.command.CustomBlockCommandHandler;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.menu.ConditionalPagedMenu;
import de.craftlancer.core.menu.MenuItem;
import de.craftlancer.core.resourcepack.TranslateSpaceFont;
import de.craftlancer.core.util.ItemBuilder;
import de.craftlancer.core.util.Tuple;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.world.StructureGrowEvent;
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
    private static final LazyService<PluginCLFeatures> CLFEATURES = new LazyService<>(PluginCLFeatures.class);
    private static CustomBlockRegistry instance;
    
    private final Map<Material, CustomBlockItem> defaults = new HashMap<>();
    private final EnumSet<Material> blockTypes = EnumSet.of(Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);
    private final EnumSet<Material> itemTypes = EnumSet.of(Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);
    private CLStuff plugin;
    private ConditionalPagedMenu gui;
    private List<CustomBlockItem> customBlockItems;
    
    private Map<Block, BlockState> runTimeCustomBlocks = new HashMap<>();
    
    public CustomBlockRegistry(CLStuff plugin) {
        ConfigurationSerialization.registerClass(CustomBlockItem.class);
        ConfigurationSerialization.registerClass(CustomMushroomItem.class);
//        ConfigurationSerialization.registerClass(CustomTripwireItem.class);
//        ConfigurationSerialization.registerClass(CustomNoteBlockItem.class);
        instance = this;
        this.plugin = plugin;
        
        File file = new File(plugin.getDataFolder(), "customBlockRegistry.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        customBlockItems = (List<CustomBlockItem>) config.getList("customBlockItems", new ArrayList<>());
        
        defaults.put(Material.RED_MUSHROOM_BLOCK, new CustomMushroomItem("defaultRedMushroom", new ItemStack(Material.RED_MUSHROOM),
                Sets.newHashSet(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN), false));
        defaults.put(Material.BROWN_MUSHROOM_BLOCK, new CustomMushroomItem("defaultBrownMushroom", new ItemStack(Material.BROWN_MUSHROOM),
                Sets.newHashSet(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN), false));
        
        plugin.getCommand("customblock").setExecutor(new CustomBlockCommandHandler(plugin, this));
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        final ItemStack item = player.getInventory().getItemInMainHand().clone();
        
        if (!itemTypes.contains(item.getType()))
            return;

//        if (item.getType() == Material.STRING && block.getLocation().getY() > 0)
//            if (!block.getRelative(0, -1, 0).getType().isSolid()) {
//                event.setCancelled(true);
//                return;
//            }
        
        Optional<CustomBlockItem> optional = customBlockItems.stream()
                .filter(n -> n.compareItem(item)).findFirst();
        
        CustomBlockItem customBlockItem;
        
        if (!optional.isPresent()) {
            if (defaults.containsKey(item.getType()))
                customBlockItem = defaults.get(item.getType());
            else {
                player.sendMessage("§cThis item cannot be placed.");
                event.setCancelled(true);
                return;
            }
        } else
            customBlockItem = optional.get();
        
        CustomBlockPlaceEvent e = new CustomBlockPlaceEvent(event, customBlockItem);
        
        Bukkit.getPluginManager().callEvent(e);
        
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        
        AbstractFeature feature = CLFEATURES.get().getFeature(customBlockItem.getId());
        
        if (feature instanceof AbstractManualPlacementFeature) {
            if (!feature.checkFeatureLimit(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You've reached your limit for this feature.");
                event.setCancelled(true);
                return;
            } else
                ((AbstractManualPlacementFeature) feature).createInstance(event.getPlayer(), event.getBlock(), event.getItemInHand().clone());
        }
        
        block.setBlockData(customBlockItem.getBlockData(block.getBlockData()));
        
        runTimeCustomBlocks.put(block, block.getState());
        new LambdaRunnable(() -> runTimeCustomBlocks.get(block).update(true)).runTaskLater(plugin, 1);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCustomBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        
        if (!blockTypes.contains(block.getType()))
            return;
        
        runTimeCustomBlocks.remove(block);
        
        if (dropCustomBlockIfPresent(block))
            event.setDropItems(false);
    }

//    //Prevent
//    @EventHandler(ignoreCancelled = true)
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (event.getAction() == Action.PHYSICAL)
//            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TRIPWIRE)
//                event.setCancelled(true);
//
//        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking())
//            return;
//
//        Block block = event.getClickedBlock();
//
//        if (block.getType() != Material.NOTE_BLOCK)
//            return;
//
//        event.setCancelled(true);
//
//        Player player = event.getPlayer();
//        ItemStack item = player.getInventory().getItemInMainHand();
//        Material material = item.getType();
//
//        if (item.getType().isAir() || !item.getType().isBlock())
//            return;
//
//        BlockData tempData = material.createBlockData();
//
//        if (tempData instanceof Attachable
//                || tempData instanceof Rotatable
//                || tempData instanceof Directional)
//            return;
//
//        Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
//        BlockState state = placed.getState();
//        Block against = event.getClickedBlock();
//
//        placed.setType(item.getType());
//
//        runTimeCustomBlocks.remove(placed.getLocation());
//
//        BlockPlaceEvent e = new BlockPlaceEvent(placed, state, against, item, player, true, EquipmentSlot.HAND);
//
//        Bukkit.getPluginManager().callEvent(e);
//
//        if (e.isCancelled()) {
//            placed.setType(Material.AIR);
//            return;
//        }
//
//        if (player.getGameMode() != GameMode.CREATIVE)
//            item.setAmount(item.getAmount() - 1);
//    }

//    @EventHandler(ignoreCancelled = true)
//    public void onBlockPower(BlockRedstoneEvent event) {
//        if (blockTypes.contains(event.getBlock().getType()))
//            event.setNewCurrent(event.getOldCurrent());
//    }

//    @EventHandler(ignoreCancelled = true)
//    public void onBlockDestroy(BlockFromToEvent event) {
//        final Block block = event.getToBlock();
//
//        if (event.getBlock().getType() == Material.LAVA)
//            return;
//
//        if (!blockTypes.contains(block.getType()))
//            return;
//
//        runTimeCustomBlocks.remove(block.getLocation());
//
//        if (dropCustomBlockIfPresent(block)) {
//            event.setCancelled(true);
//            block.setType(Material.AIR);
//        }
//    }
    
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        
        if (!blockTypes.contains(block.getType()))
            return;
        
        if (!runTimeCustomBlocks.containsKey(block))
            runTimeCustomBlocks.put(block, block.getState());
        
        
        runTimeCustomBlocks.get(block).update(true);
        event.setCancelled(true);
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
        if (event.getBlocks().stream().anyMatch(b -> blockTypes.contains(b.getType())))
            event.setCancelled(true);

//        for (Block block : event.getBlocks()) {
//            if (block.getType() == Material.TRIPWIRE)
//                if (dropCustomBlockIfPresent(block))
//                    block.setType(Material.AIR);
//
//        }
//
//        for (Block key : event.getBlocks())
//            runTimeCustomBlocks.remove(key.getLocation());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> blockTypes.contains(b.getType())))
            event.setCancelled(true);

//        for (Block block : event.getBlocks()) {
//            if (block.getType() == Material.TRIPWIRE)
//                if (dropCustomBlockIfPresent(block))
//                    block.setType(Material.AIR);
//
//        }
//
//        for (Block key : event.getBlocks())
//            runTimeCustomBlocks.remove(key.getLocation());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMushroomGrow(StructureGrowEvent event) {
        TreeType type = event.getSpecies();
        
        if (type != TreeType.BROWN_MUSHROOM && type != TreeType.RED_MUSHROOM)
            return;
        
        event.getBlocks().stream().filter(b -> blockTypes.contains(b.getType()))
                .forEach(state -> {
                    CustomBlockItem def = type == TreeType.RED_MUSHROOM ? defaults.get(Material.RED_MUSHROOM_BLOCK) : defaults.get(Material.BROWN_MUSHROOM_BLOCK);
                    
                    state.setBlockData(def.getBlockData(state.getBlockData()));
                    state.update(true);
                });
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

//    @EventHandler(ignoreCancelled = true)
//    public void onNotePlay(NotePlayEvent event) {
//        event.setCancelled(true);
//    }
    
    public Optional<CustomBlockItem> getCustomBlockItem(Block block) {
        return customBlockItems.stream().filter(b -> b.equals(block.getBlockData())).findFirst();
    }
    
    public boolean dropCustomBlockIfPresent(Block block) {
        Optional<CustomBlockItem> optional = getCustomBlockItem(block);
        
        if (!optional.isPresent())
            return false;
        
        if (!optional.get().isDropItem())
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
