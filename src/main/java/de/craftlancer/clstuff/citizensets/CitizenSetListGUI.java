package de.craftlancer.clstuff.citizensets;

import de.craftlancer.core.gui.PageItem;
import de.craftlancer.core.gui.PagedListGUIInventory;
import de.craftlancer.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.List;

public class CitizenSetListGUI extends PagedListGUIInventory {
    public CitizenSetListGUI(Plugin plugin, List<PageItem> pageItems) {
        super(plugin, false, 2, pageItems, true);
    }
    
    @Nonnull
    @Override
    public ItemStack getInfoItem() {
        return new ItemBuilder(Material.STONE).setDisplayName("&3&lWhat is this?")
                .setLore("",
                        "&7Click on an icon to",
                        "&7view all items in the set.")
                .setCustomModelData(5).build();
    }
}
