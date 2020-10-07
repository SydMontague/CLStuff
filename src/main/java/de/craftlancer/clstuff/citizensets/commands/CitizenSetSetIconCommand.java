package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetsListener;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitizenSetSetIconCommand extends SubCommand {
    public CitizenSetSetIconCommand(Plugin plugin) {
        super("clstuff.citizenset.admin", plugin, true);
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], CitizenSetsListener.getInstance().getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return null;
        
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (args.length < 2)
            return CitizenSetsListener.CC_PREFIX + "You must specify an id!";
        
        Optional<CitizenSet> optional = CitizenSetsListener.getInstance().getCitizenSets().stream().filter(set -> set.getId().equals(args[1])).findFirst();
        
        if (!optional.isPresent())
            return CitizenSetsListener.CC_PREFIX + "You must specify a valid id!";
        
        optional.get().setIcon(item == null ? new ItemStack(Material.STONE) : item);
        
        return CitizenSetsListener.CC_PREFIX + ChatColor.GREEN + "You have set the icon for " + args[1] + ".";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
