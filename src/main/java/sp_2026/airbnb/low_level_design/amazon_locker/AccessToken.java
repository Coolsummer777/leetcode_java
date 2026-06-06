package sp_2026.airbnb.low_level_design.amazon_locker;

import java.util.UUID;

public class AccessToken {

    private final String token;
    private final long expiresAt;
    private final Component component;

   

    public AccessToken(Component component) {
        this.token = UUID.randomUUID().toString();

        // expires in 24 hours
        this.expiresAt = System.currentTimeMillis()  + 1000 * 60 * 60 * 24;
        this.component = component;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresAt() {    
        return expiresAt;
    }

    public Component getComponent() {
        return component;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis()  > expiresAt;
    }
    
}
