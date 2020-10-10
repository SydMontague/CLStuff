package de.craftlancer.clstuff.emotes;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmoteAliasCommand extends Command {
    
    private Emote emote;
    private EmoteManager manager;
    
    protected EmoteAliasCommand(@NotNull String name, Emote emote, EmoteManager manager) {
        super(name);
        
        this.emote = emote;
        this.manager = manager;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
        
        if (!(commandSender instanceof Player))
            return false;
        
        Player player = (Player) commandSender;
        
        if (!player.hasPermission(emote.getPermission())) {
            commandSender.sendMessage(EmoteManager.PREFIX + "§cYou do not have permission to use this command!");
            return false;
        }
        
        if (manager.hasCooldown(player.getUniqueId())) {
            commandSender.sendMessage(EmoteManager.PREFIX + "§cYou must wait to use an emote again!");
            return false;
        }
        
        emote.run(player);
        
        manager.addCooldown(player.getUniqueId());
        new LambdaRunnable(() -> manager.removeCooldown(player.getUniqueId())).runTaskLater(CLStuff.getInstance(), EmoteManager.COOLDOWN);
        
        return true;
    }
}
