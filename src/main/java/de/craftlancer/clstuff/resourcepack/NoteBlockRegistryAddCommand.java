package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
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

public class NoteBlockRegistryAddCommand extends SubCommand {
    
    private NoteBlockRegistry registry;
    
    public NoteBlockRegistryAddCommand(CLStuff plugin, NoteBlockRegistry registry) {
        super("clstuff.admin", plugin, false);
        
        this.registry = registry;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3)
            return Collections.singletonList("id");
        if (args.length == 4)
            return Utils.getMatches(args[3], Arrays.stream(Instrument.values()).map(Instrument::name).collect(Collectors.toList()));
        if (args.length == 5)
            return Collections.singletonList("0-24");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        if (args.length < 5)
            return "§eYou must enter an id, instrument, and a note.";
        
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        
        if (item.getType() == Material.AIR || !item.getItemMeta().hasCustomModelData())
            return "§eYou must hold an item with custom model data.";
        
        if (registry.getNoteBlockItems().stream().anyMatch(b -> b.getId().equals(args[2])))
            return "§eThis id is already taken.";
        
        if (Arrays.stream(Instrument.values()).noneMatch(i -> i.name().equals(args[3])))
            return "§eYou must enter a valid instrument.";
        
        int i;
        try {
            i = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        }
        
        if (i < 0 || i > 24)
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        
        Instrument instrument = Instrument.valueOf(args[3]);
        
        registry.addNoteBlockItem(new NoteBlockItem(args[2], new Note(i), instrument, item.clone()));
        return "§aSuccessfully added noteblock item.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
