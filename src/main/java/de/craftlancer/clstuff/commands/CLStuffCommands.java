package de.craftlancer.clstuff.commands;

import java.io.StringReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.NMSUtils;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class CLStuffCommands {
    private static final DateTimeFormatter DATE_FORMAT;
    
    static {
        Map<Long, String> dow = new HashMap<>();
        dow.put(1L, "Mon");
        dow.put(2L, "Tue");
        dow.put(3L, "Wed");
        dow.put(4L, "Thu");
        dow.put(5L, "Fri");
        dow.put(6L, "Sat");
        dow.put(7L, "Sun");
        Map<Long, String> moy = new HashMap<>();
        moy.put(1L, "Jan");
        moy.put(2L, "Feb");
        moy.put(3L, "Mar");
        moy.put(4L, "Apr");
        moy.put(5L, "May");
        moy.put(6L, "Jun");
        moy.put(7L, "Jul");
        moy.put(8L, "Aug");
        moy.put(9L, "Sep");
        moy.put(10L, "Oct");
        moy.put(11L, "Nov");
        moy.put(12L, "Dec");
        
        DATE_FORMAT = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, dow)
                                                    .appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
                                                    .appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, moy).appendLiteral(' ')
                                                    .appendValue(ChronoField.YEAR, 4)  // 2 digit year not handled
                                                    .appendLiteral(" §e").appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':')
                                                    .appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':')
                                                    .appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(" §7")
                                                    .appendOffset("+HHMM", "GMT")  // should handle
                                                                                   // UT/Z/EST/EDT/CST/CDT/MST/MDT/PST/MDT
                                                    .toFormatter();
    }
    
    public CLStuffCommands() {
    }
    
    private static final BaseComponent wiki = simpleLink("https://craftcitizen.net/wiki/");
    private static final BaseComponent map = simpleLink("https://craftcitizen.net/livemap/");
    private static final BaseComponent store = simpleLink("https://craftcitizen.tebex.io/");
    private static final BaseComponent voteall = simpleLink("https://craftcitizen.net/voteall.html");
    
    private static BaseComponent simpleLink(String link) {
        BaseComponent component = new TextComponent(link);
        component.setColor(ChatColor.DARK_GREEN);
        component.setClickEvent(new ClickEvent(Action.OPEN_URL, link));
        
        return component;
    }
    
    public static boolean wikiCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, wiki);
        return true;
    }
    
    public static boolean mapCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, map);
        return true;
    }
    
    public static boolean voteallCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, voteall);
        return true;
    }
    
    public static boolean storeCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, store);
        return true;
    }
    
    public static boolean timeCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.INFO, ZonedDateTime.now().format(DATE_FORMAT));
        return true;
    }
    
    public static boolean pingCommand(CommandSender a, Command b, String c, String[] d) {
        Player target = null;
        
        if (d.length == 1)
            target = Bukkit.getPlayer(d[0]);
        else if (a instanceof Player)
            target = (Player) a;
        
        if (target == null)
            a.sendMessage("No player found to check ping for.");
        else {
            int ping = NMSUtils.getPing(target);
            
            ChatColor pingColor = ChatColor.GREEN;
            if (ping < 100)
                pingColor = ChatColor.GREEN;
            else if (ping < 250)
                pingColor = ChatColor.GOLD;
            else
                pingColor = ChatColor.RED;
            
            a.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen] " + target.getDisplayName() + ChatColor.YELLOW
                    + "'s Ping: " + pingColor + ping + ChatColor.YELLOW + " ms");
            
        }
        return true;
    }
    
    public static boolean howtoplayCommand(CommandSender a, Command b, String c, String[] d) {
        String commandLine = "minecraft:give " + a.getName()
                + " written_book{pages:[\"[\\\"\\\",{\\\"text\\\":\\\" \\\\u0020 \\\\u0020 \\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\"Craft\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"Citizen\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gray\\\"},{\\\"text\\\":\\\"\\\\n\\\\nWelcome \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Peasant "
                + a.getName()
                + "\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\"!\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Brave enough to conquer \\\",\\\"italic\\\":true},{\\\"text\\\":\\\"Alinor\\\",\\\"italic\\\":true,\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/ConquerPoints\\\"}},{\\\"text\\\":\\\" or raid some \\\",\\\"color\\\":\\\"reset\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"dungeons\\\",\\\"italic\\\":true,\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Dungeons_%26_Raids\\\"}},{\\\"text\\\":\\\"?\\\\nBecome a cunning merchant, build a village or found a mighty kingdom?\\\",\\\"color\\\":\\\"reset\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit boat\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit peasant\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Medieval Fantasy Survival RPG\\\\n\\\\n\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Clan & FactionMob\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Clans\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Portals\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Portals\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"PvP Events\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/ConquerPoints\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Dungeons & Raids\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Dungeons_%26_Raids\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Ranks\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Ranks\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Rentable Shops\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Marketstalls\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Blueprints\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Blueprints\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Pumpkinbandit\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Pumpkinbandit\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Cannons\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Cannons\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Roads\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Roads\\\"}}]\",\"[\\\"\\\",{\\\"text\\\":\\\"You can:\\\\n\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" PvP\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714 \\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"Build Auto Farms\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Build Traps\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Steal Stuff & Loot\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Use Optifine\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Tip:\\\",\\\"bold\\\":true},{\\\"text\\\":\\\" Use \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/rankup\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" and \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/stats\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to unlock rewards!\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Behaviour we expect:\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Be Polite\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Help others\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Play together\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Be Mature\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Chat Spam\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Cheats/Mods\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Advertising\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Bullying\\\\n\\\\nPlease read the \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Rules\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Rules\\\"}},{\\\"text\\\":\\\"!\\\",\\\"color\\\":\\\"reset\\\"}]\"],title:\"§6A Peasants Guide\",author:ReadMe}";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        return true;
    }
    
    public static boolean dungeonsCommand(CommandSender a, Command b, String c, String[] d) {
        String commandLine = "minecraft:give " + a.getName()
                + " written_book{pages:['[\"\",{\"text\":\"The Dungeon Guide\",\"bold\":true},{\"text\":\"\\\\n\\\\nTo get a taste of dungeons you should start by challenging the \",\"color\":\"reset\"},{\"text\":\"Hoolis brothers\",\"bold\":true},{\"text\":\" at the \",\"color\":\"reset\"},{\"text\":\"spawn\",\"bold\":true},{\"text\":\"!\\\\n\\\\nUse \",\"color\":\"reset\"},{\"text\":\"/spawn\",\"bold\":true,\"color\":\"dark_green\"},{\"text\":\" or use portal: \",\"color\":\"reset\"},{\"text\":\"stockades\",\"color\":\"dark_purple\"},{\"text\":\"\\\\n\\\\nThis is an entry level dungeon and keep inventory is on.\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Finding Dungeons\",\"bold\":true},{\"text\":\"\\\\n\\\\nAll Dungeons are marked on the \",\"color\":\"reset\"},{\"text\":\"/map\",\"bold\":true,\"color\":\"dark_green\"},{\"text\":\" with their portal adresses!\\\\n\\\\nMost Dungeons can be entered at the \",\"color\":\"reset\"},{\"text\":\"valgard hub\",\"bold\":true},{\"text\":\".\\\\n\\\\nPortal: \",\"color\":\"reset\"},{\"text\":\"valgard\",\"color\":\"dark_purple\"}]','[\"\",{\"text\":\"Dungeon Progress\",\"bold\":true},{\"text\":\"\\\\n\\\\nDefeat the entry level bosses to loot keys, inorder to challenge the stronger bosses!\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u26a0 Citizens should not wander into dungeons without good gear! \\\\u26a0\",\"bold\":true}]','[\"\",{\"text\":\"Tips and Tricks\",\"bold\":true},{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Rightclick a boss to taunt it.\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Heroes might help you in a dungeon.\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Dungeons have Keep Inventory \",\"color\":\"reset\"},{\"text\":\"enabled\",\"color\":\"dark_green\"},{\"text\":\".\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_red\"},{\"text\":\" Raids have Keep Inventory \",\"color\":\"reset\"},{\"text\":\"disabled\",\"color\":\"dark_red\"},{\"text\":\".\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Loot and Treasure\",\"bold\":true},{\"text\":\"\\\\n\\\\nMany a Citizen has made his fortune in a dungeon.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Custom 3D Items\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6 \",\"color\":\"dark_green\"},{\"text\":\"Colored Items\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Many Collectables\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Custom Potions\\\\n \",\"color\":\"reset\"}]'],title:\"§6Dungeons and Raids\",author:Bjorn,display:{Lore:[\"Bjorn's Survival Guide to Dungeons\"]}}";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        
        // TODO give portal book
        
        return true;
    }

    public static boolean giveClaimblocksCommand(CommandSender a, Command b, String c, String[] args) {
        if (args.length < 2)
            return false;
        
        if (!(a instanceof Player))
            return false;
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Player sender = (Player) a;
        int amount = Utils.parseIntegerOrDefault(args[1], 0);
        
        if (amount <= 0) {
            MessageUtil.sendMessage(CLStuff.getInstance(), sender, MessageLevel.WARNING, "You must specify a positive amount of claimblocks.");
            return true;
        }
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            MessageUtil.sendMessage(CLStuff.getInstance(), sender, MessageLevel.WARNING, "Target player not found.");
            return true;
        }
        
        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(sender.getUniqueId());
        PlayerData targetData = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId());
        
        if (senderData.getRemainingClaimBlocks() < amount) {
            MessageUtil.sendMessage(CLStuff.getInstance(), sender, MessageLevel.WARNING, "You don't have enough claimblocks to send.");
            return true;
        }
        
        senderData.setBonusClaimBlocks(senderData.getBonusClaimBlocks() - amount);
        targetData.setBonusClaimBlocks(targetData.getBonusClaimBlocks() + amount);
        
        MessageUtil.sendMessage(CLStuff.getInstance(), sender, MessageLevel.INFO, String.format("You sent %d claimblocks to %s.", amount, target.getName()));
        if (target.isOnline())
            MessageUtil.sendMessage(CLStuff.getInstance(), target.getPlayer(), MessageLevel.INFO, String.format("%s sent you %d claimblocks.", sender.getName(), amount));
        else
            GriefPrevention.instance.dataStore.savePlayerData(target.getUniqueId(), targetData);
        
        return true;
    }
    
    public static boolean fixItem(CommandSender a, Command b, String c, String[] d) {
        if (!(a instanceof Player))
            return true;
        
        Player p = (Player) a;
        PlayerInventory inv = p.getInventory();
        int fixed = 0;
        
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            Material type = item != null ? item.getType() : Material.AIR;
            
            switch (type) {
                case INK_SAC:
                case MUSIC_DISC_CHIRP:
                case MUSIC_DISC_WAIT:
                case TRIPWIRE_HOOK:
                case COMPASS:
                case STONE:
                case LILY_PAD:
                case CHEST:
                case PLAYER_HEAD:
                    break;
                default:
                    continue;
            }
            
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", item);
            YamlConfiguration config2 = YamlConfiguration.loadConfiguration(new StringReader(config.saveToString()));
            ItemStack newItem = config2.getItemStack("item");
            
            if (newItem != null && item != null && !item.isSimilar(newItem)) {
                fixed++;
                inv.setItem(i, newItem);
            }
        }
        
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.INFO, String.format("Fixed %d items in your Inventory.", fixed));
        return true;
    }
    
    public static boolean countEntitiesCommand(CommandSender a, Command b, String c, String[] d) {
        if (!a.isOp())
            return true;
        
        Bukkit.getWorlds().forEach(w -> {
            Map<EntityType, EntityEntry> entityMap = new EnumMap<>(EntityType.class);
            int totalCount = 0;
            int totalActiveCount = 0;
            
            for (Entity e : w.getEntities()) {
                boolean isActive = NMSUtils.isEntityActive(e);
                entityMap.putIfAbsent(e.getType(), new EntityEntry());
                entityMap.get(e.getType()).add(isActive);
                totalCount++;
                if (isActive)
                    totalActiveCount++;
            }
            
            a.sendMessage("Entities in World " + w.getName());
            a.sendMessage(String.format("Total: %d/%d", totalActiveCount, totalCount));
            entityMap.forEach((k, v) -> a.sendMessage(String.format("%s: %d/%d", k.name(), v.activeEntityCount, v.entityCount)));
            a.sendMessage("=========");
        });
        
        return true;
    }

    
    static class EntityEntry {
        int entityCount = 0;
        int activeEntityCount = 0;
        
        public void add(boolean isEntityActive) {
            this.entityCount += 1;
            this.activeEntityCount += isEntityActive ? 1 : 0;
        }
    }
}
