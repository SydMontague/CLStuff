package de.craftlancer.clstuff.help;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;
import net.md_5.bungee.api.ChatColor;

public class CCHelpCommandHandler extends CommandHandler {
    
    public CCHelpCommandHandler(Plugin plugin) {
        super(plugin);
        
        registerSubCommand("portal", new CCHelpCommand(plugin, this::portalHelp), "portals");
        registerSubCommand("capture", new CCHelpCommand(plugin, this::captureHelp), "conquerpoints", "capturepoints", "capturepoint", "conquerpoint", "alinor");
        registerSubCommand("aether", new CCHelpCommand(plugin, this::shardHelp), "shards", "aethershard", "shard", "aethershards");
        registerSubCommand("stonecrusher", new CCHelpCommand(plugin, this::stonecrusherHelp), "stone");
        registerSubCommand("help", new CCHelpCommand(plugin, this::help));
    }
    
    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "/cchelp aether");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp portal");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp capture");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp stonecrusher");
    }
    
    private void stonecrusherHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "The Stone Crusher is a machine used to extract small amounts of ore from stone and gravel.");
        sender.sendMessage(ChatColor.YELLOW + "Just fill the top chest with stone, cobble or gravel.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.GREEN + "https://craftlancer.de/wiki/index.php/Stone_Crusher" + ChatColor.YELLOW
                + " for more information.");
    }
    
    private void portalHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Get your first portal by ranking up to hireling rank. ");
        sender.sendMessage(ChatColor.YELLOW + "Be careful where you place it, since it can't be moved later.");
        sender.sendMessage(ChatColor.YELLOW + "Make sure to have enough space for a frame to generate and place down your portal (lectern).");
        sender.sendMessage(ChatColor.YELLOW + "Use \"" + ChatColor.GREEN + "/portal name x" + ChatColor.YELLOW
                + "\" to name your portal. Keep your portal name to yourself and trusted friends!");
        sender.sendMessage(ChatColor.YELLOW + "Place a written book with the portal name you want to travel to in the lectern to use the portal.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.GREEN + "https://craftlancer.de/wiki/index.php/Portals" + ChatColor.YELLOW
                + " for more information!");
    }
    
    private void captureHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Right click the sign of a Capture Point to start capturing it. If you're in a clan you capture for your clan.");
        sender.sendMessage(ChatColor.YELLOW + "Once the timer is full you can loot the chest, after 5 minutes everyone can open the chest.");
        sender.sendMessage(ChatColor.YELLOW + "Dying in Alinor will only drop Aether shards, you'll keep all other items.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.GREEN + "https://craftlancer.de/wiki/index.php/ConquerPoints" + ChatColor.YELLOW
                + " for more information!");
    }
    
    private void shardHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Aether shards are gained by capturing points in Alinor.");
        sender.sendMessage(ChatColor.YELLOW + "They can be used to buy portals, custom heads and other valuable items.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.GREEN + "https://craftlancer.de/wiki/index.php/Aether" + ChatColor.YELLOW
                + " for more information!");
    }
}
