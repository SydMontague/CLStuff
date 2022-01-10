package de.craftlancer.clstuff.economy;

import de.craftlancer.clapi.LazyService;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.resourcepack.EconomyFont;
import de.craftlancer.core.resourcepack.TranslateSpaceFont;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ToggleBalanceDisplay implements CommandExecutor {
    
    private static final LazyService<Economy> eco = new LazyService<>(Economy.class);
    
    private CLStuff plugin;
    private NamespacedKey displayDisabled;
    
    public ToggleBalanceDisplay(CLStuff plugin) {
        this.plugin = plugin;
        this.displayDisabled = new NamespacedKey(plugin, "toggleBalanceDisplay");
        
        plugin.getCommand("togglebalancedisplay").setExecutor(this);
        
        new LambdaRunnable(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isDisplayEnabled(player))
                    return;
                send(player);
            }
        }).runTaskTimer(plugin, 0, 10);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            MessageUtil.sendMessage(plugin, commandSender, MessageLevel.INFO, "You do not have access to this command.");
            return false;
        }
        
        Player player = (Player) commandSender;
        
        if (!isDisplayEnabled(player)) {
            player.getPersistentDataContainer().remove(displayDisabled);
            MessageUtil.sendMessage(plugin, commandSender, MessageLevel.SUCCESS, "Your balance is now being displayed.");
        } else {
            player.getPersistentDataContainer().set(displayDisabled, PersistentDataType.INTEGER, 0);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
            MessageUtil.sendMessage(plugin, commandSender, MessageLevel.SUCCESS, "Your balance is no longer being displayed.");
        }
        
        return true;
    }
    
    public void send(Player player) {
        String bal = EconomyFont.getBalance((int) eco.get().getBalance(player));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                TranslateSpaceFont.getSpecificAmount(166 - bal.length() * 6) + bal + EconomyFont.AETHER));
    }
    
    private boolean isDisplayEnabled(Player player) {
        return !player.getPersistentDataContainer().has(displayDisabled, PersistentDataType.INTEGER);
    }
}
