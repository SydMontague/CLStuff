package de.craftlancer.clstuff.heroes.commands;

import de.craftlancer.clapi.clstuff.heroes.AbstractHeroesLocation;
import de.craftlancer.clapi.clstuff.heroes.HeroesCategory;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MaterialUtil;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeroesAddLocationCommand extends SubCommand {
    private Heroes heroes;
    
    public HeroesAddLocationCommand(Plugin plugin, Heroes heroes) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.heroes = heroes;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Arrays.asList("sign", "head", "banner"));
        if (args.length == 3)
            return heroes.getCategories().stream().filter(c -> args[1].equalsIgnoreCase("sign")
                    || (args[1].equalsIgnoreCase("head") && c.hasHead()) || (args[1].equalsIgnoreCase("banner") && c.hasBanner()))
                    .map(HeroesCategory::getCategoryName).collect(Collectors.toList());
        if (args.length == 4)
            return Arrays.asList("1", "2", "3");
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(heroes, sender, MessageLevel.WARNING, "You do not have access to this command.");
            return null;
        }
        Player player = (Player) sender;
        
        Location blockLocation = player.getTargetBlock(null, 5).getLocation();
        Material material = blockLocation.getBlock().getType();
        
        if (args.length < 3) {
            MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou must enter 3 arguments!");
            return null;
        }
        
        String type = args[1];
        Optional<HeroesCategory> optional = heroes.getCategories().stream().filter(c -> c.getCategoryName().equalsIgnoreCase(args[2])).findFirst();
        String ranking = args[3];
        
        if (type.equalsIgnoreCase("sign")) {
            if (!MaterialUtil.isSign(material)) {
                MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou are not looking at a sign!");
                return null;
            }
        } else if (type.equalsIgnoreCase("head")) {
            if (!MaterialUtil.isHead(material)) {
                MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou are not looking at a head!");
                return null;
            }
        } else if (type.equalsIgnoreCase("banner")) {
            if (!MaterialUtil.isBanner(material)) {
                MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou are not looking at a banner!");
                return null;
            }
        } else {
            MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou must enter §e'banner'§c, §e'sign'§c, or §e'head'§c in the first argument!");
            return null;
        }
        if (!optional.isPresent()) {
            MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou must specify a category.");
            return null;
        }
        
        if (!ranking.equals("1") && !ranking.equals("2") && !ranking.equals("3")) {
            MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§cYou must specify 1/2/3!");
            return null;
        }
        
        if (heroes.getHeroesLocations().stream().anyMatch(heroesLocation ->
                heroesLocation.getDisplayLocations().contains(blockLocation) || heroesLocation.getSignLocations().contains(blockLocation))) {
            MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "A location already exists here.");
            return null;
        } else
            setLocation(optional.get(), ranking, type, blockLocation);
        
        MessageUtil.sendMessage(heroes, player, MessageLevel.INFO, "§aLocation has been set.");
        return null;
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
    
    /**
     * @param category location category (playertop/baltop/clantop)
     * @param ranking  location ranking (1/2/3)
     * @param type     location type (head/display)
     */
    private void setLocation(HeroesCategory category, String ranking, String type, Location location) {
        AbstractHeroesLocation loc = heroes.getHeroLocation(category.getCategoryName(), ranking);
        
        if (type.equalsIgnoreCase("sign"))
            loc.getSignLocations().add(location);
        else
            loc.getDisplayLocations().add(location);
    }
}
