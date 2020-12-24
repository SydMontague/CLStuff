package de.craftlancer.clstuff.premium;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class DonatorTicketCommandHandler extends CommandHandler {
    public DonatorTicketCommandHandler(Plugin plugin, DonatorTicketRegistry registry) {
        super(plugin);
        
        registerSubCommand("take", new DonatorTicketTakeCommand(plugin, registry));
        registerSubCommand("give", new DonatorTicketGiveCommand(plugin, registry));
        registerSubCommand("setPremium", new DonatorTicketSetPremiumCommand(plugin, registry));
    }
}
