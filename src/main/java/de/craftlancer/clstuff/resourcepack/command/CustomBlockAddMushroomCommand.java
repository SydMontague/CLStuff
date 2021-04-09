package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.clstuff.resourcepack.CustomMushroomItem;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomBlockAddMushroomCommand extends SubCommand {
    
    private CustomBlockRegistry registry;
    
    public CustomBlockAddMushroomCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Collections.singletonList("id");
        if (args.length > 3)
            return Utils.getMatches(args[args.length - 1], Arrays.asList("NORTH", "SOUTH", "EAST", "WEST", "UP", "DOWN"));
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        if (args.length < 4)
            return "§eYou must enter an id and at least one direction!";
        
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        
        if (item.getType() == Material.AIR || !item.getItemMeta().hasCustomModelData())
            return "§eYou must hold an item with custom model data.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getId().equals(args[3])))
            return "§eThis id is already taken.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()))
            return "§eThis custom model data is already taken.";
        
        Set<BlockFace> set = new HashSet<>();
        for (String string : Arrays.copyOfRange(args, 3, args.length))
            set.add(BlockFace.valueOf(string.toUpperCase()));
        
        registry.addCustomBlockItem(new CustomMushroomItem(args[2], item.clone(), set));
        return "§aSuccessfully added noteblock item.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
