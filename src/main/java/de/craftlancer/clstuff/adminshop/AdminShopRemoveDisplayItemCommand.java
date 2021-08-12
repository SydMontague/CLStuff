package de.craftlancer.clstuff.adminshop;

import de.craftlancer.core.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class AdminShopRemoveDisplayItemCommand extends SubCommand {
    
    private AdminShopManager manager;
    
    public AdminShopRemoveDisplayItemCommand(Plugin plugin, AdminShopManager manager) {
        super("clstuff.adminshop", plugin, false);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Collections.singletonList("<row>");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You're not allowed to run this command.";
        
        Player p = (Player) sender;
        Block b = p.getTargetBlock(null, 5);
        AdminShop shop = manager.getShop(b.getLocation());
        if (shop == null)
            return "You must look at an AdminShop.";
        
        if (args.length < 2)
            return "You must enter a row.";
        
        int i;
        try {
            i = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "You must enter a row that is between 1 and 4 (inclusive)";
        }
        
        if (i < 1 || i > 4)
            return "You must enter a row that is between 1 and 4 (inclusive)";
        
        shop.getDisplayItems()[i - 1] = new ItemStack(Material.AIR);
        shop.createMenu();
        
        return "Display item returned to default.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
