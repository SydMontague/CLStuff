package de.craftlancer.clstuff;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.clstuff.help.CCHelpCommandHandler;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.LambdaRunnable;
import net.md_5.bungee.api.ChatColor;

public class CLStuff extends JavaPlugin implements Listener {
    
    private WGNoDropFlag flag;
    private ServerQuests serverQuests;
    private boolean useDiscord = false;
    
    @Override
    public void onLoad() {
        WGNoDropFlag.registerFlag();
    }
    
    @Override
    public void onEnable() {
        useDiscord = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
        
        getCommand("wiki").setExecutor((a,b,c,d) -> {
            a.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/");
            return true;
        });
        getCommand("howtoplay").setExecutor((a, b, c, d) -> {
            String commandLine = "minecraft:give %s written_book{pages:[\"[\\\"\\\",{\\\"text\\\":\\\"\\\\n \\\\u0020 \\\\u0020 \\\\u0020Welcome \\\\u0020to\\\\n\\\"},{\\\"text\\\":\\\" \\\\u0020 \\\\u0020 \\\\u0020Craft\\\",\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"Citizen\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"\\\\n\\\\n > \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Visit our Website\\\",\\\"underlined\\\":true,\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/\\\"}},{\\\"text\\\":\\\" <\\\\n\\\\n > \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Join our Discord\\\",\\\"underlined\\\":true,\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://discordapp.com/invite/GgPEaP8\\\"}},{\\\"text\\\":\\\" <\\\\n\\\\nMake sure to read the \\\\u0020 \\\\u0020 \\\\u0020 \\\\u0020 \\\\u0020\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/rules\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Here are some helpfull commands:\\\\n\\\\n\\\"},{\\\"text\\\":\\\"/map\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" > livemap\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/cchelp\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" > help\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/sethome\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" > respawn\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit boat\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit peasant\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/vote\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\\n\\\\n\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Index\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"1. Ranks\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":4}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"2. Portals\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":5}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"3. Using Portals\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":6}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"4. Claims\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":7}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"5. Clans\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":8}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"6. Aether Shards\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":10}},{\\\"text\\\":\\\"\\\\n\\\"},{\\\"text\\\":\\\"7. Alinor PvP Event\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":11}},{\\\"text\\\":\\\"\\\\n \\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Ranks\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nYou can gain ranks with \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/rankup\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" you can check your time played with \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/stats\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\\nRank up to Citizen to get your first \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Portal\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":4}},{\\\"text\\\":\\\".\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Portals\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nYou get your first Portal for free with the rank Citizen.\\\\nPortals are used instead of tp commands or home teleports.\\\\n\\\\nPlace a portal and use \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/portal name x\\\",\\\"color\\\":\\\"dark_green\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Using Portals\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nTo use a portal place a book with the name of your destination in the lectern.\\\\n\\\\nThe portal will remain active for a moment after removing the book.\\\\n\\\\nYou may use \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/portal list\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to see all portals you own.\\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Claims\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nUse a golden shovel to claim your builds. Rightclick two opposing corners of your build to claim it.\\\\nUse \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit peasant\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to get a claim tool.\\\\n\\\\nUse \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/expandclaim x\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to expand in the direction you are looking at.\\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Clans\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nUse \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/clan\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to list all clan commands.\\\\n\\\\nUse \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/clan create name tag color\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to create a clan. Creating a clan is free, and thats a great price!\\\\n\\\\nYou can use clan chat with \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/c text\\\",\\\"color\\\":\\\"dark_green\\\"}]\",\"{\\\"text\\\":\\\"With clans you can add rivals, create alliances and recruit npcs to fight for you!\\\\n\\\\nCheck out the Clanhall in Valgard (Spawn) for more information!\\\"}\",\"[\\\"\\\",{\\\"text\\\":\\\"Aether Shards\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nAether shards can be used in Valgard stores to buy spawneggs, cosmetics, rare items and Portals!\\\\n\\\\nYou can get them in Alinor, our pvp event, through voting and a few other ways.\\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Alinor PvP Event\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\nAlinor is the island in the middle of the map, south-east of spawn.\\\\n\\\\nThe main events are at 20:00, 03:00 Central European Time (CET).\\\\n\\\\nDieing on ALinor will not drop your items, only Aether Shards drop after death in Alinor!\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\"],title:CraftCitizenCode,author:\"Craftlancer.de\",generation:3,display:{Lore:[\"A guide written by Craftlancer historian zwilling89.\"]}}";
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(commandLine, a.getName()));
            return true;
        });
        getCommand("stats").setExecutor(new StatsCommandExecutor());
        getCommand("map").setExecutor((a, b, c, d) -> {
            a.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.DARK_GREEN + "https://craftlancer.de/map.html");
            return true;
        });
        getCommand("ping").setExecutor((a, b, c, d) -> {
            Player target = null;
            
            if (d.length == 1)
                target = Bukkit.getPlayer(d[0]);
            else if (a instanceof Player)
                target = (Player) a;
            
            if (target == null)
                a.sendMessage("No player found to check ping for.");
            else
                a.sendMessage(target.getName() + "'s Ping: " + ((CraftPlayer) target).getHandle().ping + " ms");
            
            return true;
        });
        getCommand("cchelp").setExecutor(new CCHelpCommandHandler(this));
        getCommand("time").setExecutor((a, b, c, d) -> {
            a.sendMessage(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            return true;
        });
        
        new LambdaRunnable(
                () -> Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(a -> a.setStatistic(Statistic.TIME_SINCE_REST, 0))).runTaskTimer(this,
                                                                                                                                                       36000L,
                                                                                                                                                       36000L);
        
        flag = new WGNoDropFlag(this);
        serverQuests = new ServerQuests(this);
        
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        serverQuests.save();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void lecternFix(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.LECTERN && event.getItemInHand().getType() == Material.WRITTEN_BOOK)
            event.setCancelled(false);
    }
    
    @EventHandler
    public void onElytraCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        Player player = (Player) event.getWhoClicked();
        
        if (result.getType() == Material.ELYTRA) {
            event.setResult(Result.DENY);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
            player.sendMessage(ChatColor.RED + "You cannot use elytras in crafting tables!");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChestPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() != Material.CHEST)
            return;
        
        Player p = event.getPlayer();
        if(p.getStatistic(Statistic.USE_ITEM, Material.CHEST) != 0)
            return;
        
        event.getPlayer().performCommand("sethome");
        p.sendMessage(ChatColor.GOLD + "This happened because you placed down your first chest.");
        p.sendMessage(ChatColor.GOLD + "You can change your spawnpoint at any time using /sethome.");
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().setPortalCooldown(600);
    }
    
    public boolean isUsingDiscord() {
        return useDiscord;
    }
}
