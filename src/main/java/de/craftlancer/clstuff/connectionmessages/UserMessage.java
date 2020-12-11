package de.craftlancer.clstuff.connectionmessages;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserMessage implements ConfigurationSerializable {
    private UUID owner;
    private String login;
    private String logout;
    
    public UserMessage(UUID owner) {
        this.owner = owner;
    }
    
    public UserMessage(Map<String, Object> map) {
        this.owner = UUID.fromString((String) map.get("owner"));
        this.login = (String) map.get("login");
        this.logout = (String) map.get("logout");
    }
    
    
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("owner", owner.toString());
        map.put("login", login);
        map.put("logout", logout);
        
        return map;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getLogout() {
        return logout;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public void setLogout(String logout) {
        this.logout = logout;
    }
    
    public boolean hasLogin() {
        return login != null;
    }
    
    public boolean hasLogout() {
        return logout != null;
    }
}
