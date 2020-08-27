package de.craftlancer.clstuff.heroes;

import com.earth2me.essentials.IEssentials;
import de.craftlancer.core.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaltopCalculateRunnable extends BukkitRunnable {
    private IEssentials ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
    private File folder = new File(ess.getDataFolder(), "userdata");
    private List<Tuple<String, Double>> top3 = new ArrayList<>();
    private Map<String, Double> balances = new HashMap<>();
    private String first, second, third;
    private Heroes heroes;
    
    public BaltopCalculateRunnable(Heroes heroes) {
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        
        if (!doesFolderExist(folder) || folder.listFiles() == null)
            return;
        
        if (ess.getSettings().isEcoDisabled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("Internal economy functions disabled, aborting baltop.");
            }
            return;
        }
        
        createBalances();
        
        setTop3();
        
        new BaltopApplyRunnable(top3, heroes).runTask(heroes.getPlugin());
    }
    
    private void setTop3() {
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            String uuid = entry.getKey();
            Double balance = entry.getValue();
            if (first == null || balance > balances.get(first)) {
                third = second;
                second = first;
                first = uuid;
                continue;
            }
            if (second == null || balance > balances.get(second)) {
                third = second;
                second = uuid;
                continue;
            }
            if (third == null || balance > balances.get(third)) {
                third = uuid;
            }
        }
        
        if (first != null)
            top3.add(new Tuple<>(first, balances.get(first)));
        if (second != null)
            top3.add(new Tuple<>(second, balances.get(second)));
        if (third != null)
            top3.add(new Tuple<>(third, balances.get(third)));
    }
    
    private void createBalances() {
        for (File file : folder.listFiles()) {
            YamlConfiguration reader = YamlConfiguration.loadConfiguration(file);
            double balance;
            if (reader.contains("npc") && reader.getBoolean("npc"))
                continue;
            else if (!reader.getKeys(false).contains("money"))
                balance = 0.0;
            else
                balance = Double.parseDouble(reader.getString("money"));
            String name = file.getName().replace(".yml", "");
            balances.put(name, balance);
        }
    }
    
    private boolean doesFolderExist(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
