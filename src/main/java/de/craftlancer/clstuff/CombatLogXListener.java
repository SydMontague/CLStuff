package de.craftlancer.clstuff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import de.craftlancer.clfeatures.portal.event.PortalTeleportEvent;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class CombatLogXListener implements Listener {
    private ICombatLogX combatLogPlugin = null;
    
    public CombatLogXListener() {
        if(Bukkit.getPluginManager().isPluginEnabled("CombatLogX"))
            combatLogPlugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
    }
    
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
        if(combatLogPlugin == null)
            return;
        
        if(combatLogPlugin.getCombatManager().isInCombat(event.getPlayer()))
            event.setCancelled(true);
        
        if(GriefPrevention.instance.dataStore.getPlayerData(event.getPlayer().getUniqueId()).siegeData != null)
            event.setCancelled(true);
    }
}
