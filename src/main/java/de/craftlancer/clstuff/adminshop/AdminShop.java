package de.craftlancer.clstuff.adminshop;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.gui.GUIInventory;
import de.craftlancer.core.util.InventoryUtils;
import de.craftlancer.core.util.ItemBuilder;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;

public class AdminShop {
    private static final ItemStack BORDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack QUESTION_MARK = new ItemBuilder(Material.STONE).setCustomModelData(5).setDisplayName("Edit").build();
    private static final ItemStack ARROW_GREEN_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(2).setDisplayName("Trade for").build();
    
    private static final ItemStack BROADCAST_OFF_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(1).setDisplayName("Broadcast Off").build();
    private static final ItemStack BROADCAST_ON_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(2).setDisplayName("Broadcast On").build();
    
    private static final ItemStack CONFIRM_CHANGE_ITEM = new ItemBuilder(Material.LIME_CONCRETE).setCustomModelData(1).setDisplayName("Confirm changes")
                                                                                                .build();
    private static final ItemStack REVERT_CHANGE_ITEM = new ItemBuilder(Material.RED_CONCRETE).setCustomModelData(1).setDisplayName("Revert changes").build();
    
    private CLStuff plugin;
    private AdminShopManager manager;
    private AdminShopTrade[] trades = new AdminShopTrade[4];
    
    private GUIInventory gui;
    private GUIInventory adminGUI;
    
    public AdminShop(CLStuff plugin, AdminShopManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        
        trades[0] = new AdminShopTrade();
        trades[1] = new AdminShopTrade();
        trades[2] = new AdminShopTrade();
        trades[3] = new AdminShopTrade();
        
        gui = new GUIInventory(plugin, 6);
        adminGUI = new GUIInventory(plugin, 6);
        updateGUI();
        updateAdminGUI();
    }
    
    public AdminShop(CLStuff plugin, AdminShopManager manager, AdminShopTrade[] trades) {
        this.plugin = plugin;
        this.manager = manager;
        this.trades = trades;
        
        gui = new GUIInventory(plugin, 6);
        adminGUI = new GUIInventory(plugin, 6);
        updateGUI();
        updateAdminGUI();
    }
    
    private void updateAdminGUI() {
        adminGUI.setItem(0, BORDER_ITEM);
        adminGUI.setItem(1, BORDER_ITEM);
        adminGUI.setItem(2, BORDER_ITEM);
        adminGUI.setItem(3, BORDER_ITEM);
        adminGUI.setItem(4, BORDER_ITEM);
        adminGUI.setItem(5, BORDER_ITEM);
        adminGUI.setItem(6, BORDER_ITEM);
        adminGUI.setItem(7, BORDER_ITEM);
        adminGUI.setItem(8, BORDER_ITEM);
        
        for (int i = 0; i < 4; i++) {
            AdminShopTrade trade = trades[i];
            
            adminGUI.setItem(9 + i * 9, trade.getInput()[0]);
            adminGUI.setItem(10 + i * 9, trade.getInput()[1]);
            adminGUI.setItem(11 + i * 9, trade.getInput()[2]);
            adminGUI.setItem(12 + i * 9, trade.getInput()[3]);
            adminGUI.setItem(13 + i * 9, trade.getInput()[4]);
            adminGUI.setItem(14 + i * 9, trade.getInput()[5]);
            adminGUI.setItem(15 + i * 9, trade.getInput()[6]);
            adminGUI.setItem(16 + i * 9, trade.isBroadcast() ? BROADCAST_ON_ITEM : BROADCAST_OFF_ITEM);
            adminGUI.setItem(17 + i * 9, trade.getOutput());
            
            final int localI = i;
            adminGUI.setClickAction(9 + i * 9, p -> adminGUI.setItem(9 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(10 + i * 9, p -> adminGUI.setItem(10 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(11 + i * 9, p -> adminGUI.setItem(11 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(12 + i * 9, p -> adminGUI.setItem(12 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(13 + i * 9, p -> adminGUI.setItem(13 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(14 + i * 9, p -> adminGUI.setItem(14 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(15 + i * 9, p -> adminGUI.setItem(15 + localI * 9, p.getItemOnCursor()));
            adminGUI.setClickAction(16 + i * 9, p -> {
                trade.setBroadcast(!trade.isBroadcast());
                adminGUI.setItem(16 + localI * 9, trade.isBroadcast() ? BROADCAST_ON_ITEM : BROADCAST_OFF_ITEM);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            });
            adminGUI.setClickAction(17 + i * 9, p -> adminGUI.setItem(17 + localI * 9, p.getItemOnCursor()));
        }
        
        adminGUI.setItem(45, BORDER_ITEM);
        adminGUI.setItem(46, BORDER_ITEM);
        adminGUI.setItem(47, BORDER_ITEM);
        adminGUI.setItem(48, REVERT_CHANGE_ITEM);
        adminGUI.setItem(49, QUESTION_MARK);
        adminGUI.setItem(50, CONFIRM_CHANGE_ITEM);
        adminGUI.setItem(51, BORDER_ITEM);
        adminGUI.setItem(52, BORDER_ITEM);
        adminGUI.setItem(53, BORDER_ITEM);
        
        adminGUI.setClickAction(48, p -> {
            updateAdminGUI();
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        });
        adminGUI.setClickAction(49, p -> {
            if (p.hasPermission("clstuff.adminshop")) {
                p.openInventory(getInventory());
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        });
        adminGUI.setClickAction(50, p -> {
            p.openInventory(getInventory());
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            updateTrades();
        });
    }
    
    private void updateTrades() {
        for (int i = 0; i < 4; i++) {
            AdminShopTrade trade = trades[i];
            if (trade == null)
                trade = new AdminShopTrade();
            
            for (int j = 0; j < 7; j++)
                trade.setInput(j, getAdminInventory().getItem(9 + 9 * i + j));
            
            trade.setOutput(getAdminInventory().getItem(9 + 9 * i + 8));
        }
        
        updateGUI();
        updateAdminGUI();
    }
    
    private void updateGUI() {
        gui.setItem(0, BORDER_ITEM);
        gui.setItem(1, BORDER_ITEM);
        gui.setItem(2, BORDER_ITEM);
        gui.setItem(3, BORDER_ITEM);
        gui.setItem(4, BORDER_ITEM);
        gui.setItem(5, BORDER_ITEM);
        gui.setItem(6, BORDER_ITEM);
        gui.setItem(7, BORDER_ITEM);
        gui.setItem(8, BORDER_ITEM);
        
        for (int i = 0; i < 4; i++) {
            AdminShopTrade trade = trades[i];
            
            gui.setItem(9 + i * 9, trade.getInput()[0]);
            gui.setItem(10 + i * 9, trade.getInput()[1]);
            gui.setItem(11 + i * 9, trade.getInput()[2]);
            gui.setItem(12 + i * 9, trade.getInput()[3]);
            gui.setItem(13 + i * 9, trade.getInput()[4]);
            gui.setItem(14 + i * 9, trade.getInput()[5]);
            gui.setItem(15 + i * 9, trade.getInput()[6]);
            gui.setItem(16 + i * 9, ARROW_GREEN_ITEM);
            gui.setItem(17 + i * 9, trade.getOutput());
            
            Consumer<Player> action = new TradeAction(trade);
            gui.setClickAction(16 + i * 9, action);
            gui.setClickAction(17 + i * 9, action);
        }
        
        gui.setItem(45, BORDER_ITEM);
        gui.setItem(46, BORDER_ITEM);
        gui.setItem(47, BORDER_ITEM);
        gui.setItem(48, BORDER_ITEM);
        gui.setItem(49, QUESTION_MARK);
        gui.setItem(50, BORDER_ITEM);
        gui.setItem(51, BORDER_ITEM);
        gui.setItem(52, BORDER_ITEM);
        gui.setItem(53, BORDER_ITEM);
        
        gui.setClickAction(49, p -> {
            if (p.hasPermission("clstuff.adminshop")) {
                p.openInventory(getAdminInventory());
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);   
            }
        });
    }
    
    public Inventory getInventory() {
        return gui.getInventory();
    }
    
    public Inventory getAdminInventory() {
        return adminGUI.getInventory();
    }
    
    public AdminShopTrade getTrade(int id) {
        return trades[id];
    }
    
    CLStuff getPlugin() {
        return plugin;
    }
    
    AdminShopManager getManager() {
        return manager;
    }
    
    private class TradeAction implements Consumer<Player> {
        private final AdminShopTrade trade;
        
        public TradeAction(AdminShopTrade trade) {
            this.trade = trade;
        }
        
        @Override
        public void accept(Player p) {
            Inventory pInv = p.getInventory();
            
            if (!trade.isValid())
                return;
            
            if (!InventoryUtils.containsAtLeast(pInv, trade.getInput())) {
                MessageUtil.sendMessage(getPlugin(), p, MessageLevel.INFO, "You can't afford this item.");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }
            
            pInv.removeItem(Arrays.stream(trade.getInput()).filter(Objects::nonNull).map(ItemStack::clone).toArray(ItemStack[]::new));
            pInv.addItem(trade.getOutput().clone()).forEach((a, b) -> p.getWorld().dropItem(p.getLocation(), b));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            
            if (trade.isBroadcast()) {
                ItemStack item = trade.getOutput();
                String m = trade.getBroadcastString() == null ? getManager().getDefaultBroadcast() : trade.getBroadcastString();
                String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
                String message = ChatColor.translateAlternateColorCodes('&', m).replace("%player%", p.getName()).replace("%item%", displayName);
                Bukkit.broadcastMessage(message);
            }
        }
        
    }
}
