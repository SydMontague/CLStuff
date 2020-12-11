package de.craftlancer.clstuff.connectionmessages;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PendingMessage implements ConfigurationSerializable {
    
    private String message;
    private MessageType type;
    private UUID owner;
    
    public PendingMessage(String message, UUID owner, MessageType type) {
        this.message = message;
        this.owner = owner;
        this.type = type;
    }
    
    public PendingMessage(Map<String, Object> map) {
        message = (String) map.get("message");
        type = MessageType.valueOf((String) map.get("type"));
        owner = UUID.fromString((String) map.get("owner"));
    }
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("message", message);
        map.put("type", type.name());
        map.put("owner", owner.toString());
        
        return map;
    }
    
    public enum MessageType {
        LOGIN,
        LOGOUT
    }
    
    public String getMessage() {
        return message;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public MessageType getType() {
        return type;
    }
}
