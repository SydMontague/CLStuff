package de.craftlancer.clstuff.adminshop;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class AdminShopDisplayItem {
    public static final String DISPLAY_ITEM_METADATA = "adminshopDisplayItem";
    
    private Location spawnLocation;
    private World world;
    
    private ItemStack displayItem;
    private Item item;
    
    public AdminShopDisplayItem(AdminShop shop) {
        this.world = shop.getLocation().getWorld();
        this.spawnLocation = shop.getLocation().clone().add(0.5, 1, 0.5);
    }
    
    public boolean tick() {
        if (displayItem == null || !Utils.isChunkLoaded(spawnLocation))
            return false;
        
        if (item == null || !item.isValid()) {
            item = world.dropItem(spawnLocation, displayItem);
            item.setInvulnerable(true);
            item.setMetadata(DISPLAY_ITEM_METADATA, new FixedMetadataValue(CLStuff.getInstance(), 0));
        }
        
        item.setVelocity(new Vector().zero());
        
        return true;
    }
    
    public void remove() {
        if (item == null)
            return;
        
        item.remove();
    }
    
    public void setItemStack(ItemStack item) {
        remove();
        
        if (item == null)
            this.displayItem = null;
        else {
            ItemStack tmp = item.clone();
            ItemMeta meta = tmp.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_GRAY + "AdminShopDisplayItem");
            
            tmp.setItemMeta(meta);
            tmp.setAmount(1);
            
            this.displayItem = tmp;
            
            tick();
        }
    }
    
    public Item getItem() {
        return item;
    }
}