package de.craftlancer.clstuff.heroes;

import de.craftlancer.clapi.clstuff.heroes.AbstractHeroesLocation;
import de.craftlancer.clapi.clstuff.heroes.CalculatedPlacement;
import de.craftlancer.clapi.clstuff.heroes.HeroesCategory;
import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MaterialUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class HeroesCategoryRunnable implements Runnable {
    
    private HeroesCategory category;
    private CLStuff plugin;
    private Heroes manager;
    private int order;
    
    public HeroesCategoryRunnable(CLStuff plugin, Heroes manager, HeroesCategory category, int order) {
        this.category = category;
        this.plugin = plugin;
        this.manager = manager;
        this.order = order;
    }
    
    @Override
    public void run() {
        BukkitRunnable runnable = new LambdaRunnable(() -> {
            List<CalculatedPlacement> placements = category.calculate();
            
            new LambdaRunnable(() -> {
                apply(placements);
                
                if (order < manager.getCategories().size())
                    new HeroesCategoryRunnable(plugin, manager, manager.getCategories().get(order), order + 1).run();
            }).runTask(plugin);
        });
        
        if (category.isCalculateAsync())
            runnable.runTaskAsynchronously(plugin);
        else
            runnable.runTask(plugin);
    }
    
    private void apply(List<CalculatedPlacement> placements) {
        for (int i = 0; i < placements.size(); i++) {
            CalculatedPlacement placement = placements.get(i);
            
            AbstractHeroesLocation location = manager.getHeroLocation(category.getCategoryName(), (i + 1) + "");
            
            location.getSignLocations()
                    .forEach(signLocation -> MaterialUtil.setSign(signLocation, placement.getSignContent()));
            location.getDisplayLocations().stream().filter(l -> l.getBlock().getType().name().contains("BANNER")).collect(Collectors.toList())
                    .forEach(bannerLocation -> MaterialUtil.setBanner(bannerLocation, placement.getBannerItem()));
            location.getDisplayLocations().stream().filter(l -> l.getBlock().getType().name().contains("HEAD")).collect(Collectors.toList())
                    .forEach(headLocation -> manager.addHeadUpdate(placement.getPlayer(), headLocation));
            
        }
    }
}
