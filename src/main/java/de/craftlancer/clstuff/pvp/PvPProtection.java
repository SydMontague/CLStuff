package de.craftlancer.clstuff.pvp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.conversation.ClickableBooleanPrompt;
import de.craftlancer.core.conversation.FormattedConversable;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageRegisterable;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.raidstone.wgevents.WorldGuardEvents;

// TODO just use a map, can't check PvP Status for offline players otherwise
public class PvPProtection implements Listener, MessageRegisterable {
    
    private static final String DISABLE_REGION = "Valgard";
    private static final long DISABLE_TIMER = 60 * 60 * 1000L;
    
    private final ConversationFactory enableConvo;
    
    private Map<UUID, Long> map = new HashMap<>();
    
    public PvPProtection(CLStuff plugin) {
        MessageUtil.register(this,
                             new TextComponent(""),
                             ChatColor.WHITE,
                             ChatColor.YELLOW,
                             ChatColor.RED,
                             ChatColor.DARK_RED,
                             ChatColor.DARK_AQUA,
                             ChatColor.GREEN);
        
        this.enableConvo = new ConversationFactory(plugin).withFirstPrompt(new EnablePvPPrompt()).withTimeout(30).withLocalEcho(false).withModality(false);
        plugin.getCommand("togglepvp").setExecutor(this::togglePvPCommand);
    }
    
    public Long getPvPEnabledValue(OfflinePlayer player) {
        return map.getOrDefault(player.getUniqueId(), 0L);
    }
    
    public boolean hasPvPEnabled(OfflinePlayer player) {
        long val = map.getOrDefault(player.getUniqueId(), 0L);
        
        return val == Long.MAX_VALUE || System.currentTimeMillis() < val;
    }
    
    public void setPvPEnabled(OfflinePlayer player) {
        map.put(player.getUniqueId(), Long.MAX_VALUE);
    }
    
    public void setPvPDisabled(OfflinePlayer player, long timer) {
        map.merge(player.getUniqueId(), System.currentTimeMillis() + timer, Math::min);
    }
    
    private void handlePvPDisable(Player player) {
        if (!WorldGuardEvents.isPlayerInAnyRegion(player.getUniqueId(), DISABLE_REGION)) {
            MessageUtil.sendMessage(this, player, MessageLevel.INFO, "You can only disable PvP mode in Valgard!");
            return;
        }
        
        setPvPDisabled(player, DISABLE_TIMER);
        MessageUtil.sendMessage(this, player, MessageLevel.INFO, "The timer has been started, PvP will be toggled off in 1 hour.");
    }
    
    private void handlePvPEnable(Player player) {
        MessageUtil.sendMessage(this, player, MessageLevel.WARNING, "====================");
        MessageUtil.sendMessage(this, player, MessageLevel.WARNING, "Warning, enabling PvP mode will have severe consequences.");
        MessageUtil.sendMessage(this, player, MessageLevel.WARNING, "It can only be disabled while being in Valgard and with a delay.");
        MessageUtil.sendMessage(this,
                                player,
                                MessageLevel.WARNING,
                                "While it is enabled you can be attacked anywhere in the world without explicit PvP protection.");
        MessageUtil.sendMessage(this,
                                player,
                                MessageLevel.WARNING,
                                "All claims you're trusted to lose automatic chest protection against other PvP enabled players and can be sieged if you're in them.");
        MessageUtil.sendMessage(this, player, MessageLevel.WARNING, "====================");
        
        Conversation convo = enableConvo.buildConversation(new FormattedConversable(player));
        convo.getContext().setSessionData("player", player);
        player.beginConversation(convo);
    }
    
    private boolean togglePvPCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(this, sender, MessageLevel.ERROR, "This command does nothing to non-players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!hasPvPEnabled(player) || getPvPEnabledValue(player) != Long.MAX_VALUE)
            handlePvPEnable(player);
        else
            handlePvPDisable(player);
        
        return true;
    }
    
    private class EnablePvPPrompt extends ClickableBooleanPrompt {
        public EnablePvPPrompt() {
            super("Do you really want to enable PvP mode?");
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            Player player = (Player) context.getSessionData("player");
            
            if (input) {
                setPvPEnabled(player);
                MessageUtil.sendMessage(PvPProtection.this, player, MessageLevel.WARNING, "PvP Mode enabled!");
            }
            else
                MessageUtil.sendMessage(PvPProtection.this, player, MessageLevel.WARNING, "PvP Mode not enabled!");
            
            return Prompt.END_OF_CONVERSATION;
        }
    }
    
    @Override
    public String getMessageID() {
        return "TogglePVP";
    }
}
