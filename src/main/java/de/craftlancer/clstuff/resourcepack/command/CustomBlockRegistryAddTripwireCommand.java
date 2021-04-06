package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.clstuff.resourcepack.CustomTripwireItem;
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

public class CustomBlockRegistryAddTripwireCommand extends SubCommand {
    
    private CustomBlockRegistry registry;
    
    public CustomBlockRegistryAddTripwireCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 4:
                return Collections.singletonList("id");
            case 5:
                return Collections.singletonList("armed");
            case 6:
                return Collections.singletonList("attached");
            default:
                return Utils.getMatches(args[args.length - 1], Arrays.asList("NORTH", "SOUTH", "EAST", "WEST"));
        }
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        if (args.length < 6)
            return "§eYou must enter an id and true/false for powered, armed, and attached.";
        
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        
        if (item.getType() == Material.AIR || !item.getItemMeta().hasCustomModelData())
            return "§eYou must hold an item with custom model data.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getId().equals(args[3])))
            return "§eThis id is already taken.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()))
            return "§eThis custom model data is already taken.";
        
        String id = args[3];
        boolean isArmed = Boolean.parseBoolean(args[4]);
        boolean isAttached = Boolean.parseBoolean(args[5]);
        Set<BlockFace> set = new HashSet<>();
        if (args.length > 6)
            for (String string : Arrays.copyOfRange(args, 6, args.length))
                set.add(BlockFace.valueOf(string.toUpperCase()));
        else
            return "§eYou must enter at least one direction for this tripwire to face.";
        
        registry.addCustomBlockItem(new CustomTripwireItem(id, item.clone(), isArmed, isAttached, set));
        return "§aSuccessfully added tripwire item.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
