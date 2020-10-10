package de.craftlancer.clstuff.emotes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
    
    public void run(Player sender) {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> sender.getLocation().distanceSquared(p.getLocation()) <= Math.pow(radius, 2)).collect(Collectors.toList());
        if (!message.equalsIgnoreCase(""))
            players.stream().filter(p -> !p.equals(sender))
                    .forEach(p -> p.sendMessage(message.replaceAll("%player%", sender.getName()).replaceAll("%target%", p.getName())));
        
        players.forEach(p -> p.playSound(p.getLocation(), sound, 1f, (float) pitch));
        
        ParticleLocation.spawnParticle(particleLocation, sender, particle, particleAmount);
    }
    
    public void target(Player sender, Player target) {
        if (sender.getLocation().distanceSquared(target.getLocation()) > Math.pow(radius, 2))
            return;
        
        if (!message.equalsIgnoreCase(""))
            target.sendMessage(message.replaceAll("%player%", sender.getName()).replaceAll("%target%", target.getName()));
        
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
            if (type == ABOVE)
                player.getWorld().spawnParticle(particle, player.getLocation().clone().add(0, 2.25, 0), particleAmount);
            else if (type == ASS)
                player.getWorld().spawnParticle(particle, player.getLocation().clone().add(0, 1, 0), particleAmount);
            else if (type == AROUND) {
                for (int i = 0; i < particleAmount; i++) {
                    Location ploc = player.getLocation();
                    Location location = new Location(ploc.getWorld(),
                            ploc.getX() + (((double) new Random().nextInt(100) - 50) / 100),
                            ploc.getY() + (((double) new Random().nextInt(25) / 10)),
                            ploc.getZ() + (((double) new Random().nextInt(100) - 50) / 100));
                    player.getWorld().spawnParticle(particle, location, 1);
                }
            }
        }
    }
}
