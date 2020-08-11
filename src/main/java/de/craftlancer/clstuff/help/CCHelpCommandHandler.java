package de.craftlancer.clstuff.help;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.command.CommandHandler;

public class CCHelpCommandHandler extends CommandHandler {
    
    public CCHelpCommandHandler(Plugin plugin) {
        super(plugin);
        reload();
    }
    
    private void reload() {
        getCommands().clear();
        
        registerSubCommand("reload", new CCHelpCommand("clstuff.cchelp.reload", getPlugin(), this::reloadWrapper));
        
        Configuration config = YamlConfiguration.loadConfiguration(new File(getPlugin().getDataFolder(), "cchelp.yml"));
        
        config.getKeys(false).forEach(a -> {
            ConfigurationSection section = config.getConfigurationSection(a);
            List<String> alias = section.getStringList("alias");
            String text = section.getString("message");
            
            registerSubCommand(a, new MessageSubCommand("", getPlugin(), text), alias.toArray(new String[0]));
        });
    }
    
    private void reloadWrapper(CommandSender sender) {
        if (sender.hasPermission("clstuff.cchelp.reload"))
            reload();
    }
}
