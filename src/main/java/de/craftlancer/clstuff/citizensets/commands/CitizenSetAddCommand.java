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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CitizenSetAddCommand extends SubCommand {
    private CitizenSetsManager csets;
    
    public CitizenSetAddCommand(Plugin plugin, CitizenSetsManager csets) {
        super(CLStuff.getAdminPermission(), plugin, false);
        this.csets = csets;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], Collections.singletonList("id"));
        if (args.length == 3)
            return Utils.getMatches(args[2], Collections.singletonList("name"));
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!checkSender(commandSender))
            return CitizenSetsManager.CC_PREFIX + "§cYou do not have permission to use this command.";
        
        Player player = (Player) commandSender;
        
        if (args.length < 2)
            return CitizenSetsManager.CC_PREFIX + "You must specify an id!";
        if (args.length < 3)
            return CitizenSetsManager.CC_PREFIX + "You must specify a display name!";
        
        String id = args[1];
        String name = args[2];
        
        if (csets.getCitizenSets().stream().anyMatch(set -> set.getId().equals(id)))
            return CitizenSetsManager.CC_PREFIX + "This id is already in use!";
        
        PlayerInventory inventory = player.getInventory();
        
        if (Arrays.stream(inventory.getContents()).allMatch(item -> realItem(item) == null))
            return CitizenSetsManager.CC_PREFIX + "You must have at least one item to add to the set!";
        if (Arrays.stream(inventory.getContents()).anyMatch(item -> realItem(item) != null
                && !item.getItemMeta().getPersistentDataContainer().has(CitizenSetsManager.KEY, PersistentDataType.STRING)))
            return CitizenSetsManager.CC_PREFIX + "One or more of the items you wish to make a set of does not have persistent data!";
        
        List<ItemStack> others = new ArrayList<>();
        
        //Add all items that aren't mainhand/offhand
        for (int x = 0; x < 36; x++) {
            if (realItem(inventory.getItem(x)) == null)
                continue;
            
            if (!realItem(inventory.getItem(x)).equals(realItem(inventory.getItemInMainHand()))
                    && !realItem(inventory.getItem(x)).equals(realItem(inventory.getItemInOffHand())))
                others.add(realItem(inventory.getItem(x)));
        }
        
        CitizenSet set = new CitizenSet(name, id,
                realItem(inventory.getHelmet()),
                realItem(inventory.getChestplate()),
                realItem(inventory.getLeggings()),
                realItem(inventory.getBoots()),
                realItem(inventory.getItemInMainHand()),
                realItem(inventory.getItemInOffHand()),
                others);
        
        csets.addCitizenSet(set);
        
        return CitizenSetsManager.CC_PREFIX + ChatColor.GREEN + "You have added a citizen set!";
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
    
    private ItemStack realItem(ItemStack item) {
        return item == null || item.getType() == Material.AIR ? null : item.clone();
    }
}
