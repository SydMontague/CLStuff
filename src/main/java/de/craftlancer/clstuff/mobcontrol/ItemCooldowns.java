package de.craftlancer.clstuff.mobcontrol;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

/*
 * Consume Cooldowns
 * 
 */
public class ItemCooldowns implements Listener, CommandExecutor {
    private static final StateFlag COOLDOWN_FLAG = new StateFlag("custom-cooldowns", false);
    
    private final CLStuff plugin;
    private ICombatLogX combatLogPlugin = null;
    
    private Map<Material, Integer> consumeCooldowns = new EnumMap<>(Material.class);
    private int riptideCooldown;
    private int totemCooldown;
    private int pearlCooldown;

    public static void registerFlag() {
        WorldGuard.getInstance().getFlagRegistry().register(COOLDOWN_FLAG);
    }
    
    public ItemCooldowns(CLStuff plugin) {
        this.plugin = plugin;
        loadConfiguration();
        
        plugin.getCommand("itemCooldowns").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        if(Bukkit.getPluginManager().isPluginEnabled("CombatLogX"))
            combatLogPlugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
    }
    
    private void loadConfiguration() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "itemCooldowns.yml"));
        
        this.riptideCooldown = config.getInt("riptideCooldown", -1);
        this.totemCooldown = config.getInt("totemCooldown", -1);
        this.pearlCooldown = config.getInt("pearlCooldown", -1);
        
        ConfigurationSection items = config.getConfigurationSection("consumeCooldowns");
        
        if(items != null) {
            for(String s : items.getKeys(false)) {
                Material mat = Material.matchMaterial(s);
                if(mat != null)
                    consumeCooldowns.put(mat, items.getInt(s, -1));
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("clstuff.itemcooldown"))
            return false;
        
        loadConfiguration();
        
        MessageUtil.sendMessage(plugin, sender, MessageLevel.NORMAL, "ItemCooldowns reloaded.");
        return true;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        int cooldown = consumeCooldowns.getOrDefault(item.getType(), -1);
        
        if(cooldown < 0)
            return;
        
        event.getPlayer().setCooldown(item.getType(), cooldown);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        if(this.riptideCooldown < 0)
            return;

        if(!isFlagSet(event.getPlayer()) && !combatLogPlugin.getCombatManager().isInCombat(event.getPlayer()))
            return;
        
        event.getPlayer().setCooldown(Material.TRIDENT, riptideCooldown);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTotem(EntityResurrectEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        if(this.totemCooldown < 0)
            return;
        
        Player player = (Player) event.getEntity();
        
        if(player.getCooldown(Material.TOTEM_OF_UNDYING) > 0)
            event.setCancelled(true);
        else
            player.setCooldown(Material.TOTEM_OF_UNDYING, totemCooldown);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if(event.getEntityType() != EntityType.ENDER_PEARL)
            return;
        if(this.pearlCooldown < 0)
            return;
        
        EnderPearl pearl = (EnderPearl) event.getEntity();

        if(!(pearl.getShooter() instanceof Player))
            return;
        
        Player shooter = (Player) pearl.getShooter();

        if(!isFlagSet(shooter) && !combatLogPlugin.getCombatManager().isInCombat(shooter))
            return;
        
        new LambdaRunnable(() -> shooter.setCooldown(Material.ENDER_PEARL, pearlCooldown)).runTask(plugin);
    }
    
    private boolean isFlagSet(Player player) {
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        
        com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        StateFlag.State state = query.queryState(wgLoc, wgPlayer, COOLDOWN_FLAG);
        
        return state == State.ALLOW;
    }
    
    
}
