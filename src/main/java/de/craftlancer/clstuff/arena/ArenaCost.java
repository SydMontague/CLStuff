package de.craftlancer.clstuff.arena;

class ArenaCost {
    private final String type;
    private final int amount;
    
    public ArenaCost(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }
    
    public int getAmount() {
        return amount;
    }
}