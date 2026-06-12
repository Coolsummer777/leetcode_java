package sp_2026.airbnb.low_level_design.in_mem_database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Table {

    private final String name;
    private final Map<String, ColumnType> schema;
    private final List<Row> rows = new ArrayList<>();
    private final Map<String, Map<Object, Set<Integer>>> equalityIndexes = new HashMap<>();

    Table(String name, Map<String, ColumnType> schema) {
        this.name = name;
        this.schema = new HashMap<>(schema);
        for (String column : schema.keySet()) {
            equalityIndexes.put(column, new HashMap<>());
        }
    }

    String name() {
        return name;
    }

    Map<String, ColumnType> schema() {
        return schema;
    }

    int insert(Map<String, Object> rowValues) {
        validateRow(rowValues);

        Row row = new Row(rowValues);
        int rowId = rows.size();
        rows.add(row);
        indexRow(rowId, row);
        return rowId;
    }

    List<Row> allRows() {
        return rows;
    }

    List<Row> rowsByEquality(String column, Object value) {
        Map<Object, Set<Integer>> columnIndex = equalityIndexes.get(column);
        if (columnIndex == null) {
            throw new IllegalArgumentException("Unknown column: " + column);
        }
        Set<Integer> rowIds = columnIndex.get(value);
        if (rowIds == null || rowIds.isEmpty()) {
            return List.of();
        }

        List<Row> matched = new ArrayList<>();
        for (Integer rowId : rowIds) {
            matched.add(rows.get(rowId));
        }
        return matched;
    }

    private void validateRow(Map<String, Object> rowValues) {
        for (String column : schema.keySet()) {
            if (!rowValues.containsKey(column)) {
                throw new IllegalArgumentException("Missing column: " + column);
            }
            schema.get(column).validate(rowValues.get(column), column);
        }

        for (String column : rowValues.keySet()) {
            if (!schema.containsKey(column)) {
                throw new IllegalArgumentException("Unknown column: " + column);
            }
        }
    }

    private void indexRow(int rowId, Row row) {
        for (String column : schema.keySet()) {
            Object value = row.get(column);
            equalityIndexes.get(column)
                    .computeIfAbsent(value, ignored -> new HashSet<>())
                    .add(rowId);
        }
    }
}
