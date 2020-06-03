package de.craftlancer.clstuff.afk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;

public class AFKListener implements Listener {
    private static final long AFK_TIME = 50 * 60 * 1000L; // 60 minutes in ms
    private static final long CHECK_INTERVAL = 60 * 20L; // 1 minute in ticks
    private final CLStuff plugin;
    
    Map<UUID, Long> players = new HashMap<>();
    
    private ConversationFactory convo;
    
    public AFKListener(CLStuff plugin) {
        this.plugin = plugin;
        
        convo = new ConversationFactory(plugin).withTimeout(600).withLocalEcho(false).withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return "§f[§4Craft§fCitizen]§e You seem afk. If you're not, please type something in chat.";
            }
            
            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                context.getForWhom().sendRawMessage("§f[§4Craft§fCitizen]§e Thank you. :)");
                players.put(((Player) context.getForWhom()).getUniqueId(), System.currentTimeMillis());
                return null;
            }
        }).addConversationAbandonedListener(event -> {
            Player p = (Player) event.getContext().getForWhom();
            long timeDiff = System.currentTimeMillis() - players.get(p.getUniqueId());
            
            if (!event.gracefulExit() && timeDiff > AFK_TIME)
                p.kickPlayer("You've been kicked for inactivity.");
            
        });
        
        
        new LambdaRunnable(() -> {
            long time = System.currentTimeMillis();
            Bukkit.getOnlinePlayers().forEach(a -> {
                if(a.isOp())
                    return;
                
                long timeDiff = time - players.get(a.getUniqueId());
                
                if (timeDiff > AFK_TIME && !a.isConversing()) {
                    a.playSound(a.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1f, 1f);
                    convo.buildConversation(a).begin();
                }
            });
        }).runTaskTimer(plugin, CHECK_INTERVAL, CHECK_INTERVAL);
    }
    
    class AFKPlayer {
        long lastActivity = System.currentTimeMillis();
        
        public void resetActivity() {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public long getLastActivity() {
            return lastActivity;
        }
    }
    
    /* Activity Listener */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        new LambdaRunnable(() -> players.put(event.getPlayer().getUniqueId(), System.currentTimeMillis())).runTask(plugin);
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        players.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        players.put(event.getEnchanter().getUniqueId(), System.currentTimeMillis());
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        players.put(event.getWhoClicked().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onTakeBook(PlayerTakeLecternBookEvent event) {
        players.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onTakeBook(PlayerRespawnEvent event) {
        players.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
    
    /* Maintenance Listener */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        players.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer().getUniqueId());
    }
}
