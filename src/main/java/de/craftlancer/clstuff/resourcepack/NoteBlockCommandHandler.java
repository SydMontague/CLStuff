package de.craftlancer.clstuff.resourcepack;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.command.CommandHandler;
import net.md_5.bungee.api.chat.TextComponent;

public class NoteBlockCommandHandler extends CommandHandler {
    public NoteBlockCommandHandler(CLStuff plugin, NoteBlockRegistry registry) {
        super(plugin, new TextComponent("§f[§4Craft§fCitizen]"));
        
        registerSubCommand("set", new NoteBlockSetCommand(plugin));
        registerSubCommand("registry", new NoteBlockRegistryCommand(plugin, registry));
    }
}
