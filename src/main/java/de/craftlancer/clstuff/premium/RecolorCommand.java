package de.craftlancer.clstuff.premium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.craftlancer.core.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class RecolorCommand implements TabExecutor, Listener {
    
    private final List<String> dyableItems = Arrays.asList("CONCRETE", "BED", "TERRACOTTA", "GLASS", "WOOL", "CARPET");
    private final List<String> dyeNames = Arrays.asList("Black",
                                                        "Yellow",
                                                        "Orange",
                                                        "Green",
                                                        "Blue",
                                                        "Lime",
                                                        "Gray",
                                                        "Light_Gray",
                                                        "White",
                                                        "Red",
                                                        "Magenta",
                                                        "Purple",
                                                        "Brown",
                                                        "Pink",
                                                        "Cyan",
                                                        "Light_Blue");
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player))
            return false;
        
        Player player = (Player) commandSender;
        
        // Check for permission
        if (!player.hasPermission("clstuff.recolor")) {
            player.sendMessage("§f[§4Craft§fCitizen]§c You do not have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("§f[§4Craft§fCitizen]§e You must specify a color!");
            return true;
        }
        
        // Is the player holding an item that can be dyed
        String itemName = player.getInventory().getItemInMainHand().getType().name();
        if (dyableItems.stream().noneMatch(itemName::contains)) {
            player.sendMessage("§f[§4Craft§fCitizen]§e You are not holding an item that is dyeable!");
            return true;
        }
        
        // If the material isn't valid, return
        Material dye;
        try {
            dye = Material.valueOf(args[0].toUpperCase() + "_DYE");
        }
        catch (IllegalArgumentException e) {
            player.sendMessage("§f[§4Craft§fCitizen]§e You must enter a valid color! Use tab complete to help you.");
            return true;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        
        Material material = getMaterial(item.getType(), dye);
        
        if (material == null) {
            player.sendMessage("§f[§4Craft§fCitizen]§e You are not holding an item that is dyeable!");
            return true;
        }
        
        if (material.equals(item.getType())) {
            player.sendMessage("§f[§4Craft§fCitizen]§e The color you specified is the same as the item's color.");
            return true;
        }
        item.setType(material);
        player.getInventory().setItemInMainHand(item);
        player.updateInventory();
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CORAL_BLOCK_HIT, 0.2F, 1F);
        
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1)
            return Utils.getMatches(args[0], dyeNames);
        return new ArrayList<>();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDyeApply(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) || !player.isSneaking())
            return;
        
        Block block = event.getClickedBlock();
        
        if (block == null)
            return;
        
        if (!player.hasPermission("clstuff.recolor"))
            return;
        
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        
        if (!item.getType().name().contains("DYE"))
            return;
        
        // If there is a claim, can the player build in it?
        if (GriefPrevention.instance.isEnabled()) {
            Claim claim = null;
            for (Claim c : GriefPrevention.instance.dataStore.getClaims())
                if (c.contains(block.getLocation(), true, false))
                    claim = c;
            if (claim == null)
                return;
            if (!claim.getOwnerName().equals(player.getName()))
                return;
            if (claim.allowBuild(player, block.getType()) != null)
                return;
        }
        
        // If there is a WG region, can the player build in it?
        if (!canPlayerBuildInRegion(block.getLocation(), player))
            return;
        
        Material material = getMaterial(block.getType(), item.getType());
        
        if (material == null || block.getType() == material)
            return;
        
        block.setType(material);
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CORAL_BLOCK_HIT, 0.2F, 1F);
        
    }
    
    private Material getMaterial(Material item, Material dye) {
        String color = dye.name().replace("_DYE", "");
        String type = item.name();
        String materialString = null;
        
        if (type.contains("CARPET"))
            materialString = color + "_CARPET";
        else if (type.contains("CONCRETE_POWDER"))
            materialString = color + "_CONCRETE_POWDER";
        else if (type.contains("CONCRETE"))
            materialString = color + "_CONCRETE";
        else if (type.contains("STAINED_GLASS_PANE") || type.contains("GLASS_PANE"))
            materialString = color + "_STAINED_GLASS_PANE";
        else if (type.contains("STAINED_GLASS") || type.contains("GLASS"))
            materialString = color + "_STAINED_GLASS";
        else if (type.contains("GLAZED_TERRACOTTA"))
            materialString = color + "_GLAZED_TERRACOTTA";
        else if (type.contains("TERRACOTTA"))
            materialString = color + "_TERRACOTTA";
        else if (type.contains("WOOL"))
            materialString = color + "_WOOL";
        
        if (materialString == null)
            return null;
        
        if (!item.name().contains(type))
            return null;
        
        return Material.valueOf(materialString);
    }
    
    boolean canPlayerBuildInRegion(Location loc, Player player) {
        if (player.isOp())
            return true;
        
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(loc.getWorld()));
        if (WorldGuard.getInstance() == null || regions == null)
            return true;
        BlockVector3 position = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        ApplicableRegionSet set = regions.getApplicableRegions(position);
        if (set.size() == 0)
            return true;
        
        ProtectedRegion topRegion = null;
        
        for (ProtectedRegion region : set) {
            if (topRegion == null || region.getPriority() > topRegion.getPriority())
                topRegion = region;
        }
        
        if (topRegion == null)
            return false;
        
        return topRegion.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }
}
