package de.craftlancer.clstuff.resourcepack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMushroomItem extends CustomBlockItem {
    
    private Set<BlockFace> faces;
    
    public CustomMushroomItem(String id, ItemStack item, Set<BlockFace> faces) {
        super(id, item);
        
        this.faces = faces;
    }
    
    public CustomMushroomItem(Map<String, Object> map) {
        super(map);
        
        this.faces = ((List<String>) map.get("faces")).stream().map(BlockFace::valueOf).collect(Collectors.toSet());
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        map.put("faces", faces.stream().map(BlockFace::name).collect(Collectors.toList()));
        
        return map;
    }
    
    @Override
    public Material getItemMaterial() {
        return getItem().getType();
    }
    
    @Override
    public Material getBlockMaterial() {
        return getItem().getType();
    }
    
    @Override
    public void setBlockData(Block block) {
        if (!(block.getBlockData() instanceof MultipleFacing))
            return;
        
        MultipleFacing facing = (MultipleFacing) block.getBlockData();
        
        for (BlockFace face : facing.getAllowedFaces())
            facing.setFace(face, faces.contains(face));
        
        block.setBlockData(facing);
    }
    
    @Override
    public boolean equals(Block block) {
        if (!(block.getBlockData() instanceof MultipleFacing))
            return false;
        
        MultipleFacing facing = (MultipleFacing) block.getBlockData();
        
        for (BlockFace allowedFace : facing.getAllowedFaces()) {
            if (faces.contains(allowedFace) && !facing.getFaces().contains(allowedFace))
                return false;
            if (facing.getFaces().contains(allowedFace) && !faces.contains(allowedFace))
                return false;
        }
        
        return true;
    }
}
