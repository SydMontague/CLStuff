package de.craftlancer.clstuff.help;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;

public class CCHelpCommandHandler extends CommandHandler {

    public CCHelpCommandHandler(Plugin plugin) {
        super(plugin);
        
        registerSubCommand("portal", new CCHelpCommand(plugin, this::portalHelp), "portals");
        registerSubCommand("capture", new CCHelpCommand(plugin, this::captureHelp), "conquerpoints", "capturepoints", "capturepoint", "conquerpoint", "alinor");
        registerSubCommand("aether", new CCHelpCommand(plugin, this::shardHelp), "shards", "aethershard", "shard", "aethershards");
    }
    
    private void portalHelp(CommandSender sender) {
        sender.sendMessage("Get your first portal by ranking up to citizen rank.");
        sender.sendMessage("Make sure to have enough space for a frame to generate and place down your portal (lectern).");
        sender.sendMessage("Use \"/portal name x\" to name your portal. Keep your portal name to yourself and trusted friends!");
        sender.sendMessage("Place a written book with the portal name you want to travel to in the lectern to use the portal.");
        sender.sendMessage("Visit: https://craftlancer.de/wiki/index.php/Portals for more information!");
    }

    private void captureHelp(CommandSender sender) {
        sender.sendMessage("Right click the sign of a Capture Point to start capturing it. If you're in a clan you capture for your clan.");
        sender.sendMessage("Once the timer is full you can loot the chest, after 5 minutes everyone can open the chest.");
        sender.sendMessage("Dying in Alinor will only drop Aether shards, you'll keep all other items.");
        sender.sendMessage("Visit: https://craftlancer.de/wiki/index.php/ConquerPoints for more information!");
    }

    private void shardHelp(CommandSender sender) {
        sender.sendMessage("Aether shards are gained by capturing points in Alinor.");
        sender.sendMessage("They can be used to buy portals, custom heads and other valuable items.");
        sender.sendMessage("Visit: https://craftlancer.de/wiki/index.php/Aether for more information!");
    }
}
