package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomBlock implements ConfigurationSerializable {
    
    private Location location;
    private String itemID;
    
    //Not initialized off of start to save performance
    private NoteBlockItem item;
    
    public CustomBlock(Location location, NoteBlockItem item) {
        this.location = location;
        this.item = item;
    }
    
    public CustomBlock(Map<String, Object> map) {
        this.location = (Location) map.get("location");
        this.itemID = (String) map.get("item");
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("location", location);
        map.put("item", item.getId());
        
        return map;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public NoteBlockItem getItem() {
        if (item == null)
            for (NoteBlockItem i : NoteBlockRegistry.getInstance().getNoteBlockItems())
                if (i.getId().equals(itemID)) {
                    item = i;
                    break;
                }
        
        return item;
    }
}
