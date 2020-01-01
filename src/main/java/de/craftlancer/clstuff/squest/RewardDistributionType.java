package de.craftlancer.clstuff.squest;

//TODO reward output type: everyone online             | item, potion, command
//TODO reward output type: X most donated              | item, potion, command
//TODO reward output type: donations above X%          | item, potion, command
//TODO reward output type: X% based on contribution    | item
//TODO reward output type: everyone who donated        | item, potion, command
public enum RewardDistributionType {
    EVERYONE_ONLINE,
    MOST_DONATED,
    DONATED_ABOVE,
    EVERY_DONATOR,
    DONATION_SHARE;

    public static RewardDistributionType getByName(String string) {
        for(RewardDistributionType a : values()) {
            if(a.name().equalsIgnoreCase(string))
                return a;
        }
        
        return null;
    }
}
