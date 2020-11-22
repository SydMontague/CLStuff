package de.craftlancer.clstuff.emotes;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Emote implements ConfigurationSerializable {
    private String permission;
    private String name;
    private String message;
    private Particle particle;
    private Sound sound;
    private ParticleLocation particleLocation;
    private double radius;
    private double pitch;
    private int particleAmount;
    
    public Emote(String name, String message, Particle particle, double radius, Sound sound, double pitch, int particleAmount, ParticleLocation particleLocation) {
        this.name = name;
        this.message = message;
        this.particle = particle;
        this.radius = radius;
        this.sound = sound;
        this.pitch = pitch;
        this.particleAmount = particleAmount;
        this.permission = "clstuff.emote." + name;
        this.particleLocation = particleLocation;
    }
    
    public Emote(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.message = (String) map.get("message");
        this.particle = Particle.valueOf(((String) map.get("particle")));
        this.radius = (double) map.get("radius");
        this.sound = Sound.valueOf((String) map.get("sound"));
        this.pitch = (double) map.get("pitch");
        this.particleAmount = (int) map.get("particleAmount");
        this.permission = "clstuff.emote." + name;
        this.particleLocation = ParticleLocation.fromString((String) map.get("particleLocation"));
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("name", name);
        map.put("message", message);
        map.put("particle", particle.toString());
        map.put("radius", radius);
        map.put("sound", sound.toString());
        map.put("pitch", pitch);
        map.put("particleAmount", particleAmount);
        map.put("particleLocation", particleLocation.toString());
        
        return map;
    }
    
    public void targetAll(Player sender) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getWorld().equals(sender.getWorld()) || target.getLocation().distanceSquared(sender.getLocation()) > radius * radius)
                continue;

            target.playSound(sender.getLocation(), sound, 1f, (float) pitch);
            
            if (!sender.equals(target) && !message.isEmpty())
                target.sendMessage(message.replace("%player%", sender.getName()).replace("%target%", target.getName()));
        }
        
        ParticleLocation.spawnParticle(particleLocation, sender, particle, particleAmount);
    }
    
    public void target(Player sender, Player target) {
        if (!sender.getWorld().equals(target.getWorld()) || sender.getLocation().distanceSquared(target.getLocation()) > radius * radius)
            return;
        
        if (!message.isEmpty())
            target.sendMessage(message.replace("%player%", sender.getName()).replace("%target%", target.getName()));
        
        target.playSound(target.getLocation(), sound, 1f, (float) pitch);
        
        ParticleLocation.spawnParticle(particleLocation, sender, particle, particleAmount);
    }
    
    public String getName() {
        return name;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public enum ParticleLocation {
        ABOVE,
        AROUND,
        ASS;
        
        public static ParticleLocation fromString(String string) {
            switch (string.toUpperCase()) {
                case "ABOVE":
                    return ABOVE;
                case "AROUND":
                    return AROUND;
                case "ASS":
                    return ASS;
                default:
                    return null;
            }
        }
        
        public static void spawnParticle(ParticleLocation type, Player player, Particle particle, int particleAmount) {
            if(type == null)
                return;
            
            switch(type) {
                case ABOVE:
                    player.getWorld().spawnParticle(particle, player.getLocation().add(0, 2.25, 0), particleAmount);
                    break;
                case ASS:
                    player.getWorld().spawnParticle(particle, player.getLocation().add(0, 1, 0), particleAmount);
                    break;
                case AROUND:
                    for (int i = 0; i < particleAmount; i++) 
                        player.getWorld().spawnParticle(particle, player.getLocation().add(0, 1.25, 0), 1, 0.5D, 1.25D, 0.5D);
                    break;
            }
        }
    }
}
