package de.craftlancer.clstuff.deathmessages;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.WGNoDropFlag;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.NMSUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class DeathMessageListener implements Listener {
    
    private static final String PUMPKINBANDIT_NAME = "PumpkinBandit";
    private static final int PUMPKINBANDIT_MODEL_DATA = 4;
    
    private HashMap<Player, ItemStack> lastBowShotMap = new HashMap<>();
    private HashMap<Player, ItemStack> lastTridentThrownMap = new HashMap<>();
    private HashMap<UUID, EntityDamageEvent.DamageCause> lastDamageCause = new HashMap<>();
    
    private CLStuff plugin;
    private RegionContainer container;
    
    public DeathMessageListener(CLStuff plugin) {
        this.plugin = plugin;
        
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
            this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        
        DeathMessageSettings.load(plugin);
    }
    
    /*
     *    MAKE MESSAGE FOR NON-LIVING DEATHS
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        
        if (!(event.getEntity() instanceof Player))
            return;
        
        Player player = (Player) event.getEntity();
        
        lastDamageCause.put(player.getUniqueId(), cause);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        
        EntityDamageEvent.DamageCause cause = lastDamageCause.get(event.getEntity().getUniqueId());
        
        event.setDeathMessage(null);
        
        String message;
        
        if (cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                && cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && cause != EntityDamageEvent.DamageCause.PROJECTILE
                && cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            
            message = DeathMessageSettings.PREFIX + DeathMessageSettings.getDeathToNonLivingEntityMessage(cause);
            
            message = message.replaceAll("\"", "'");
            message = message.replaceAll("%player%", victim.getDisplayName());
            
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            Entity d = ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager();
            LivingEntity killer;
            String reason;
            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                if (d instanceof Player) {
                    killer = ((Player) d);
                    reason = "player_attack";
                } else {
                    killer = (LivingEntity) d;
                    reason = "entity_attack";
                }
            else if (cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
                if (d instanceof Player) {
                    killer = ((Player) d);
                    reason = "entity_sweep_attack";
                } else {
                    killer = (LivingEntity) d;
                    reason = "entity_sweep_attack";
                }
            else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                if (d instanceof LivingEntity) {
                    killer = (LivingEntity) d;
                    reason = "entity_explosion";
                } else {
                    killer = null;
                    reason = "entity_explosion";
                }
            else {
                if (((Projectile) d).getShooter() instanceof Player) {
                    killer = (Player) ((Projectile) d).getShooter();
                    reason = "projectile_player";
                } else if (((Projectile) d).getShooter() instanceof LivingEntity) {
                    killer = (LivingEntity) ((Projectile) d).getShooter();
                    reason = "projectile_living";
                } else {
                    killer = null;
                    reason = "projectile_non_living";
                }
            }
            
            message = DeathMessageSettings.PREFIX + DeathMessageSettings.getDeathToLivingEntityMessage(reason, killer);
            
            message = message.replaceAll("\"", "'");
            message = message.replaceAll("%player%", victim.getName());
            
            if (message.contains("%killer%") && killer != null)
                message = message.replaceAll("%killer%",
                        killer instanceof Player && hasMatchingItem((Player) killer) ? "Pumpkin Bandit" : killer.getName());
            
            message = ChatColor.translateAlternateColorCodes('&', message);
            
            ComponentBuilder builder = replaceItemVar(cause, message, killer, d);
            
            event.setDeathMessage(null);
            
            broadcast(builder.create());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onProjectileShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) event.getEntity();
            Player shooter = (Player) arrow.getShooter();
            
            lastBowShotMap.put(shooter, getBow(shooter.getInventory().getItemInMainHand(), shooter.getInventory().getItemInOffHand()));
        }
        if (event.getEntity() instanceof Trident && event.getEntity().getShooter() instanceof Player) {
            Trident trident = (Trident) event.getEntity();
            Player shooter = (Player) trident.getShooter();
            
            lastTridentThrownMap.put(shooter, getTrident(shooter.getInventory().getItemInMainHand(), shooter.getInventory().getItemInOffHand()));
        }
    }
    
    /*
     * LISTENER FOR STAT TRACK WEAPONS
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Trident) && !(event.getDamager() instanceof Arrow))
            return;
        
        if (event.getDamager() instanceof Arrow && !(((Arrow) event.getDamager()).getShooter() instanceof Player))
            return;
        
        if (event.getDamager() instanceof Trident && !(((Trident) event.getDamager()).getShooter() instanceof Player))
            return;
        
        Player victim = (Player) event.getEntity();
        Player killer;
        
        ItemStack item;
        if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            killer = (Player) ((Arrow) event.getDamager()).getShooter();
            item = lastBowShotMap.get(killer);
        } else if (event.getDamager() instanceof Trident && ((Trident) event.getDamager()).getShooter() instanceof Player) {
            killer = (Player) ((Trident) event.getDamager()).getShooter();
            item = lastTridentThrownMap.get(killer);
        } else {
            killer = ((Player) event.getDamager());
            item = killer.getInventory().getItemInMainHand();
        }
        
        if (victim.getHealth() - event.getDamage() > 0)
            return;
        
        if (item.getType() == Material.AIR)
            return;
    }
    
    private void broadcast(BaseComponent[] message) {
        new LambdaRunnable(() -> Bukkit.getServer().spigot().broadcast(message))
                .runTaskLater(plugin, 2);
    }
    
    private ItemStack getBow(ItemStack mainHand, ItemStack offHand) {
        if (mainHand.getType() != Material.BOW && mainHand.getType() != Material.CROSSBOW
                && offHand.getType() != Material.BOW && offHand.getType() != Material.CROSSBOW)
            return new ItemStack(Material.BOW, 1);
        
        return (mainHand.getType() == Material.BOW || mainHand.getType() == Material.CROSSBOW) ? mainHand : offHand;
    }
    
    private ItemStack getTrident(ItemStack mainHand, ItemStack offHand) {
        if (mainHand.getType() != Material.TRIDENT
                && offHand.getType() != Material.TRIDENT)
            return new ItemStack(Material.TRIDENT, 1);
        
        return (mainHand.getType() == Material.TRIDENT) ? mainHand : offHand;
    }
    
    private ComponentBuilder replaceItemVar(EntityDamageEvent.DamageCause cause, String message, LivingEntity
            killer, Entity damager) {
        String[] array = message.split("%item%");
        
        ComponentBuilder builder = new ComponentBuilder("");
        
        if (array.length > 1) {
            for (int i = 0; i < array.length; i++) {
                builder.append(array[i]);
                
                if (i == array.length - 1)
                    break;
                
                if (killer instanceof Player && hasMatchingItem((Player) killer)) {
                    builder.append(ChatColor.MAGIC + PUMPKINBANDIT_NAME);
                    continue;
                }
                
                if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                    if (killer.getEquipment() == null || killer.getEquipment().getItemInMainHand().getType() == Material.AIR)
                        builder.append("fists");
                    else
                        builder.append(getItemComponent(killer.getEquipment().getItemInMainHand()));
                } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                    if (killer instanceof Player) {
                        if (damager instanceof Arrow)
                            builder.append(getItemComponent(lastBowShotMap.get(killer)));
                        if (damager instanceof Trident)
                            builder.append(getItemComponent(lastTridentThrownMap.get(killer)));
                    } else
                        builder.append(getItemComponent(getBow(killer.getEquipment())));
                }
            }
        } else
            builder.append(array[0]);
        
        return builder;
    }
    
    
    private BaseComponent getItemComponent(ItemStack item) {
        String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : beautify(item.getType().name());
        
        BaseComponent component = new TextComponent(displayName);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{NMSUtils.getItemHoverComponent(item)}));
        
        return component;
    }
    
    private ItemStack getBow(EntityEquipment equipment) {
        if (equipment == null)
            return new ItemStack(Material.BOW);
        
        return equipment.getItemInMainHand().getType() == Material.AIR ? equipment.getItemInOffHand() : equipment.getItemInMainHand();
    }
    
    private String beautify(String string) {
        return "a " + string.toLowerCase().replaceAll("_", " ");
    }
    
    private boolean isKeepInventoryRegion(Location location) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null)
            return false;
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
        
        boolean bool = false;
        for (ProtectedRegion region : manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()))) {
            StateFlag.State state = region.getFlag(WGNoDropFlag.getNoDropFlag());
            if (state == StateFlag.State.ALLOW) {
                bool = true;
                break;
            }
        }
        return bool;
    }
    
    private boolean hasMatchingItem(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        return helmet != null && helmet.getType() == Material.CARVED_PUMPKIN && helmet.hasItemMeta() && helmet.getItemMeta().hasCustomModelData()
                && helmet.getItemMeta().getCustomModelData() == PUMPKINBANDIT_MODEL_DATA;
    }
}
