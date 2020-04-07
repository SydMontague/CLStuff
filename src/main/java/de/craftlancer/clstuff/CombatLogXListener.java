package de.craftlancer.clstuff;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import de.craftlancer.clfeatures.portal.event.PortalTeleportEvent;

public class CombatLogXListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCombatTag(PlayerPreTagEvent event) {
        if (!(event.getEnemy() instanceof OfflinePlayer))
            return;
        
        Clan self = CLClans.getInstance().getClan(event.getPlayer());
        Clan other = CLClans.getInstance().getClan((OfflinePlayer) event.getEnemy());
        
        if (self != null && self.equals(other))
            event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPortal(PortalTeleportEvent event) {
        if(CombatUtil.isInCombat(event.getPlayer()))
            event.setCancelled(true);
    }
}
