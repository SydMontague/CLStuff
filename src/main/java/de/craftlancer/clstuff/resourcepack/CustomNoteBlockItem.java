package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomNoteBlockItem extends CustomBlockItem {
    
    private Note note;
    private Instrument instrument;
    private boolean powered;
    
    public CustomNoteBlockItem(String id, ItemStack item, Note note, Instrument instrument, boolean powered) {
        super(id, item);
        
        this.note = note;
        this.instrument = instrument;
        this.powered = powered;
    }
    
    public CustomNoteBlockItem(Map<String, Object> map) {
        super(map);
        
        this.instrument = Instrument.valueOf((String) map.get("instrument"));
        this.note = new Note((Integer) map.get("octave"), Note.Tone.valueOf((String) map.get("tone")), (boolean) map.get("isSharped"));
        this.powered = (boolean) map.get("powered");
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        map.put("instrument", instrument.name());
        map.put("octave", note.getOctave());
        map.put("tone", note.getTone().name());
        map.put("isSharped", note.isSharped());
        map.put("powered", powered);
        
        return map;
    }
    
    public boolean isPowered() {
        return powered;
    }
    
    public Note getNote() {
        return note;
    }
    
    public Instrument getInstrument() {
        return instrument;
    }
    
    @Override
    public Material getBlockMaterial() {
        return Material.NOTE_BLOCK;
    }
    
    @Override
    public Material getItemMaterial() {
        return Material.NOTE_BLOCK;
    }
    
    @Override
    public BlockData getBlockData(BlockData data) {
        if (!(data instanceof NoteBlock))
            return data;
        
        NoteBlock noteBlock = (NoteBlock) data;
        
        noteBlock.setNote(getNote());
        noteBlock.setPowered(isPowered());
        noteBlock.setInstrument(instrument);
        
        return data;
    }
    
    @Override
    public boolean equals(BlockData block) {
        if (block.getMaterial() != getBlockMaterial())
            return false;
        
        NoteBlock noteBlock = (NoteBlock) block;
        
        if (!noteBlock.getInstrument().equals(getInstrument()))
            return false;
        
        if (!noteBlock.getNote().equals(getNote()))
            return false;
        
        return noteBlock.isPowered() == isPowered();
    }
}
