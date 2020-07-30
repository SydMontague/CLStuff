package de.craftlancer.clstuff.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.gui.GUIInventory;
import de.craftlancer.core.items.CustomItemRegistry;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;

class ArenaEntry {
    private final Plugin plugin;
    
    private final Location location;
    private final Location spawnLocation;
    private final String name;
    private final List<ArenaMob> mobs;
    
    private GUIInventory gui;
    private final List<String> mobNames;
    
    public GUIInventory getGUI() {
        return gui;
    }
    
    public ArenaEntry(Plugin plugin, Location loc, Location spawnLoc, String name2, List<ArenaMob> mob) {
        this.plugin = plugin;
        this.location = loc;
        this.spawnLocation = spawnLoc;
        this.name = name2;
        this.mobs = mob;
        this.mobNames = mob.stream().filter(a -> a != null).flatMap(a -> a.getDescription().stream()).collect(Collectors.toList());
        
        buildInventory();
    }

    private void buildInventory() {
        gui = new GUIInventory(plugin, name, 1 + mobs.size() / 9);
        
        CustomItemRegistry itemRegistry = CLCore.getInstance().getItemRegistry();
        
        int slotId = 0;
        for (ArenaMob mob : mobs) {
            ItemStack item = itemRegistry.getItem(mob.getHead()).orElseGet(() -> new ItemStack(Material.AIR));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(mob.getName());
            
            List<String> lore = new ArrayList<>(mob.getDescription());
            if (!mob.getCosts().isEmpty())
                lore.add("Costs:");
            
            for (ArenaCost cost : mob.getCosts()) {
                if (cost.getType().equalsIgnoreCase("money"))
                    lore.add("Money: " + cost.getAmount());
                else {
                    ItemMeta i = itemRegistry.getItem(cost.getType()).orElseGet(() -> new ItemStack(Material.AIR)).getItemMeta();
                    lore.add(String.format("%dx %s", cost.getAmount(), i.hasDisplayName() ? i.getDisplayName() : "INVALID NAME"));
                }
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            gui.setItem(slotId, item);
            gui.setClickAction(slotId, p -> doClick(p, mob));
            
            slotId++;
        }
    }
    
    @SuppressWarnings("resource")
    private void doClick(Player p, ArenaMob mob) {
        // already a mob active
        if (MythicMobs.inst().getMobManager().getActiveMobs().stream().anyMatch(a -> mobNames.contains(a.getType().getInternalName()))) {
            MessageUtil.sendMessage(plugin, p, MessageLevel.WARNING, "There is already a mob of this spawner active.");
            return;
        }
        
        // cost check
        boolean canPay = true;
        for (ArenaCost c : mob.getCosts()) {
            if (c.getType().equalsIgnoreCase("money")) {
                if (!CLCore.getInstance().getEconomy().has(p, c.getAmount()))
                    canPay = false;
            }
            else if (!p.getInventory().containsAtLeast(CLCore.getInstance().getItemRegistry().getItem(c.getType()).orElse(new ItemStack(Material.AIR)),
                                                       c.getAmount()))
                canPay = false;
        }
        
        if (!canPay) {
            MessageUtil.sendMessage(plugin, p, MessageLevel.WARNING, "You can't affort to spawn this mob.");
            return;
        }
        
        // pay costs
        for (ArenaCost c : mob.getCosts()) {
            if (c.getType().equalsIgnoreCase("money")) {
                CLCore.getInstance().getEconomy().withdrawPlayer(p, c.getAmount());
            }
            else {
                ItemStack item = CLCore.getInstance().getItemRegistry().getItem(c.getType()).orElse(new ItemStack(Material.AIR));
                item.setAmount(c.getAmount());
                p.getInventory().removeItem(item);
            }
        }
        
        // spawn mob
        try {
            for (String m : mob.getMobs())
                MythicMobs.inst().getAPIHelper().spawnMythicMob(m, spawnLocation);
        }
        catch (InvalidMobTypeException e) {
            e.printStackTrace();
        }
        
        p.closeInventory();
        MessageUtil.sendMessage(plugin, p, MessageLevel.NORMAL, "Boss Mob spawned.");
    }
    
    public Location getLocation() {
        return location;
    }
}
