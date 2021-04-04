package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.clstuff.resourcepack.CustomNoteBlockItem;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomBlockRegistryAddNoteBlockCommand extends SubCommand {
    
    private CustomBlockRegistry registry;
    
    public CustomBlockRegistryAddNoteBlockCommand(CLStuff plugin, CustomBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 4)
            return Collections.singletonList("id");
        if (args.length == 5)
            return Utils.getMatches(args[4], Arrays.stream(Instrument.values()).map(Instrument::name).collect(Collectors.toList()));
        if (args.length == 6)
            return Collections.singletonList("0-24");
        if (args.length == 7)
            return Collections.singletonList("powered");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        if (args.length < 7)
            return "§eYou must enter an id, instrument, note, and powered/unpowered.";
        
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        
        if (item.getType() == Material.AIR || !item.getItemMeta().hasCustomModelData())
            return "§eYou must hold an item with custom model data.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getId().equals(args[3])))
            return "§eThis id is already taken.";
        
        if (registry.getCustomBlockItems().stream().anyMatch(b -> b.getItemMaterial() == item.getType() && b.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()))
            return "§eThis custom model data is already taken.";
        
        if (Arrays.stream(Instrument.values()).noneMatch(i -> i.name().equals(args[4])))
            return "§eYou must enter a valid instrument.";
        
        int i;
        try {
            i = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        }
        
        if (i < 0 || i > 24)
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        
        Instrument instrument = Instrument.valueOf(args[4]);
        
        registry.addCustomBlockItem(new CustomNoteBlockItem(args[3], item.clone(), new Note(i), instrument, Boolean.parseBoolean(args[6])));
        return "§aSuccessfully added noteblock item.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
