package sp_2026.airbnb.low_level_design.in_mem_database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Engine {

    private final Map<String, Table> tables = new HashMap<>();

    void createTable(String name, List<String> columnDefs) {
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException("Table already exists: " + name);
        }
        if (columnDefs == null || columnDefs.isEmpty()) {
            throw new IllegalArgumentException("Table must have columns");
        }

        Map<String, ColumnType> schema = new HashMap<>();
        for (String columnDef : columnDefs) {
            String[] parts = columnDef.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid column definition: " + columnDef);
            }
            String column = parts[0].trim();
            if (column.isEmpty()) {
                throw new IllegalArgumentException("Invalid column definition: " + columnDef);
            }
            if (schema.containsKey(column)) {
                throw new IllegalArgumentException("Duplicate column: " + column);
            }
            schema.put(column, ColumnType.fromToken(parts[1].trim()));
        }

        tables.put(name, new Table(name, schema));
    }

    void insert(String tableName, Map<String, Object> row) {
        Table table = requireTable(tableName);
        table.insert(row);
    }

    List<Map<String, Object>> select(String tableName, List<String> columns, Predicate where) {
        Table table = requireTable(tableName);
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Select columns must not be empty");
        }

        List<Row> candidates = candidateRows(table, where);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Row row : candidates) {
            if (where == null || where.evaluate(row)) {
                result.add(row.project(table.schema(), columns));
            }
        }
        return result;
    }

    private List<Row> candidateRows(Table table, Predicate where) {
        Predicates.EqualityClause lookup = Predicates.extractEquality(where);
        if (lookup != null) {
            return table.rowsByEquality(lookup.column, lookup.value);
        }
        return table.allRows();
    }

    private Table requireTable(String tableName) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        return table;
    }

}
