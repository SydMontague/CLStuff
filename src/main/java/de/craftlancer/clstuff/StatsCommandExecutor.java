package de.craftlancer.clstuff;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.core.Utils;

public class StatsCommandExecutor implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            int playtime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
            
            p.sendMessage("Playtime: " + Utils.ticksToTimeString(playtime));
        }
        
        return true;
    }
    
}
