package de.craftlancer.clstuff.economy;

import de.craftlancer.clapi.LazyService;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.CLCore;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankManager implements Listener {
    
    private static final LazyService<Economy> economy = new LazyService<>(Economy.class);
    
    private CLStuff plugin;
    private List<String> mobNames;
    
    public BankManager(CLStuff plugin) {
        this.plugin = plugin;
        
        plugin.getCommand("bankmanager").setExecutor(new BankManagerCommandHandler(plugin, this));
        Bukkit.getPluginManager().registerEvents(this, plugin);
        
        load();
    }
    
    private void load() {
        File file = new File(plugin.getDataFolder(), "bankManager.yml");
        
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        mobNames = (List<String>) config.getList("mobNames", new ArrayList<>());
    }
    
    public void save() {
        File file = new File(plugin.getDataFolder(), "bankManager.yml");
        
        try {
            if (!file.exists())
                file.createNewFile();
            
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            config.set("mobNames", mobNames);
            
            config.save(file);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getMobNames() {
        return mobNames;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        ActiveMob active = MythicMobs.inst().getAPIHelper().getMythicMobInstance(event.getRightClicked());
        
        if (active == null)
            return;
        
        if (mobNames.contains(active.getType().getInternalName())) {
            player.openInventory(new BankMenu(plugin, this, player).getInventory());
        }
    }
    
    
    protected int getValue(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasCustomModelData())
            return -1;
        
        if (isSimilar(item, 1))
            return item.getAmount();
        
        if (isSimilar(item, 2))
            return item.getAmount() * 100;
        
        if (isSimilar(item, 3))
            return item.getAmount() * 1000;
        
        return -1;
    }
    
    private boolean isSimilar(ItemStack item, int c) {
        Optional<ItemStack> currency = CLCore.getInstance().getItemRegistry().getItem("aethercurrency" + c);
        
        if (!currency.isPresent())
            return false;
        
        return sameRegisterItem(item, currency.get());
    }
    
    private boolean sameRegisterItem(ItemStack item, ItemStack compare) {
        if (compare.getType() != item.getType())
            return false;
        
        if (!compare.hasItemMeta() || !compare.getItemMeta().hasCustomModelData())
            return false;
        
        return item.getItemMeta().getCustomModelData() == compare.getItemMeta().getCustomModelData();
    }
    
}
