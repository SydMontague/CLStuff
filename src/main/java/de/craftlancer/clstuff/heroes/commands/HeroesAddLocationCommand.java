package de.craftlancer.clstuff.heroes.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.HeroesLocation;
import de.craftlancer.clstuff.heroes.MaterialUtil;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;

public class HeroesAddLocationCommand extends SubCommand {
    private Heroes heroes;
    
    public HeroesAddLocationCommand(Plugin plugin, Heroes heroes) {
        super("clstuff.heroes.admin", plugin, false);
        
        this.heroes = heroes;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Arrays.asList("sign", "head", "banner"));
        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("head"))
                return Utils.getMatches(args[2], Arrays.asList("baltop", "playertop"));
            else if (args[1].equalsIgnoreCase("banner"))
                return Utils.getMatches(args[2], Collections.singletonList("clantop"));
            else
                return Utils.getMatches(args[2], Arrays.asList("clantop", "baltop", "playertop"));
        }
        if (args.length == 4)
            return Arrays.asList("1", "2", "3");
        return new ArrayList<>();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        
        if (!(sender instanceof Player))
            return null;
        
        Player player = (Player) sender;
        
        Location blockLocation = player.getTargetBlock(null, 5).getLocation();
        Material material = blockLocation.getBlock().getType();
        
        if (args.length < 3)
            return heroes.getPrefix() + "§cYou must enter 3 arguments!";
        
        String type = args[1];
        String category = args[2];
        String ranking = args[3];
        
        if (type.equalsIgnoreCase("sign")) {
            if (!MaterialUtil.isSign(material))
                return heroes.getPrefix() + "§cYou are not looking at a sign!";
        } else if (type.equalsIgnoreCase("head")) {
            if (!MaterialUtil.isHead(material))
                return heroes.getPrefix() + "§cYou are not looking at a head!";
        } else if (type.equalsIgnoreCase("banner")) {
            if (!MaterialUtil.isBanner(material))
                return heroes.getPrefix() + "§cYou are not looking at a banner!";
        } else
            return heroes.getPrefix() + "§cYou must enter §e'banner'§c, §e'sign'§c, or §e'head'§c in the first argument!";
        
        if (!category.equalsIgnoreCase("baltop") && !category.equalsIgnoreCase("clantop") && !category.equalsIgnoreCase("playertop"))
            return heroes.getPrefix() + "§cYou must specify baltop/clantop/playertop!";
        
        if (!ranking.equals("1") && !ranking.equals("2") && !ranking.equals("3"))
            return heroes.getPrefix() + "§cYou must specify 1/2/3!";
        
        setLocation(category, ranking, type, blockLocation);
        
        return heroes.getPrefix() + "§aLocation has been set.";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
    
    /**
     * @param category location category (playertop/baltop/clantop)
     * @param ranking  location ranking (1/2/3)
     * @param type     location type (head/display)
     */
    private void setLocation(String category, String ranking, String type, Location location) {
        HeroesLocation heroesLocation = heroes.getHeroLocation(category, ranking);
        
        if (type.equalsIgnoreCase("sign"))
            heroesLocation.addSignLocation(location);
        else
            heroesLocation.addDisplayLocation(location);
        
        heroes.addHeroesLocation(heroesLocation);
    }
}
