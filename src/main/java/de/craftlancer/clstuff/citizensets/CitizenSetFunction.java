package de.craftlancer.clstuff.citizensets;

import de.craftlancer.core.util.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CitizenSetFunction implements ConfigurationSerializable {
    private String id;
    
    CitizenSetFunction(String id) {
        this.id = id;
    }
    
    public CitizenSetFunction(Map<String, Object> map) {
        this.id = (String) map.get("id");
    }
    
    abstract BiConsumer<Player, Long> getConsumer();
    
    /**
     * Ran every 1 tick
     */
    public void run(Player player, long tickId) {
        getConsumer().accept(player, tickId);
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", id);
        
        return map;
    }
    
    public static class FunctionWaterBreathing extends CitizenSetFunction {
        
        FunctionWaterBreathing(String id) {
            super(id);
        }
        
        public FunctionWaterBreathing(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickID) -> {
                if (tickID % 40 == 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 45, 0));
            };
        }
    }
    
    public static class FunctionFireResistance extends CitizenSetFunction {
        
        FunctionFireResistance(String id) {
            super(id);
        }
        
        public FunctionFireResistance(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickId) -> {
                if (tickId % 40 == 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 45, 0));
            };
        }
    }
    
    public static class FunctionNightVision extends CitizenSetFunction {
        
        FunctionNightVision(String id) {
            super(id);
        }
        
        public FunctionNightVision(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickId) -> {
                if (tickId % 40 == 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 45, 0));
            };
        }
    }
    
    public static abstract class FunctionParticle extends CitizenSetFunction {
        
        private Color color;
        
        FunctionParticle(String id, Color color) {
            super(id);
            
            this.color = color;
        }
        
        public FunctionParticle(Map<String, Object> map) {
            super(map);
            
            color = (Color) map.get("color");
        }
        
        public Color getColor() {
            return color;
        }
        
        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = super.serialize();
            
            map.put("color", color);
            
            return map;
        }
    }
    
    public static class FunctionHaloParticle extends FunctionParticle {
        
        FunctionHaloParticle(String id, Color color) {
            super(id, color);
        }
        
        public FunctionHaloParticle(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickId) -> {
                if (tickId % 16 == 0)
                    ParticleUtil.spawnSpinningParticleCircle(() -> player.getLocation().clone().add(0, player.isSneaking() ? 2 : 2.25, 0), 16, 2, 1, 0.5, getColor());
            };
        }
    }
    
    public static class FunctionAuraParticle extends FunctionParticle {
        
        FunctionAuraParticle(String id, Color color) {
            super(id, color);
        }
        
        public FunctionAuraParticle(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickId) -> {
                if (tickId % 16 == 0)
                    ParticleUtil.spawnSpinningParticleCircle(() -> player.getLocation().clone().add(0, 2.25, 0), 16, 2, 1, 0.5, getColor());
            };
        }
    }
    
    public static class FunctionTrailParticle extends FunctionParticle {
        
        FunctionTrailParticle(String id, Color color) {
            super(id, color);
        }
        
        public FunctionTrailParticle(Map<String, Object> map) {
            super(map);
        }
        
        @Override
        BiConsumer<Player, Long> getConsumer() {
            return (player, tickId) -> {
                Particle.DustOptions particle = new Particle.DustOptions(getColor(), 2.0F);
                
                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 2, particle);
            };
        }
    }
    
    public enum FunctionType {
        WATER_BREATHING,
        FIRE_RESISTANCE,
        NIGHT_VISION,
        TRAIL_PARTICLE,
        HALO_PARTICLE;
        
        private static FunctionType fromString(String type) {
            switch (type.toLowerCase()) {
                case "water_breathing":
                    return WATER_BREATHING;
                case "fire_resistance":
                    return FIRE_RESISTANCE;
                case "night_vision":
                    return NIGHT_VISION;
                case "trail_particle":
                    return TRAIL_PARTICLE;
                case "halo_particle":
                    return HALO_PARTICLE;
                default:
                    return null;
            }
        }
        
        public static CitizenSetFunction getFunction(String type, String id) {
            return getFunction(type, id, Color.WHITE);
        }
        
        public static CitizenSetFunction getFunction(String type, String id, Color color) {
            switch (fromString(type)) {
                case HALO_PARTICLE:
                    return new FunctionHaloParticle(id, color);
                case TRAIL_PARTICLE:
                    return new FunctionTrailParticle(id, color);
                //case :
                //    return new FunctionAuraParticle(id, color);
                case WATER_BREATHING:
                    return new FunctionWaterBreathing(id);
                case FIRE_RESISTANCE:
                    return new FunctionFireResistance(id);
                case NIGHT_VISION:
                    return new FunctionNightVision(id);
                default:
                    return null;
            }
        }
    }
}
