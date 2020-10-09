package de.craftlancer.clstuff.emotes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmoteAliasCommand extends Command {
    
    private Emote emote;
    
    protected EmoteAliasCommand(@NotNull String name, Emote emote) {
        super(name);
        
        this.emote = emote;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        
        if (!(commandSender instanceof Player))
            return false;
        
        Player player = (Player) commandSender;
        
        if (!player.hasPermission(emote.getPermission())) {
            commandSender.sendMessage(EmoteManager.PREFIX + "§You do not have permission to use this command!");
            return false;
        }
        
        emote.run(player);
        
        return true;
    }
}
