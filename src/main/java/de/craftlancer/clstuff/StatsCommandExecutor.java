package de.craftlancer.clstuff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.core.Utils;

public class StatsCommandExecutor implements CommandExecutor {
    
    private CLStuff plugin;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player = null;
        
        if (args.length == 1)
            player = Bukkit.getOfflinePlayer(args[0]);
        else if (sender instanceof Player)
            player = (Player) sender;
        
        if (player == null)
            return false;
        
        sender.sendMessage("Playtime: " + Utils.ticksToTimeString(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        sender.sendMessage("Score: " + plugin.getRankings().getScore(player));
        return true;
    }
}
