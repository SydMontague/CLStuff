package de.craftlancer.clstuff.customfont;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Arrays;
import java.util.List;

public class PersistentActionBar {
    
    
    public static class ActionBar {
        
        private List<CloseAction> closeActions;
        private BaseComponent[] components;
        
        public ActionBar(BaseComponent[] components, CloseAction... actions) {
            this.components = components;
            this.closeActions = Arrays.asList(actions);
        }
        
        public BaseComponent[] getComponents() {
            return components;
        }
        
        public List<CloseAction> getCloseActions() {
            return closeActions;
        }
        
        public boolean canSend(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            Action a = event.getAction();
            
            for (CloseAction action : closeActions) {
                switch (action) {
                    case SHIFT:
                        if (!player.isSneaking())
                            return false;
                        break;
                    case RIGHT_CLICK:
                        if (a != Action.RIGHT_CLICK_BLOCK && a != Action.LEFT_CLICK_BLOCK)
                            return false;
                        break;
                    case LEFT_CLICK:
                        if (a != Action.LEFT_CLICK_BLOCK && a != Action.LEFT_CLICK_AIR)
                            return false;
                        break;
                    case BLOCK_CLICK:
                        if (a != Action.LEFT_CLICK_BLOCK && a != Action.RIGHT_CLICK_BLOCK)
                            return false;
                        break;
                }
            }
            
            return true;
        }
        
        public boolean canSend(PlayerToggleSneakEvent event) {
            return event.isSneaking() && closeActions.stream().allMatch(a -> a == CloseAction.SHIFT);
        }
        
        public void send(Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        }
    }
    
    public enum CloseAction {
        SHIFT,
        RIGHT_CLICK,
        LEFT_CLICK,
        BLOCK_CLICK
    }
}
