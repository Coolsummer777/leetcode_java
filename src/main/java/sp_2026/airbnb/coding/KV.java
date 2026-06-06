package sp_2026.airbnb.coding;

import java.util.List;

public interface KV {
    void setValue(String key,int value);
    boolean setRef(String key,List<String> ref);
    Integer getValue(String key);
}
