package de.craftlancer.clstuff.citizensets;

import de.craftlancer.core.gui.NavigationItem;
import de.craftlancer.core.gui.PageItem;
import de.craftlancer.core.gui.PagedListGUIInventory;
import de.craftlancer.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.List;

public class CitizenSetPageGUI extends PagedListGUIInventory {
    public CitizenSetPageGUI(Plugin plugin, List<PageItem> pageItems) {
        super(plugin, false, 2, pageItems, true);
        
        NavigationItem item = new NavigationItem(new ItemBuilder(Material.ENDER_EYE).setDisplayName("&3Back to main page...").build(), -1);
        item.setClickAction(p -> CitizenSetsListener.getInstance().getGui().display(p));
        addNavigationItem(item);
    }
    
    @Nonnull
    @Override
    public ItemStack getInfoItem() {
        return new ItemBuilder(Material.STONE).setDisplayName("&3&lWhat is this?")
                .setLore("",
                        "&7This page shows all items",
                        "&7in this set.")
                .setCustomModelData(5).build();
    }
}
