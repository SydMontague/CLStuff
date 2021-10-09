package de.craftlancer.clstuff.deathmessages;

import de.craftlancer.clstuff.CLStuff;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.util.List;

public class DeathMessageSettings {
    
    private static YamlConfiguration config;
    
    static String PREFIX;
    //Damage caused by being in the area when a block explodes.
    private static List<String> BLOCK_EXPLOSION;
    //Damage caused when an entity contacts a block such as a Cactus.
    private static List<String> CONTACT;
    //Damage caused when an entity is colliding with too many entities due to the maxEntityCramming game rule.
    private static List<String> CRAMMING;
    //Custom damage.
    private static List<String> CUSTOM;
    //Damage caused by a dragon breathing fire.
    private static List<String> DRAGON_BREATH;
    //Damage caused by running out of air while in water
    private static List<String> DROWNING;
    //Damage caused when an player attacks another player.
    private static List<String> PLAYER_ATTACK;
    //Damage caused when a non player living entity attacks a player
    private static List<String> ENTITY_ATTACK;
    //Damage caused by being in the area when an entity, such as a Creeper, explodes.
    private static List<String> ENTITY_EXPLOSION;
    //Damage caused when an entity attacks another entity in a sweep attack.
    private static List<String> ENTITY_SWEEP_ATTACK;
    //Damage caused when an entity falls a distance greater than 3 blocks
    private static List<String> FALL;
    //Damage caused by being hit by a falling block which deals damage
    private static List<String> FALLING_BLOCK;
    //Damage caused by direct exposure to fire
    private static List<String> FIRE;
    //Damage caused due to burns caused by fire
    private static List<String> FIRE_TICK;
    //Damage caused when an entity runs into a wall.
    private static List<String> FLY_INTO_WALL;
    //Damage caused when an entity steps on Material.MAGMA_BLOCK.
    private static List<String> HOT_FLOOR;
    //Damage caused by direct exposure to lava
    private static List<String> LAVA;
    //Damage caused by being struck by lightning
    private static List<String> LIGHTNING;
    //Damage caused by being hit by a damage potion or spell
    private static List<String> MAGIC;
    //Damage caused due to an ongoing poison effect
    private static List<String> POISON;
    //Damage caused by a projectile shot by a player
    private static List<String> PROJECTILE_PLAYER;
    //Damage caused by a projectile shot by a non-player living entity
    private static List<String> PROJECTILE_LIVING;
    //Damage caused by a non living entity shooting a projectile.
    private static List<String> PROJECTILE_NOT_LIVING;
    //Damage caused by starving due to having an empty hunger bar
    private static List<String> STARVATION;
    //Damage caused by being put in a block
    private static List<String> SUFFOCATION;
    //Damage caused by committing suicide using the command "/kill"
    private static List<String> SUICIDE;
    //Damage caused in retaliation to another attack by the Thorns enchantment.
    private static List<String> THORNS;
    //Damage caused by falling into the void
    private static List<String> VOID;
    //Damage caused by Wither potion effect
    private static List<String> WITHER;
    
    static void load(CLStuff plugin) {
        File file = new File(plugin.getDataFolder(), "deathMessages.yml");
        if (!file.exists())
            plugin.saveResource(file.getName(), false);
        config = YamlConfiguration.loadConfiguration(file);
        
        PREFIX = config.getString("prefix");
        CONTACT = config.getStringList("contact");
        PLAYER_ATTACK = config.getStringList("player_attack");
        ENTITY_ATTACK = config.getStringList("entity_attack");
        ENTITY_SWEEP_ATTACK = config.getStringList("entity_sweep_attack");
        PROJECTILE_PLAYER = config.getStringList("projectile_player");
        PROJECTILE_LIVING = config.getStringList("projectile_living");
        PROJECTILE_NOT_LIVING = config.getStringList("projectile_not_living");
        SUFFOCATION = config.getStringList("suffocation");
        FALL = config.getStringList("fall");
        FIRE = config.getStringList("fire");
        FIRE_TICK = config.getStringList("fire_tick");
        LAVA = config.getStringList("lava");
        DROWNING = config.getStringList("drowning");
        BLOCK_EXPLOSION = config.getStringList("block_explosion");
        ENTITY_EXPLOSION = config.getStringList("entity_explosion");
        VOID = config.getStringList("void");
        LIGHTNING = config.getStringList("lightning");
        SUICIDE = config.getStringList("suicide");
        STARVATION = config.getStringList("suicide");
        POISON = config.getStringList("poison");
        MAGIC = config.getStringList("magic");
        WITHER = config.getStringList("wither");
        FALLING_BLOCK = config.getStringList("falling_block");
        THORNS = config.getStringList("thorns");
        DRAGON_BREATH = config.getStringList("dragon_breath");
        CUSTOM = config.getStringList("custom");
        FLY_INTO_WALL = config.getStringList("fly_into_wall");
        HOT_FLOOR = config.getStringList("hot_floor");
        CRAMMING = config.getStringList("cramming");
    }
    
    static String getDeathToNonLivingEntityMessage(EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case BLOCK_EXPLOSION:
                return getRandom(BLOCK_EXPLOSION);
            case FALL:
                return getRandom(FALL);
            case FALLING_BLOCK:
                return getRandom(FALLING_BLOCK);
            case FIRE:
                return getRandom(FIRE);
            case LAVA:
                return getRandom(LAVA);
            case VOID:
                return getRandom(VOID);
            case MAGIC:
                return getRandom(MAGIC);
            case CUSTOM:
                return getRandom(CUSTOM);
            case POISON:
                return getRandom(POISON);
            case THORNS:
                return getRandom(THORNS);
            case WITHER:
                return getRandom(WITHER);
            case CONTACT:
                return getRandom(CONTACT);
            case SUICIDE:
                return getRandom(SUICIDE);
            case CRAMMING:
                return getRandom(CRAMMING);
            case DROWNING:
                return getRandom(DROWNING);
            case FIRE_TICK:
                return getRandom(FIRE_TICK);
            case HOT_FLOOR:
                return getRandom(HOT_FLOOR);
            case LIGHTNING:
                return getRandom(LIGHTNING);
            case STARVATION:
                return getRandom(STARVATION);
            case SUFFOCATION:
                return getRandom(SUFFOCATION);
            case DRAGON_BREATH:
                return getRandom(DRAGON_BREATH);
            case FLY_INTO_WALL:
                return getRandom(FLY_INTO_WALL);
            case ENTITY_EXPLOSION:
                return getRandom(ENTITY_EXPLOSION);
            default:
                return "%player% died";
        }
    }
    
    static String getDeathToLivingEntityMessage(String cause, Entity killer) {
        switch (cause.toUpperCase()) {
            case "PLAYER_ATTACK":
                return getRandom(PLAYER_ATTACK);
            case "ENTITY_EXPLOSION":
                return killer != null && config.contains(killer.getName().toLowerCase()) ? getRandom(config.getStringList(killer.getName().toLowerCase())) : getRandom(ENTITY_EXPLOSION);
            case "ENTITY_ATTACK":
                return config.contains(killer.getName().toLowerCase()) ? getRandom(config.getStringList(killer.getName().toLowerCase())) : getRandom(ENTITY_ATTACK);
            case "PROJECTILE_LIVING":
                return config.contains(killer.getName().toLowerCase()) ? getRandom(config.getStringList(killer.getName().toLowerCase())) : getRandom(PROJECTILE_LIVING);
            case "ENTITY_SWEEP_ATTACK":
                return getRandom(ENTITY_SWEEP_ATTACK);
            case "PROJECTILE_PLAYER":
                return getRandom(PROJECTILE_PLAYER);
            case "PROJECTILE_NON_LIVING":
                return getRandom(PROJECTILE_NOT_LIVING);
            default:
                return "%player% died";
        }
    }
    
    private static String getRandom(List<String> list) {
        return list.get((int) (Math.random() * list.size()));
    }
}
