package de.craftlancer.clstuff.resourcepack.command;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.core.command.CommandHandler;
import net.md_5.bungee.api.chat.TextComponent;

public class CustomBlockCommandHandler extends CommandHandler {
    public CustomBlockCommandHandler(CLStuff plugin, CustomBlockRegistry registry) {
        super(plugin, new TextComponent("§f[§4Craft§fCitizen]"));
        
        registerSubCommand("registry", new CustomBlockRegistryCommand(plugin, registry));
    }
}
