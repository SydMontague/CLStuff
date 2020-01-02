package de.craftlancer.clstuff.squest;

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
