package de.craftlancer.clstuff.explosionregulator;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public class ExplosionRegulatorCreateCommand extends ExplosionRegulatorSubCommand {
    
    public ExplosionRegulatorCreateCommand(Plugin plugin, ExplosionRegulator regulator) {
        super("clstuff.explonerf.create", plugin, true, regulator);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 5)
            return "Not enough arguments. <id> <limit> <freeLimit> <materials...>";
        
        String id = args[1];
        int limit = Utils.parseIntegerOrDefault(args[2], 0);
        int freeLimit = Utils.parseIntegerOrDefault(args[3], 0);
        float minimalYield = Utils.parseFloatOrDefault(args[4], 0f);
        Set<Material> matList = Arrays.stream(args, 5, args.length).map(Material::getMaterial).filter(Objects::nonNull).collect(Collectors.toSet());
        
        if(getRegulator().hasItemGroup(id))
            return "This ID is already taken.";
        
        getRegulator().addItemGroup(id, new ItemGroup(limit, freeLimit, minimalYield, matList));
        return "Group successfully added.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
