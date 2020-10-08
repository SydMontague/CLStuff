package de.craftlancer.clstuff.premium;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ModelTokenListCommand extends ModelTokenSubCommand {
    
    public ModelTokenListCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.list", plugin, false, token);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You're not allowed to use this command.";
        
        sender.sendMessage("ID - Token - Item");
        
        int id = 0;
        for(Entry<ItemStack, TokenData> a : getToken().getTokens().entrySet()) {
            BaseComponent delAction = new TextComponent("[Delete]");
            delAction.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/modeltoken remove " + a.getKey().hashCode()));
            delAction.setColor(ChatColor.RED);
            
            BaseComponent token = Utils.getItemComponent(a.getKey());
            token.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/modeltoken gettoken " + a.getKey().hashCode()));
            BaseComponent item = Utils.getItemComponent(a.getValue().toItemStack());
            item.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/modeltoken getitem " + a.getKey().hashCode()));
            
            BaseComponent base = new TextComponent(Integer.toString(id));
            base.addExtra(" - ");
            base.addExtra(token);
            base.addExtra(" - ");
            base.addExtra(item);
            base.addExtra(" - ");
            base.addExtra(delAction);
            
            sender.spigot().sendMessage(base);
            id++;
        }
        
        return null;
        
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
