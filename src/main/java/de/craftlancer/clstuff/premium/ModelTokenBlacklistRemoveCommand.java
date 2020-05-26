package de.craftlancer.clstuff.premium;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ModelTokenBlacklistRemoveCommand extends ModelTokenSubCommand {
    
    public ModelTokenBlacklistRemoveCommand(Plugin plugin, ModelToken token) {
        super("clstuff.modeltoken.blacklist.add", plugin, true, token);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You can't run this command.";

        if(args.length < 4)
            return "Not enough arguments.";
        
        Material mat = Material.matchMaterial(args[2]);
        List<Integer> cmdList = Arrays.stream(args, 3, args.length).map(a -> Utils.parseIntegerOrDefault(a, -1)).filter(a -> a != -1).collect(Collectors.toList());
        
        if (getToken().removeBlacklist(mat, cmdList))
            return "CustomModelData successfully added to blacklist.";
        else
            return "Couldn't add CustomModelData to blacklist, maybe it's already on the list?";
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 3)
            return Utils.getMatches(args[2], Utils.toString(Material.values()));
        
        return super.onTabComplete(sender, args);
    }
    
    @Override
    public void help(CommandSender sender) {
    }
    
}
