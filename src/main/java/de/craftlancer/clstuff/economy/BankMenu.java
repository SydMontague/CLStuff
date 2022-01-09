package de.craftlancer.clstuff.economy;

import de.craftlancer.clapi.LazyService;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.Utils;
import de.craftlancer.core.menu.Menu;
import de.craftlancer.core.menu.MenuClick;
import de.craftlancer.core.menu.MenuItem;
import de.craftlancer.core.resourcepack.TranslateSpaceFont;
import de.craftlancer.core.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class BankMenu extends Menu {
    
    private static final LazyService<Economy> economy = new LazyService<>(Economy.class);
    private static final Function<Player, String> titleSupplier = player -> {
        int bal = (int) economy.get().getBalance(player);
        int balLength = String.valueOf(bal).length() - 1;
        return ChatColor.WHITE + TranslateSpaceFont.getSpecificAmount(-8) + "\uE331"
                + TranslateSpaceFont.getSpecificAmount(-95 - balLength * 3) + Utils.translateColorCodes("#7C3100")
                + ((int) economy.get().getBalance(player)) + "§f\uEE30";
    };
    
    private CLStuff plugin;
    private BankManager manager;
    private Player player;
    
    public BankMenu(CLStuff plugin, BankManager manager, Player player) {
        super(plugin, titleSupplier.apply(player), 6);
        
        this.plugin = plugin;
        this.manager = manager;
        this.player = player;
        
        Consumer<Player> refreshInventory = who -> who.openInventory(new BankMenu(plugin, manager, player).getInventory());
        
        MenuItem emptyDepositItem = new MenuItem(new ItemBuilder(Material.AIR).build())
                .addClickAction(click -> {
                    ItemStack cursor = click.getCursor();
                    Player who = click.getPlayer();
                    
                    int value = manager.getValue(click.getCursor());
                    
                    if (value == -1)
                        return;
                    
                    economy.ifPresent(e -> e.depositPlayer(who, value));
                    who.playSound(who.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1F);
                    plugin.getBalanceDisplay().send(player);
                    cursor.setAmount(0);
                    refreshInventory.accept(who);
                });
        
        set(19, emptyDepositItem);
        set(20, emptyDepositItem);
        set(21, emptyDepositItem);
        set(28, emptyDepositItem);
        set(29, emptyDepositItem);
        set(30, emptyDepositItem);
        set(37, emptyDepositItem);
        set(38, emptyDepositItem);
        set(39, emptyDepositItem);
        
        setWithdrawItem(23, 1, 1, refreshInventory);
        setWithdrawItem(32, 2, 100, refreshInventory);
        setWithdrawItem(41, 3, 1000, refreshInventory);
        
    }
    
    private void setWithdrawItem(int slotStart, int currency, int value, Consumer<Player> refresh) {
        ItemStack item = CLCore.getInstance().getItemRegistry().getItem("aethercurrency" + currency).orElse(new ItemStack(Material.AIR));
        
        ItemStack invisibleItem = new ItemBuilder(item).setType(Material.BOOK).setCustomModelData(200)
                .setLore(1, getAffordMessage(player, value)).build();
        
        Consumer<MenuClick> consumer = click -> {
            Player who = click.getPlayer();
            
            if (!economy.get().has(who, value)) {
                who.playSound(who.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2F, 1F);
                return;
            }
            
            economy.get().withdrawPlayer(who, value);
            who.getInventory().addItem(item)
                    .forEach((amount, i) -> who.getWorld().dropItemNaturally(who.getLocation(), i));
            who.playSound(who.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.5F);
            plugin.getBalanceDisplay().send(player);
            refresh.accept(who);
        };
        
        set(slotStart, new MenuItem(invisibleItem).addClickAction(consumer));
        set(slotStart + 1, new MenuItem(new ItemBuilder(item).setLore(1, getAffordMessage(player, value)).build()).addClickAction(consumer));
        set(slotStart + 2, new MenuItem(invisibleItem).addClickAction(consumer));
    }
    
    private String getAffordMessage(Player player, double amount) {
        return economy.get().has(player, amount) ? "&e→ Click to withdraw" : "&f\uEE31 &cYou cannot afford this";
    }
    
    
}
