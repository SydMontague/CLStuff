package de.craftlancer.clstuff.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilderCommand implements CommandExecutor, TabCompleter {
    
    private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.BOLD + "" + ChatColor.WHITE + "ItemBuilder" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW;
    
    private CLStuff plugin;
    
    public ItemBuilderCommand(CLStuff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player))
            return false;
        
        if (!commandSender.hasPermission("clstuff.itembuilder")) {
            commandSender.sendMessage(PREFIX + "§cYou do not have permission to use this command.");
            return false;
        }
        
        if (args.length == 0) {
            commandSender.sendMessage(PREFIX + "You must specify a value!");
            return false;
        }
        
        if (args.length < 2 && !args[0].equalsIgnoreCase("removeLastLoreLine")) {
            commandSender.sendMessage(PREFIX + "You must specify a value!");
            return false;
        }
        
        Player player = (Player) commandSender;
        
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            commandSender.sendMessage(PREFIX + "You must hold an item!");
            return false;
        }
        
        ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
        String input = (String.join(" ", Arrays.copyOfRange(args, 1, args.length))).replace("\"", "");
        String function = args[0];
        
        switch (function.toLowerCase()) {
            case "setdisplayname":
                builder.setDisplayName(input);
                break;
            case "addloreline":
                builder.addLore(input);
                break;
            case "settype":
                try {
                    builder.setType(Material.valueOf(input));
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(PREFIX + "Application unsuccessful. The material you entered cannot be found.");
                    return false;
                }
                break;
            case "removelastloreline":
                builder.removeLastLoreLine();
                break;
            case "setenchantmentglow":
                builder.setEnchantmentGlow(Boolean.parseBoolean(input));
                break;
            case "setamount":
                try {
                    builder.setAmount(Integer.parseInt(input));
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(PREFIX + "Application unsuccessful. For unsafe amounts use " + ChatColor.GOLD + "/itembuilder setAmountUnsafe");
                    return false;
                }
                break;
            case "setcustommodeldata":
                builder.setCustomModelData(Integer.parseInt(input));
                break;
            case "setamountunsafe":
                builder.setAmountUnsafe(Integer.parseInt(input));
                break;
            case "addpersistentdata":
                builder.addPersistentData(plugin, args[1], args[2]);
                break;
            case "removepersistentdata":
                builder.removePersistentData(plugin, args[1]);
                break;
            default:
                commandSender.sendMessage(PREFIX + "The function you specified is not valid.");
                return false;
        }
        
        player.sendMessage(PREFIX + ChatColor.GREEN + "Applied function " + ChatColor.DARK_GREEN + function + ChatColor.GREEN + " to item.");
        player.getInventory().setItemInMainHand(builder.build());
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.2F, 2F);
        
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player))
            return Collections.emptyList();
        
        if (!commandSender.hasPermission("clstuff.itembuilder"))
            return Collections.emptyList();
        
        Player player = (Player) commandSender;
        
        if (args.length == 1)
            return Utils.getMatches(args[0], Arrays.asList("setType", "setDisplayName", "addLoreLine", "removeLastLoreLine",
                    "setEnchantmentGlow", "setAmount", "setAmountUnsafe", "setCustomModelData",
                    "addPersistentData", "removePersistentData"));
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("settype"))
                return Utils.getMatches(args[1], Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()));
            else if (args[0].equalsIgnoreCase("addpersistentdata"))
                return Collections.singletonList("key");
            else if (args[0].equalsIgnoreCase("removepersistentdata")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.getType() != Material.AIR)
                    return item.getItemMeta().getPersistentDataContainer().getKeys().stream().map(NamespacedKey::getKey).collect(Collectors.toList());
            }
        }
        
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addpersistentdata"))
                return Collections.singletonList("value");
        }
        return Collections.emptyList();
    }
}
