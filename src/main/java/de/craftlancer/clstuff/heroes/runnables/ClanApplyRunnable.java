package de.craftlancer.clstuff.heroes.runnables;

import de.craftlancer.clclans.Clan;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.HeroesLocation;
import de.craftlancer.clstuff.heroes.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ClanApplyRunnable extends BukkitRunnable {
    private List<Clan> topClans;
    private Heroes heroes;
    
    public ClanApplyRunnable(List<Clan> topClans, Heroes heroes) {
        this.topClans = topClans;
        this.heroes = heroes;
    }
    
    @Override
    public void run() {
        int counter = 1;
        for (Clan clan : topClans) {
            HeroesLocation heroesLocation = heroes.getHeroLocation("clantop", String.valueOf(counter));
            
            List<Location> signLocationList = heroesLocation.getSignLocations();
            List<Location> bannerLocationlist = heroesLocation.getDisplayLocations();
            
            signLocationList.forEach(signLocation -> setClanSign(clan, signLocation));
            bannerLocationlist.forEach(bannerLocation -> setClanBanner(clan, bannerLocation));
            
            counter++;
        }
        new PlayerScoreCalculateRunnable(heroes).runTaskAsynchronously(heroes.getPlugin());
    }
    
    private static void setClanSign(Clan clan, Location signLocation) {
        if (signLocation == null || clan == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4)) {
            return;
        }
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
        sign.setLine(1, clan.getColor() + " " + ChatColor.BOLD + "[" + clan.getTag() + "]");
        sign.setLine(2, clan.getColor() + clan.getName());
        sign.update();
    }
    
    protected static void setClanBanner(Clan clan, Location bannerLocation) {
        if (clan == null
                || clan.getBanner() == null
                || bannerLocation == null
                || !MaterialUtil.isBanner(bannerLocation.getBlock().getType())
                || !bannerLocation.getWorld().isChunkLoaded(bannerLocation.getBlockX() >> 4, bannerLocation.getBlockZ() >> 4))
            return;
        
        
        if (bannerLocation.getBlock().getType().toString().contains("_WALL_BANNER")) {
            Directional directional = (Directional) bannerLocation.getBlock().getBlockData();
            BlockFace face = directional.getFacing();
            
            bannerLocation.getBlock().setType(Material.getMaterial(clan.getBanner().getType().toString().replace("_BANNER", "_WALL_BANNER")));
            //To apply direction
            
            //Set face to what it was before.
            Directional newFace = (Directional) bannerLocation.getBlock().getBlockData();
            newFace.setFacing(face);
            bannerLocation.getBlock().setBlockData(newFace);
        } else {
            Rotatable rotatable = (Rotatable) bannerLocation.getBlock().getBlockData();
            BlockFace face = rotatable.getRotation();
            
            bannerLocation.getBlock().setType(clan.getBanner().getType());
            
            Rotatable newRotatable = (Rotatable) bannerLocation.getBlock().getBlockData();
            newRotatable.setRotation(face);
            bannerLocation.getBlock().setBlockData(newRotatable);
        }
        
        //If the banner meta of the clan banner is null, don't set the patterns.
        
        //Set directional to previous face
        
        BannerMeta clanBanner = (BannerMeta) clan.getBanner().getItemMeta();
        Banner banner = (Banner) bannerLocation.getBlock().getState();
        
        if (clanBanner != null) {
            banner.setPatterns(clanBanner.getPatterns());
            banner.update();
        }
    }
}
