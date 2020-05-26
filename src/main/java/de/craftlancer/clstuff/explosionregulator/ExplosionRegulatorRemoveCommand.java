package de.craftlancer.clstuff.explosionregulator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorRemoveCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorRemoveCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.removemat", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 3)
            return "Not enough arguments. <id> <limit>";
        
        String id = args[1];
        Set<Material> matList = Arrays.stream(args, 2, args.length).map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toSet());

        if(!getRegulator().hasItemGroup(id))
            return "This ID isn't taken.";
        if(matList.isEmpty())
            return "You must specify materials";
        
        if(getRegulator().getItemGroup(id).removeMaterials(matList))
            return "Materials removed from group.";
        else
            return "Failed to remove Materials from group. Maybe they're not in the group.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 2)
            return Utils.getMatches(args[1], getRegulator().getIds());
        else
            return Utils.getMatches(args[args.length-1], Utils.toString(Material.values()));
    }
}
