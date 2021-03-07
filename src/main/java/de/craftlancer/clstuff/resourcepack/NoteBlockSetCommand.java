package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NoteBlockSetCommand extends SubCommand {
    public NoteBlockSetCommand(Plugin plugin) {
        super("clstuff.noteblock.set", plugin, false);
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Arrays.stream(Instrument.values()).map(Instrument::name).collect(Collectors.toList()));
        if (args.length == 3)
            return Collections.singletonList("0-24");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender))
            return "§eYou do not have access to this command.";
        
        Player player = (Player) sender;
        
        if (args.length < 3)
            return "§eYou must enter an instrument and a note.";
        
        if (Arrays.stream(Instrument.values()).noneMatch(v -> v.name().equalsIgnoreCase(args[1])))
            return "§eYou must enter a valid instrument. Use tab complete to assist you.";
        
        Instrument instrument = Instrument.valueOf(args[1].toUpperCase());
        
        int i;
        try {
            i = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        }
        
        if (i < 0 || i > 24)
            return "§eYou must enter a valid number between 0 and 24 inclusive.";
        
        Block block = player.getTargetBlock(null, 6);
        
        if (block.getType() != Material.NOTE_BLOCK)
            return "§eYou must look at a noteblock.";
        
        NoteBlock noteBlock = (NoteBlock) block.getBlockData();
        
        noteBlock.setInstrument(instrument);
        noteBlock.setNote(new Note(i));
        
        block.setBlockData(noteBlock);
        
        return "§aNoteblock updated.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
