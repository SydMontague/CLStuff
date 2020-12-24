package de.craftlancer.clstuff.citizensets.commands;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.citizensets.CitizenSet;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
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
    
    private CitizenSetsManager csets;
    
    public CitizenSetSetIconCommand(Plugin plugin, CitizenSetsManager csets) {
        super(CLStuff.getAdminPermission(), plugin, false);
        
        this.csets = csets;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], csets.getCitizenSets().stream().map(CitizenSet::getId).collect(Collectors.toList()));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!checkSender(commandSender))
            return CitizenSetsManager.CC_PREFIX + "§cYou do not have permission to use this command.";
        
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (args.length < 2)
            return CitizenSetsManager.CC_PREFIX + "You must specify an id!";
        
        Optional<CitizenSet> optional = csets.getCitizenSets().stream().filter(set -> set.getId().equals(args[1])).findFirst();
        
        if (!optional.isPresent())
            return CitizenSetsManager.CC_PREFIX + "You must specify a valid id!";
        
        optional.get().setIcon(item == null || item.getType() == Material.AIR ? new ItemStack(Material.STONE) : item);
        
        return CitizenSetsManager.CC_PREFIX + ChatColor.GREEN + "You have set the icon for " + args[1] + ".";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
