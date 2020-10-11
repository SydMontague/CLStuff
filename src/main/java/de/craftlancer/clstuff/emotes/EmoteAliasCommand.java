package de.craftlancer.clstuff.emotes;

import de.craftlancer.core.LambdaRunnable;

import javax.annotation.Nonnull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmoteAliasCommand extends Command {
    
    private Emote emote;
    private EmoteManager manager;
    
    protected EmoteAliasCommand(@Nonnull Emote emote, @Nonnull EmoteManager manager) {
        super(emote.getName());
        
        this.emote = emote;
        this.manager = manager;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
        
        if (!(commandSender instanceof Player))
            return false;
        
        Player player = (Player) commandSender;
        
        if (!player.hasPermission(emote.getPermission())) {
            commandSender.sendMessage(manager.getPrefix() + "§cYou do not have permission to use this command!");
            return false;
        }
        
        if (manager.hasCooldown(player.getUniqueId())) {
            commandSender.sendMessage(manager.getPrefix() + "§cYou must wait to use an emote again!");
            return false;
        }
        
        emote.run(player);
        
        manager.addCooldown(player.getUniqueId());
        new LambdaRunnable(() -> manager.removeCooldown(player.getUniqueId())).runTaskLater(manager.getPlugin(), manager.getCooldown());
        
        return true;
    }
}
