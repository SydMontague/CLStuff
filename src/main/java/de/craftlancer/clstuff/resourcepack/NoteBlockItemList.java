package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.core.gui.PageItem;
import de.craftlancer.core.gui.PagedListGUIInventory;
import de.craftlancer.core.util.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class NoteBlockItemList extends PagedListGUIInventory {
    public NoteBlockItemList(Plugin plugin, List<PageItem> pageItems) {
        super(plugin, true, 6, pageItems, true);
    }
    
    protected static List<PageItem> getPageItems(NoteBlockRegistry registry, List<NoteBlockItem> items) {
        return items.stream().map(item -> {
            PageItem pageItem = new PageItem(new ItemBuilder(item.getItem()).addLore("", "§6§lLEFT CLICK §eto recieve item.", "§6§lSHIFT RIGHT CLICK §eto remove item.").build());
            pageItem.setClickAction(player -> player.getInventory().addItem(item.getItem().clone()), ClickType.LEFT);
            pageItem.setClickAction(() -> registry.removeNoteBlockItem(item), ClickType.SHIFT_RIGHT);
            return pageItem;
        }).collect(Collectors.toList());
    }
}
