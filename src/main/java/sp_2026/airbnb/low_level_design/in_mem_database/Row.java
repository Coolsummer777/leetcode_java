package sp_2026.airbnb.low_level_design.in_mem_database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class Row {

    private final Map<String, Object> values;

    Row(Map<String, Object> values) {
        this.values = new HashMap<>(values);
    }

    Object get(String column) {
        return values.get(column);
    }

    Map<String, Object> project(Map<String, ColumnType> schema, java.util.List<String> columns) {
        if (columns.size() == 1 && "*".equals(columns.get(0))) {
            return Collections.unmodifiableMap(new HashMap<>(values));
        }

        Map<String, Object> projected = new HashMap<>();
        for (String column : columns) {
            if (!schema.containsKey(column)) {
                throw new IllegalArgumentException("Unknown column: " + column);
            }
            projected.put(column, values.get(column));
        }
        return projected;
    }
}
