package de.craftlancer.clstuff.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;

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
    
    public static boolean wikiCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, ChatColor.DARK_GREEN + "https://craftcitizen.net/wiki/");
        return true;
    }
    
    public static boolean mapCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, ChatColor.DARK_GREEN + "https://craftcitizen.net/livemap/");
        return true;
    }
    
    public static boolean voteallCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, ChatColor.DARK_GREEN + "https://craftcitizen.net/voteAll.html");
        return true;
    }
    
    public static boolean storeCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.NORMAL, ChatColor.DARK_GREEN + "https://craftcitizen.tebex.io/");
        return true;
    }
    
    public static boolean timeCommand(CommandSender a, Command b, String c, String[] d) {
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.INFO, ZonedDateTime.now().format(DATE_FORMAT));
        return true;
    }
}
