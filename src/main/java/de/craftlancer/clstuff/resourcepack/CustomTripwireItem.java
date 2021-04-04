package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomTripwireItem extends CustomBlockItem {
    
    private boolean armed;
    private boolean attached;
    private Set<BlockFace> faces;
    
    public CustomTripwireItem(String id, ItemStack item, boolean armed, boolean attached, Set<BlockFace> faces) {
        super(id, item);
        
        this.armed = armed;
        this.attached = attached;
        this.faces = faces;
    }
    
    public CustomTripwireItem(Map<String, Object> map) {
        super(map);
        
        this.armed = (boolean) map.get("armed");
        this.attached = (boolean) map.get("attached");
        this.faces = ((List<String>) map.get("faces")).stream().map(BlockFace::valueOf).collect(Collectors.toSet());
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        map.put("armed", armed);
        map.put("attached", attached);
        map.put("faces", faces.stream().map(BlockFace::name).collect(Collectors.toList()));
        
        return map;
    }
    
    public boolean isArmed() {
        return armed;
    }
    
    public boolean isAttached() {
        return attached;
    }
    
    public Set<BlockFace> getFaces() {
        return faces;
    }
    
    @Override
    public Material getBlockMaterial() {
        return Material.TRIPWIRE;
    }
    
    @Override
    public Material getItemMaterial() {
        return Material.STRING;
    }
    
    @Override
    public void setBlockData(Block block) {
        if (!(block.getBlockData() instanceof Tripwire))
            return;
        
        Tripwire tripwire = (Tripwire) block.getBlockData();
        
        tripwire.setPowered(false);
        tripwire.setDisarmed(!isArmed());
        tripwire.setAttached(isAttached());
        for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST))
            tripwire.setFace(face, faces.contains(face));
        
        block.setBlockData(tripwire);
    }
    
    @Override
    public boolean equals(Block block) {
        if (block.getType() != getBlockMaterial())
            return false;
        
        Tripwire tripwire = (Tripwire) block.getBlockData();
        
        if (tripwire.isPowered())
            return false;
        
        if (tripwire.isDisarmed() == isArmed())
            return false;
        
        if (tripwire.isAttached() != isAttached())
            return false;
        
        for (BlockFace allowedFace : tripwire.getAllowedFaces())
            if (faces.contains(allowedFace) && !tripwire.getFaces().contains(allowedFace))
                return false;
        
        return true;
    }
}
