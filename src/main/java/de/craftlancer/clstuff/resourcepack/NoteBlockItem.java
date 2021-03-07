package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NoteBlockItem implements ConfigurationSerializable {
    
    private String id;
    private Note note;
    private Instrument instrument;
    private ItemStack item;
    
    public NoteBlockItem(String id, Note note, Instrument instrument, ItemStack item) {
        this.id = id;
        this.note = note;
        this.instrument = instrument;
        this.item = item;
    }
    
    public NoteBlockItem(Map<String, Object> map) {
        this.item = (ItemStack) map.get("item");
        this.instrument = Instrument.valueOf((String) map.get("instrument"));
        this.note = new Note((Integer) map.get("octave"), Note.Tone.valueOf((String) map.get("tone")), (boolean) map.get("isSharped"));
        this.id = (String) map.get("id");
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", id);
        map.put("instrument", instrument.name());
        map.put("item", item);
        map.put("octave", note.getOctave());
        map.put("tone", note.getTone().name());
        map.put("isSharped", note.isSharped());
        
        return map;
    }
    
    public String getId() {
        return id;
    }
    
    public Note getNote() {
        return note;
    }
    
    public Instrument getInstrument() {
        return instrument;
    }
    
    public ItemStack getItem() {
        return item;
    }
}
