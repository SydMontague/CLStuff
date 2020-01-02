package de.craftlancer.clstuff.help;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;
import net.md_5.bungee.api.ChatColor;

public class CCHelpCommandHandler extends CommandHandler {
    
    public CCHelpCommandHandler(Plugin plugin) {
        super(plugin);
        reload();
    }
    
    private void reload() {
        getCommands().clear();
        
        registerSubCommand("portal", new CCHelpCommand(getPlugin(), this::portalHelp), "portals");
        registerSubCommand("capture",
                           new CCHelpCommand(getPlugin(), this::captureHelp),
                           "conquerpoints",
                           "capturepoints",
                           "capturepoint",
                           "conquerpoint",
                           "alinor");
        registerSubCommand("aether", new CCHelpCommand(getPlugin(), this::shardHelp), "shards", "aethershard", "shard", "aethershards");
        registerSubCommand("stonecrusher", new CCHelpCommand(getPlugin(), this::stonecrusherHelp), "stone");
        registerSubCommand("clan", new CCHelpCommand(getPlugin(), this::clanHelp), "clans");
        registerSubCommand("help", new CCHelpCommand(getPlugin(), this::help));
        registerSubCommand("reload", new CCHelpCommand("clstuff.cchelp.reload", getPlugin(), this::reloadWrapper));
        
        Configuration config = YamlConfiguration.loadConfiguration(new File(getPlugin().getDataFolder(), "cchelp.yml"));
        
        config.getKeys(false).forEach(a -> {
            ConfigurationSection section = config.getConfigurationSection(a);
            List<String> alias = section.getStringList("alias");
            String text = section.getString("message");
            
            registerSubCommand(a, new MessageSubCommand("", getPlugin(), text), alias.toArray(new String[0]));
        });
    }
    
    private void reloadWrapper(CommandSender sender) {
        if (sender.hasPermission("clstuff.cchelp.reload"))
            reload();
    }
    
    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW + "Need help? Visit our "
                + ChatColor.DARK_GREEN + "/discord" + ChatColor.YELLOW + " or " + ChatColor.DARK_GREEN + "/wiki");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp clans");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp portal");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp capture");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp aether");
        sender.sendMessage(ChatColor.YELLOW + "/cchelp stonecrusher");
    }
    
    private void stonecrusherHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW
                + "The Stone Crusher is a machine used to extract");
        sender.sendMessage(ChatColor.YELLOW + "small amounts of ore from stone and gravel. Just fill the top chest with stone, cobble or gravel.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/index.php/Stone_Crusher" + ChatColor.YELLOW
                + " for more information.");
    }
    
    private void portalHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW
                + "Get your first portal by ranking up to hireling rank. ");
        sender.sendMessage(ChatColor.YELLOW + "Be careful where you place it, since it can't be moved later.");
        sender.sendMessage(ChatColor.YELLOW + "Make sure to have enough space for a frame to generate and place down your portal (lectern).");
        sender.sendMessage(ChatColor.YELLOW + "Use \"" + ChatColor.DARK_GREEN + "/portal name x" + ChatColor.YELLOW
                + "\" to name your portal. Keep your portal name to yourself and trusted friends!");
        sender.sendMessage(ChatColor.YELLOW + "Place a written book with the portal name you want to travel to in the lectern to use the portal.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/index.php/Portals" + ChatColor.YELLOW
                + " for more information!");
    }
    
    private void captureHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW
                + "Right click the sign of a Capture Point to start capturing it.");
        sender.sendMessage(ChatColor.YELLOW + "Once the timer is full you can loot the chest, after 5 minutes everyone can open the chest.");
        sender.sendMessage(ChatColor.YELLOW + "Dying in Alinor will only drop Aether shards, you'll keep all other items.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/index.php/ConquerPoints" + ChatColor.YELLOW
                + " for more information!");
    }
    
    private void shardHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW
                + "Aether shards are gained by capturing points in Alinor.");
        sender.sendMessage(ChatColor.YELLOW + "They can be used to buy portals, custom heads and other valuable items.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/index.php/Aether" + ChatColor.YELLOW
                + " for more information!");
    }
    
    private void clanHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen]" + ChatColor.YELLOW
                + "Clans are our factions or guilds, each Clan may pick a minecraft color to represent them.");
        sender.sendMessage(ChatColor.YELLOW + "Use: " + ChatColor.DARK_GREEN + "/clan create <name> <tag> <color>" + ChatColor.YELLOW + " to create a clan.");
        sender.sendMessage(ChatColor.YELLOW + "If you are not sure what colors you may use just leave it blank.");
        sender.sendMessage(ChatColor.YELLOW + "Once created pick a color with: " + ChatColor.DARK_GREEN + "/clan <color>" + ChatColor.YELLOW
                + " colors will be suggested with tab complete.");
        sender.sendMessage(ChatColor.YELLOW + "Invite your friends with: " + ChatColor.DARK_GREEN + "/clan invite <name>");
        sender.sendMessage(ChatColor.YELLOW + "Use: " + ChatColor.DARK_GREEN + "/c <text>" + ChatColor.YELLOW + " to chat in clan chat");
        sender.sendMessage(ChatColor.YELLOW + "You may also recruit NPC soldiers to fight for you.");
        sender.sendMessage(ChatColor.YELLOW + "Visit: " + ChatColor.DARK_GREEN + "https://craftlancer.de/wiki/index.php/Clans" + ChatColor.YELLOW
                + " for more information!");
    }
}
