package de.craftlancer.clstuff.squest;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import de.craftlancer.clstuff.CLStuff;

/*
 * squest desc <name> <desc>
 * squest req add <name> <amount> (takes item from hand)
 * squest req list <name>
 * squest req remove <name> <id>
 * squest reward <name> <value>
 * squest status <name> <value>
 * 
 * squest list
 * squest progress <name>
 * squest info <name>
 */

public class ServerQuests {
    static {
        ConfigurationSerialization.registerClass(EmptyReward.class);
        ConfigurationSerialization.registerClass(CommandReward.class);
        ConfigurationSerialization.registerClass(BroadcastReward.class);
    }
    
    private final CLStuff plugin;
    
    public ServerQuests(CLStuff plugin) {
        this.plugin = plugin;
    }
    
}
