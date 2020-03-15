package de.craftlancer.clstuff;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class AntiMuleDupe implements Listener {
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Entity vehicle = p.getVehicle();

        if(vehicle instanceof InventoryHolder)
            ((InventoryHolder) vehicle).getInventory().getViewers().forEach(HumanEntity::closeInventory);
    }
}
