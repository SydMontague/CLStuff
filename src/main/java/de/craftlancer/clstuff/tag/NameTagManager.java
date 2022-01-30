package de.craftlancer.clstuff.tag;

import com.lkeehl.tagapi.TagBuilder;
import com.lkeehl.tagapi.api.Tag;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NameTagManager implements Listener {
    
    private CLStuff plugin;
    
    public NameTagManager(CLStuff plugin) {
        
        this.plugin = plugin;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        Player player = event.getPlayer();
    
        Tag tag = TagBuilder.create(player)
                .withLine(pl -> player.getDisplayName())
                .build();
    
        tag.giveTag();
    }
    
}
