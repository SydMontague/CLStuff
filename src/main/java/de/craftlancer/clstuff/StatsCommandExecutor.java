package de.craftlancer.clstuff;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import de.craftlancer.clclans.ClanUtils;
import de.craftlancer.clstuff.rankings.Rankings.RankingsEntry;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class StatsCommandExecutor implements CommandExecutor {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("0.00");
    
    private CLStuff plugin;
    
    public StatsCommandExecutor(CLStuff plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player = null;
        
        if (args.length == 1)
            player = Bukkit.getOfflinePlayer(args[0]);
        else if (sender instanceof Player)
            player = (Player) sender;
        
        if (player == null || !player.hasPlayedBefore())
            return false;

        // TODO player profile
        // description
        // Wiki Link
        
        RankingsEntry entry = plugin.getRankings().getRankingsEntry(player);
        Clan clan = CLClans.getInstance().getClan(player);
        
        sender.sendMessage("§f[§4Craft§fCitizen]" + ChatColor.DARK_RED + CLCore.getInstance().getPermissions().getPrimaryGroup(null, player) + " " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s Stats:");
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + "Playtime: " + Utils.TEXT_COLOR_IMPORTANT + Utils.ticksToTimeString(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + "Money: " + Utils.TEXT_COLOR_IMPORTANT + MONEY_FORMAT.format(entry.getBalance()));
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + String.format("Claimblocks:%s %d/%d", Utils.TEXT_COLOR_IMPORTANT, entry.getSpent(), entry.getSpent() + entry.getUnspent()));
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + "Score: " + Utils.TEXT_COLOR_IMPORTANT + plugin.getRankings().getScore(player));
        if(clan != null) {
            BaseComponent comp = new TextComponent(Utils.INDENTATION + "Clan: ");
            comp.setColor(Utils.TEXT_COLOR_UNIMPORTANT.asBungee());
            comp.addExtra(ClanUtils.getClanTagAndNameComponent(clan));
            comp.addExtra(" | ");
            comp.addExtra(ClanUtils.getRankComponent(clan, clan.getMember(player).getRank()));
            sender.spigot().sendMessage(comp);
        } 
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + "Join Date: " + Utils.TEXT_COLOR_IMPORTANT + ClanUtils.toDate(player.getFirstPlayed()));
        sender.sendMessage(Utils.INDENTATION + Utils.TEXT_COLOR_UNIMPORTANT + "Last Seen: " + Utils.TEXT_COLOR_IMPORTANT + ClanUtils.toDate(player.getLastPlayed()));
        
        return true;
    }
}
