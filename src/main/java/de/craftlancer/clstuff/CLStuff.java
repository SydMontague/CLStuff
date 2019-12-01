package de.craftlancer.clstuff;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.clstuff.help.CCHelpCommandHandler;
import de.craftlancer.clstuff.squest.ServerQuests;

public class CLStuff extends JavaPlugin implements Listener {
    
    private WGNoDropFlag flag;
    private ServerQuests serverQuests;
    
    @Override
    public void onLoad() {
        WGNoDropFlag.registerFlag();
    }
    
    @Override
    public void onEnable() {
        getCommand("stats").setExecutor(new StatsCommandExecutor());
        getCommand("map").setExecutor((a, b, c, d) -> {
            a.sendMessage("https://craftlancer.de/map.html");
            return true;
        });
        getCommand("discord").setExecutor((a, b, c, d) -> {
            a.sendMessage("https://discord.gg/GgPEaP8");
            return true;
        });
        getCommand("ping").setExecutor((a, b, c, d) -> {
            Player target = null;
            
            if(d.length == 1) 
                target = Bukkit.getPlayer(d[0]);
            else if(a instanceof Player)
                target = (Player) a;
               
            if(target == null)
                a.sendMessage("No player found to check ping for.");
            else
                a.sendMessage(target.getName() + "'s Ping: " + ((CraftPlayer) target).getHandle().ping + " ms");
                
            return true;
        });
        getCommand("cchelp").setExecutor(new CCHelpCommandHandler(this));
        
        flag = new WGNoDropFlag(this);
        serverQuests = new ServerQuests(this);
        
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void lecternFix(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.LECTERN && event.getItemInHand().getType() == Material.WRITTEN_BOOK)
            event.setCancelled(false);
    }
}
