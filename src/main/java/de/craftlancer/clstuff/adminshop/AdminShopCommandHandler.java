package de.craftlancer.clstuff.adminshop;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class AdminShopCommandHandler extends CommandHandler {
    
    public AdminShopCommandHandler(Plugin plugin, AdminShopManager manager) {
        super(plugin);
        
        registerSubCommand("create", new AdminShopCreateCommand(plugin));
        registerSubCommand("remove", new AdminShopRemoveCommand(plugin));
        registerSubCommand("setbroadcast", new AdminShopSetBroadcastCommand(plugin));
        registerSubCommand("defaultbroadcast", new AdminShopDefaultBroadcastCommand(plugin, manager));
        registerSubCommand("setDisplayItem", new AdminShopSetDisplayItemCommand(plugin, manager));
    }
}
