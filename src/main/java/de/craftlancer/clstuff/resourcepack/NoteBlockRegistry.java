package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteBlockRegistry implements Listener {
    
    private static NoteBlockRegistry instance;
    
    private CLStuff plugin;
    private NoteBlockItemList gui;
    private List<CustomBlock> customBlocks;
    private List<NoteBlockItem> noteBlockItems;
    
    public NoteBlockRegistry(CLStuff plugin) {
        ConfigurationSerialization.registerClass(CustomBlock.class);
        ConfigurationSerialization.registerClass(NoteBlockItem.class);
        instance = this;
        this.plugin = plugin;
        
        File file = new File(plugin.getDataFolder(), "noteBlockRegistry.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        noteBlockItems = (List<NoteBlockItem>) config.getList("noteBlockItems", new ArrayList<>());
        customBlocks = (List<CustomBlock>) config.getList("customBlocks", new ArrayList<>());
        
        gui = new NoteBlockItemList(plugin, NoteBlockItemList.getPageItems(this, noteBlockItems));
    }
    
    public void save() {
        File file = new File(plugin.getDataFolder(), "noteBlockRegistry.yml");
        
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        config.set("noteBlockItems", noteBlockItems);
        config.set("customBlocks", customBlocks);
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onNoteBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        
        if (block.getType() != Material.NOTE_BLOCK)
            return;
        
        if (!item.getItemMeta().hasCustomModelData())
            return;
        
        if (noteBlockItems.stream().noneMatch(n -> n.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()))
            return;
        
        NoteBlockItem noteBlockItem = noteBlockItems.stream()
                .filter(n -> n.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()).
                        findFirst().get();
        
        NoteBlock noteBlock = (NoteBlock) block.getBlockData();
        noteBlock.setNote(noteBlockItem.getNote());
        noteBlock.setInstrument(noteBlockItem.getInstrument());
        block.setBlockData(noteBlock);
        
        customBlocks.add(new CustomBlock(block.getLocation(), noteBlockItem));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onNoteBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        
        if (block.getType() != Material.NOTE_BLOCK)
            return;
        
        if (customBlocks.stream().noneMatch(n -> n.getLocation().equals(block.getLocation())))
            return;
        
        event.setDropItems(false);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            block.getWorld().dropItemNaturally(block.getLocation(),
                    customBlocks.stream().filter(n -> n.getLocation().equals(block.getLocation())).findFirst().get().getItem().getItem());
        customBlocks.removeIf(b -> b.getLocation().equals(block.getLocation()));
    }
    
    //Prevent
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)
            return;
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        Block block = event.getClickedBlock();
        
        if (block.getType() == Material.NOTE_BLOCK)
            if (customBlocks.stream().anyMatch(b -> b.getLocation().equals(block.getLocation())))
                event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPower(BlockRedstoneEvent event) {
        if (customBlocks.stream().filter(b -> Utils.isChunkLoaded(b.getLocation()))
                .anyMatch(b -> b.getLocation().equals(event.getBlock().getLocation())))
            event.setNewCurrent(event.getOldCurrent());
    }
    
    //Prevent changing instrument and note if block is saved as a custom noteblock.
    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        
        if (event.getBlock().getType() != Material.NOTE_BLOCK)
            return;
        
        if (customBlocks.stream().noneMatch(n -> n.getLocation().equals(block.getLocation())))
            return;
        
        NoteBlockItem noteBlockItem = customBlocks.stream().filter(n -> n.getLocation().equals(block.getLocation())).findFirst().get().getItem();
        NoteBlock noteBlock = (NoteBlock) block.getBlockData();
        
        if (noteBlockItem.getInstrument().equals(noteBlock.getInstrument()) && noteBlockItem.getNote().equals(noteBlock.getNote()))
            return;
        
        noteBlock.setNote(noteBlockItem.getNote());
        noteBlock.setInstrument(noteBlockItem.getInstrument());
        block.setBlockData(noteBlock);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(EntityExplodeEvent event) {
        customBlocks.removeIf(b -> {
            if (!Utils.isChunkLoaded(b.getLocation()) || !event.blockList().contains(b.getLocation().getBlock()))
                return false;
            
            event.blockList().remove(b.getLocation().getBlock());
            b.getLocation().getBlock().setType(Material.AIR);
            if (Math.random() < (double) event.getYield())
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), b.getItem().getItem());
            return true;
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        BlockFace face = event.getDirection();
        
        customBlocks.stream()
                .filter(b -> Utils.isChunkLoaded(b.getLocation()))
                .filter(b -> event.getBlocks().contains(b.getLocation().getBlock()))
                .forEach(b -> b.setLocation(b.getLocation().add(face.getModX(), face.getModY(), face.getModZ())));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonRetractEvent event) {
        BlockFace face = event.getDirection();
        
        customBlocks.stream()
                .filter(b -> Utils.isChunkLoaded(b.getLocation()))
                .filter(b -> event.getBlocks().contains(b.getLocation().getBlock()))
                .forEach(b -> b.setLocation(b.getLocation().add(face.getModX(), face.getModY(), face.getModZ())));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onNotePlay(NotePlayEvent event) {
        if (customBlocks.stream().anyMatch(b -> b.getLocation().equals(event.getBlock().getLocation())))
            event.setCancelled(true);
    }
    
    public List<NoteBlockItem> getNoteBlockItems() {
        return noteBlockItems;
    }
    
    public List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }
    
    public NoteBlockItemList getGui() {
        return gui;
    }
    
    public void removeNoteBlockItem(NoteBlockItem item) {
        noteBlockItems.remove(item);
        gui.setPageItems(NoteBlockItemList.getPageItems(this, noteBlockItems));
    }
    
    public void addNoteBlockItem(NoteBlockItem item) {
        noteBlockItems.add(item);
        gui.setPageItems(NoteBlockItemList.getPageItems(this, noteBlockItems));
    }
    
    protected static NoteBlockRegistry getInstance() {
        return instance;
    }
}
