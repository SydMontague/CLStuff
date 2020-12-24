package de.craftlancer.clstuff.premium;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DonatorTicketRegistry {
    
    private static DonatorTicketRegistry instance;
    
    private CLStuff plugin;
    private File dataFile;
    private long pointClaimCooldown = 2592000000L;
    private List<DonatorTicketAccount> accounts;
    
    public DonatorTicketRegistry(CLStuff plugin) {
        instance = this;
        this.plugin = plugin;
        
        ConfigurationSerialization.registerClass(DonatorTicketAccount.class);
        
        dataFile = new File(plugin.getDataFolder(), "donatorTicketRegistry.yml");
        
        load();
        new LambdaRunnable(() -> run()).runTaskTimer(plugin, 0, 1200);
    }
    
    private void load() {
        if (!dataFile.exists())
            plugin.saveResource(dataFile.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        
        accounts = (List<DonatorTicketAccount>) config.getList("accounts", new ArrayList<>());
        
    }
    
    public void save() {
        if (!dataFile.exists())
            plugin.saveResource(dataFile.getName(), false);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        
        config.set("accounts", accounts);
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void run() {
        accounts.stream()
                .filter(h -> h.getLastPointClaimTime() + pointClaimCooldown <= System.currentTimeMillis())
                .forEach(h -> h.updatePoints(2));
    }
    
    public int getPoints(UUID uuid) {
        Optional<DonatorTicketAccount> optional = accounts.stream().filter(h -> h.getOwner().equals(uuid)).findFirst();
        
        return optional.map(DonatorTicketAccount::getPoints).orElse(0);
    }
    
    public void updatePoints(UUID uuid, int amount) {
        Optional<DonatorTicketAccount> optional = accounts.stream().filter(h -> h.getOwner().equals(uuid)).findFirst();
        
        if (optional.isPresent())
            optional.get().updatePoints(amount);
        else
            accounts.add(new DonatorTicketAccount(uuid, amount, System.currentTimeMillis()));
    }
    
    public void setPremium(UUID uuid, boolean premium) {
        Optional<DonatorTicketAccount> optional = accounts.stream().filter(h -> h.getOwner().equals(uuid)).findFirst();
        
        optional.ifPresent(donatorTicketAccount -> donatorTicketAccount.setPremium(premium));
    }
    
    public static DonatorTicketRegistry getInstance() {
        return instance;
    }
    
    public String getPrefix() {
        return "§8[§aDonatorTokens§8]§7 ";
    }
}
