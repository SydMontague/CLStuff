package de.craftlancer.clstuff.adminshop;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.menu.ConditionalMenu;
import de.craftlancer.core.menu.Menu;
import de.craftlancer.core.menu.MenuItem;
import de.craftlancer.core.resourcepack.ResourcePackManager;
import de.craftlancer.core.resourcepack.TranslateSpaceFont;
import de.craftlancer.core.util.InventoryUtils;
import de.craftlancer.core.util.ItemBuilder;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import de.craftlancer.core.util.Tuple;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class AdminShop {
    private static final ItemStack BORDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack ADMIN_QUESTION_MARK = new ItemBuilder(Material.STONE).setCustomModelData(5).setDisplayName("§6To default page...").build();
    private static final ItemStack QUESTION_MARK = new ItemBuilder(Material.STONE).setCustomModelData(5).setDisplayName("§6What is this?")
            .setLore("", "§7Click on an arrow to make", "§7a trade. You must have all", "§7Items listed on the left side of", "§7the arrow to make a trade.").build();
    private static final ItemStack ARROW_GREEN_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(2).setDisplayName("Trade for").build();
    
    private static final ItemStack BROADCAST_OFF_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(1).setDisplayName("Broadcast Off").build();
    private static final ItemStack BROADCAST_ON_ITEM = new ItemBuilder(Material.ARROW).setCustomModelData(2).setDisplayName("Broadcast On").build();
    
    private static final ItemStack CONFIRM_CHANGE_ITEM = new ItemBuilder(Material.LIME_CONCRETE).setCustomModelData(1).setDisplayName("Confirm changes")
            .build();
    private static final ItemStack REVERT_CHANGE_ITEM = new ItemBuilder(Material.RED_CONCRETE).setCustomModelData(1).setDisplayName("Revert changes").build();
    
    private CLStuff plugin;
    private AdminShopManager manager;
    private AdminShopTrade[] trades = new AdminShopTrade[4];
    
    private ConditionalMenu menu;
    
    public AdminShop(CLStuff plugin, AdminShopManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        
        trades[0] = new AdminShopTrade();
        trades[1] = new AdminShopTrade();
        trades[2] = new AdminShopTrade();
        trades[3] = new AdminShopTrade();
        
    }
    
    public AdminShop(CLStuff plugin, AdminShopManager manager, AdminShopTrade[] trades) {
        this.plugin = plugin;
        this.manager = manager;
        this.trades = trades;
        
    }
    
    private void updateTrades() {
        for (int i = 0; i < 4; i++) {
            AdminShopTrade trade = trades[i];
            if (trade == null)
                trade = new AdminShopTrade();
            
            Menu m = menu.getMenu("defaultAdmin");
            
            for (int j = 0; j < 7; j++)
                trade.setInput(j, m.getMenuItem(9 + 9 * i + j).getItem().getType() == Material.AIR ? null : m.getMenuItem(9 + 9 * i + j).getItem());
            
            trade.setOutput(m.getMenuItem(9 + 9 * i + 8).getItem().getType() == Material.AIR ? null : m.getMenuItem(9 + 9 * i + 8).getItem());
        }
        
        
        createMenu();
    }
    
    public void createMenu() {
        this.menu = new ConditionalMenu(plugin, 6,
                Arrays.asList(new Tuple<>("defaultUser", "Admin Shop"), new Tuple<>("defaultAdmin", "Admin Shop Editor"),
                        new Tuple<>("resourceUser", ChatColor.WHITE + "" + TranslateSpaceFont.TRANSLATE_NEGATIVE_8 + "\uE304" + TranslateSpaceFont.getSpecificAmount(-169) + "§8Admin Shop"),
                        new Tuple<>("resourceAdmin", ChatColor.WHITE + "" + TranslateSpaceFont.TRANSLATE_NEGATIVE_8 + "\uE304" + TranslateSpaceFont.getSpecificAmount(-169) + "§8Admin Shop Editor")));
        
        for (int i = 0; i < 9; i++)
            menu.set(i, new MenuItem(BORDER_ITEM), "defaultUser", "defaultAdmin");
        
        for (int i = 45; i < 54; i++)
            menu.set(i, new MenuItem(BORDER_ITEM), "defaultUser", "defaultAdmin");
        
        for (int i = 0; i < 4; i++) {
            AdminShopTrade trade = trades[i];
            
            MenuItem input0 = new MenuItem(trade.getInput()[0]);
            MenuItem input1 = new MenuItem(trade.getInput()[1]);
            MenuItem input2 = new MenuItem(trade.getInput()[2]);
            MenuItem input3 = new MenuItem(trade.getInput()[3]);
            MenuItem input4 = new MenuItem(trade.getInput()[4]);
            MenuItem input5 = new MenuItem(trade.getInput()[5]);
            MenuItem input6 = new MenuItem(trade.getInput()[6]);
            MenuItem arrow = new MenuItem(ARROW_GREEN_ITEM);
            MenuItem isBroadcast = new MenuItem(trade.isBroadcast() ? BROADCAST_ON_ITEM : BROADCAST_OFF_ITEM);
            MenuItem output = new MenuItem(trade.getOutput());
            
            final int localI = i;
            menu.set(9 + i * 9, input0.clone().addClickAction(p -> menu.replace(9 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(10 + i * 9, input1.clone().addClickAction(p -> menu.replace(10 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(11 + i * 9, input2.clone().addClickAction(p -> menu.replace(11 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(12 + i * 9, input3.clone().addClickAction(p -> menu.replace(12 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(13 + i * 9, input4.clone().addClickAction(p -> menu.replace(13 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(14 + i * 9, input5.clone().addClickAction(p -> menu.replace(14 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(15 + i * 9, input6.clone().addClickAction(p -> menu.replace(15 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            menu.set(16 + i * 9, isBroadcast.clone().addClickAction(c -> {
                trade.setBroadcast(!trade.isBroadcast());
                menu.replace(16 + localI * 9, trade.isBroadcast() ? BROADCAST_ON_ITEM : BROADCAST_OFF_ITEM, "defaultAdmin", "resourceAdmin");
                c.getPlayer().playSound(c.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }), "defaultAdmin", "resourceAdmin");
            menu.set(17 + i * 9, output.clone().addClickAction(p -> menu.replace(17 + localI * 9, p.getCursor(), "defaultAdmin", "resourceAdmin")), "defaultAdmin", "resourceAdmin");
            
            Consumer<Player> action = new TradeAction(trade);
            
            menu.set(9 + i * 9, input0.clone(), "defaultUser", "resourceUser");
            menu.set(10 + i * 9, input1.clone(), "defaultUser", "resourceUser");
            menu.set(11 + i * 9, input2.clone(), "defaultUser", "resourceUser");
            menu.set(12 + i * 9, input3.clone(), "defaultUser", "resourceUser");
            menu.set(13 + i * 9, input4.clone(), "defaultUser", "resourceUser");
            menu.set(14 + i * 9, input5.clone(), "defaultUser", "resourceUser");
            menu.set(15 + i * 9, input6.clone(), "defaultUser", "resourceUser");
            menu.set(16 + i * 9, arrow.clone().addClickAction(c -> action.accept(c.getPlayer())), "defaultUser", "resourceUser");
            menu.set(17 + i * 9, output.clone().addClickAction(c -> action.accept(c.getPlayer())), "defaultUser", "resourceUser");
        }
        
        menu.set(48, new MenuItem(REVERT_CHANGE_ITEM).addClickAction(click -> {
            createMenu();
            click.getPlayer().playSound(click.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            display(click.getPlayer());
        }), "defaultAdmin", "resourceAdmin");
        
        menu.set(49, new MenuItem(ADMIN_QUESTION_MARK).addClickAction(click -> {
            Player p = click.getPlayer();
            
            display(p, ResourcePackManager.getInstance().isFullyAccepted(p) ? "resourceUser" : "defaultUser");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }), "defaultAdmin", "resourceAdmin");
        
        menu.set(50, new MenuItem(CONFIRM_CHANGE_ITEM).addClickAction(click -> {
            Player p = click.getPlayer();
            
            updateTrades();
            display(p, ResourcePackManager.getInstance().isFullyAccepted(p) ? "resourceUser" : "defaultUser");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }), "defaultAdmin", "resourceAdmin");
        
        menu.set(49, new MenuItem(QUESTION_MARK), "defaultUser", "resourceUser");
    }
    
    public void display(Player player) {
        boolean usingResourcePack = ResourcePackManager.getInstance().isFullyAccepted(player);
        String key;
        if (player.hasPermission("clstuff.adminshop")) {
            if (usingResourcePack)
                key = "resourceAdmin";
            else
                key = "defaultAdmin";
        } else {
            if (usingResourcePack)
                key = "resourceUser";
            else
                key = "defaultUser";
        }
        display(player, key);
    }
    
    public void display(Player player, String key) {
        if (menu == null)
            createMenu();
        
        player.openInventory(menu.getMenu(key).getInventory());
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
            
            Bukkit.getPluginManager().callEvent(new AdminShopTransactionEvent(p, trade));
        }
        
    }
}
