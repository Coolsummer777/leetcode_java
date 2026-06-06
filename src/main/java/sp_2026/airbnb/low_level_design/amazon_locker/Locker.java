package sp_2026.airbnb.low_level_design.amazon_locker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Locker {
    private Component[] components;
    private Map<String, AccessToken> accessTokens;

    public Locker(Component[] components) {
        this.components = components;
        this.accessTokens = new ConcurrentHashMap<>();
    }


    // public methods
    public AccessToken putCargo(Size size, String cargo) {

        // get an available component
        Component component = getAvailableComponent(size);
        if (component == null) {
            throw new RuntimeException("No available component found");
        }
        
        // set the component as used
        component.open();
        component.setCargo(cargo);
        component.setUsed();

        // generate an access token
        AccessToken accessToken = new AccessToken(component);
        accessTokens.put(accessToken.getToken(), accessToken);
        return accessToken;
    }

    public String getCargo(String token) {
        // check if the token is null or empty
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid access token");
        }

        // get the access token from the map
        // check if the access token is in the map
        AccessToken accessToken = accessTokens.get(token);
        if (accessToken == null) {
            throw new RuntimeException("Access token not found");
        }

        // check if the access token is expired
        if (accessToken.isExpired()) {
            throw new RuntimeException("Access token expired");
        }

        // get the component from the access token
        Component component = accessToken.getComponent();
        if (component == null) {
            throw new RuntimeException("Component not found");
        }

        component.open();
        String cargo = component.getCargo();

        // set the component as free
        resetComponent(component);

        // remove the access token from the map
        removeAccessToken(accessToken);
        return cargo;
    }

    public synchronized void clearExpiredAccessTokens() {
        List<AccessToken> expiredAccessTokens = new ArrayList<>();
        for (AccessToken accessToken : accessTokens.values()) {
            if (accessToken.isExpired()) {
                expiredAccessTokens.add(accessToken);
            }
        }
        for (AccessToken accessToken : expiredAccessTokens) {
            resetComponent(accessToken.getComponent());
            removeAccessToken(accessToken);
        }
    }


    // private methods
    private Component getAvailableComponent(Size size) {
        Component best = null;

        for (Component component : components) {
            if (component.getSize() == size) {
                if (component.tryReserve()) {
                    resetComponent(best);
                    best = component;
                    break;
                }
            }
            if (component.getSize().getValue() > size.getValue()) {
                if (best == null || component.getSize().getValue() < best.getSize().getValue()) {
                    if (component.tryReserve()) {
                        resetComponent(best);
                        best = component;
                    }
                }
            }

        }
        return best;
    }
    
    private void removeAccessToken(AccessToken accessToken) {
        accessTokens.remove(accessToken.getToken());
    }

    private void resetComponent(Component component) {
        if (component == null) {
            return;
        }
        component.setFree();
        component.setCargo(null);
    }

}
