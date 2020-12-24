package de.craftlancer.clstuff.inventorymanagement;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class InventoryManagementCommandHandler extends CommandHandler {
    public InventoryManagementCommandHandler(Plugin plugin, InventoryManagement management) {
        super(plugin);
        
        registerSubCommand("giveLastInventory", new InventoryManagementGiveLastInventoryCommand(plugin, management));
    }
}
