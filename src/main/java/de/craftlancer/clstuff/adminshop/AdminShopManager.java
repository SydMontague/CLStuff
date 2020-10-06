package de.craftlancer.clstuff.adminshop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

public class AdminShopManager implements Listener {
    private static final String PERMISSION = "clstuff.adminshop";
    static final String METADATA = "clstuff.adminshop";
    static final String METADATA_CREATE = "create";
    static final String METADATA_REMOVE = "remove";
    
    static final String METADATA_BROADCAST_ID = "clstuff.adminshop.broadcastId";
    static final String METADATA_BROADCAST_MSG = "clstuff.adminshop.broadcastMsg";
    
    static {
        ConfigurationSerialization.registerClass(AdminShopTrade.class);
    }
    
    private final CLStuff plugin;
    private Map<Location, AdminShop> shops = new HashMap<>();
    private String defaultBroadcast = "%player% bought %item%.";
    
    public AdminShopManager(CLStuff plugin) {
        this.plugin = plugin;
        
        File configFile = new File(plugin.getDataFolder(), "adminShops.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        defaultBroadcast = config.getString("defaultBroadcast");
        
        config.getKeys(false).forEach(a -> {
            if(a.equalsIgnoreCase("defaultBroadcast"))
                return;
            
            Location loc = fromConfigKey(a);
            AdminShopTrade[] trades = new AdminShopTrade[4];
            ConfigurationSection section = config.getConfigurationSection(a);
            trades[0] = (AdminShopTrade) section.get("trade1");
            trades[1] = (AdminShopTrade) section.get("trade2");
            trades[2] = (AdminShopTrade) section.get("trade3");
            trades[3] = (AdminShopTrade) section.get("trade4");
            
            shops.put(loc, new AdminShop(plugin, this, trades));
        });
        
        plugin.getCommand("adminshop").setExecutor(new AdminShopCommandHandler(plugin, this));
        
        new LambdaRunnable(this::displayParticles).runTaskTimer(plugin, 0, 10);
    }
    
    private static final String toConfigKey(Location loc) {
        return String.format("%s,%d,%d,%d", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    private static final Location fromConfigKey(String string) {
        String[] split = string.split(",");
        World world = Bukkit.getWorld(split[0]);
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);
        
        return new Location(world, x, y, z);
    }
    
    public void save() {
        File configFile = new File(plugin.getDataFolder(), "adminShops.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("defaultBroadcast", defaultBroadcast);
        shops.forEach((a, b) -> {
            ConfigurationSection section = config.createSection(toConfigKey(a));
            section.set("trade1", b.getTrade(0));
            section.set("trade2", b.getTrade(1));
            section.set("trade3", b.getTrade(2));
            section.set("trade4", b.getTrade(3));
        });
        
        BukkitRunnable saveTask = new LambdaRunnable(() -> {
            try {
                config.save(configFile);
            }
            catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while saving Admin Shops: ", e);
            }
        });
        
        if (plugin.isEnabled())
            saveTask.runTaskAsynchronously(plugin);
        else
            saveTask.run();
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAdminInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!player.hasPermission(PERMISSION))
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!event.hasBlock())
            return;
        
        Location clicked = event.getClickedBlock().getLocation();
        
        if (player.hasMetadata(METADATA)) {
            String mode = player.getMetadata(METADATA).get(0).asString();
            player.removeMetadata(METADATA, plugin);
            
            if (!shops.containsKey(clicked) && METADATA_CREATE.equals(mode)) {
                shops.put(clicked, new AdminShop(plugin, this));
                MessageUtil.sendMessage(plugin, player, MessageLevel.NORMAL, "AdminShop created");
            }
            else if (shops.containsKey(clicked) && METADATA_REMOVE.equals(mode)) {
                shops.remove(clicked);
                MessageUtil.sendMessage(plugin, player, MessageLevel.NORMAL, "AdminShop removed");
            }
        }
        if (player.hasMetadata(METADATA_BROADCAST_ID)) {
            String msg = player.getMetadata(METADATA_BROADCAST_MSG).get(0).asString();
            int id = player.getMetadata(METADATA_BROADCAST_ID).get(0).asInt();
            AdminShop shop = shops.get(clicked);
            
            player.removeMetadata(METADATA_BROADCAST_ID, plugin);
            player.removeMetadata(METADATA_BROADCAST_MSG, plugin);
            
            if (shop == null)
                return;
            
            shop.getTrade(id).setBroadcastString(msg);
            MessageUtil.sendMessage(plugin, player, MessageLevel.NORMAL, "Broadcast message set.");
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!event.hasBlock())
            return;
        
        Location clicked = event.getClickedBlock().getLocation();
        
        if (!shops.containsKey(clicked))
            return;
        
        AdminShop shop = shops.get(clicked);
        
        event.getPlayer().openInventory(shop.getInventory());
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (shops.containsKey(event.getBlock().getLocation()))
            event.setCancelled(true);
    }
    
    private void displayParticles() {
        shops.keySet().stream().filter(a -> a.getWorld().isChunkLoaded(a.getBlockX() >> 4, a.getBlockZ() >> 4))
             .forEach(a -> a.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, a.clone().add(0.5, 1.2, 0.5), 5, 0.2D, 0.2D, 0.2D));
    }
    
    public void setDefaultBroadcast(String message) {
        this.defaultBroadcast = message;
    }
    
    public String getDefaultBroadcast() {
        return defaultBroadcast;
    }
}
