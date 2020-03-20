package de.craftlancer.clstuff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import de.craftlancer.core.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class WGNoDropFlag implements Listener, TabExecutor {
    private static final StateFlag NO_DROP_FLAG = new StateFlag("keep-inventory", false);
    
    private WorldGuard wg;
    private WorldGuardPlugin wgPlugin;
    
    private File configFile;
    private List<ItemStack> excluded = new ArrayList<>();
    
    public static void registerFlag() {
        WorldGuard.getInstance().getFlagRegistry().register(NO_DROP_FLAG);
    }
    
    @SuppressWarnings("unchecked")
    public WGNoDropFlag(CLStuff plugin) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null)
            return;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        
        configFile = new File(plugin.getDataFolder(), "noDrop.yml");
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        excluded = (List<ItemStack>) config.getList("excludedItems", new ArrayList<>());
        
        plugin.getCommand("nodropflag").setExecutor(this);
        
        wg = WorldGuard.getInstance();
        wgPlugin = WorldGuardPlugin.inst();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("clstuff.nodrop.admin"))
            return true;
        
        if (args.length < 1)
            return false;
        
        switch (args[0]) {
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("You must be a player to do this.");
                    return true;
                }
                
                Player p = (Player) sender;
                ItemStack stack = p.getEquipment().getItemInMainHand();
                
                if (!stack.getType().isAir() && !excluded.contains(stack)) {
                    excluded.add(stack);
                    saveExcludedList();
                    sender.sendMessage("Item added to exclusion list.");
                }
                else
                    sender.sendMessage("Couldn't add item to exclusion list (already on it?)");
                
                break;
            case "list":
                printExcludedList(sender);
                break;
            case "remove":
                if (args.length < 2)
                    return false;
                
                try {
                    int id = Integer.parseInt(args[1]);
                    if (id < excluded.size()) {
                        excluded.remove(id);
                        sender.sendMessage("Item successfully removed.");
                        printExcludedList(sender);
                        saveExcludedList();
                    }
                    else
                        sender.sendMessage("Given item index out of bounds.");
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("You must give an item index as number.");
                }
                break;
            default:
        }
        
        return true;
    }
    
    private void saveExcludedList() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("excludedItems", excluded);
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            Bukkit.getLogger().warning("[NoDropFlag] Error while saving config.");
        }
    }
    
    private void printExcludedList(CommandSender sender) {
        int i = 0;
        sender.sendMessage("Currently excluded items:");
        for (ItemStack a : excluded) {
            BaseComponent delete = new TextComponent("[Delete]");
            delete.setColor(ChatColor.RED);
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nodropflag remove " + i));
            
            BaseComponent comp = new TextComponent(Integer.toString(i++));
            comp.addExtra(" - ");
            comp.addExtra(Utils.getItemComponent(a));
            comp.addExtra(" | ");
            comp.addExtra(delete);
            
            sender.spigot().sendMessage(comp);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            Arrays.asList("add", "list", "remove").stream().filter(a -> a.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        
        return Collections.emptyList();
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        LocalPlayer wgPlayer = wgPlugin.wrapPlayer(player);
        
        com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = wg.getPlatform().getRegionContainer().createQuery();
        StateFlag.State state = query.queryState(wgLoc, wgPlayer, NO_DROP_FLAG);
        
        if (state == State.ALLOW) {
            e.setKeepInventory(true);
            e.setKeepLevel(true);
            e.setDroppedExp(0);
            e.getDrops().removeIf(a -> excluded.stream().noneMatch(a::isSimilar));
            e.getDrops().forEach(a -> player.getInventory().removeItem(a));
        }
    }
    
}