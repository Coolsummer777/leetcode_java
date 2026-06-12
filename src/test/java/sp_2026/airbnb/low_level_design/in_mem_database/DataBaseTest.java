package sp_2026.airbnb.low_level_design.in_mem_database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataBaseTest {

    private DataBase db;

    @BeforeEach
    void setUp() {
        db = new DataBase();
        db.createTable("users", List.of("id:int", "name:str", "city:str", "age:int"));
    }

    @Test
    void insertAndSelectAllColumns() {
        db.insert("users", Map.of("id", 1, "name", "Alice", "city", "SF", "age", 25));
        db.insert("users", Map.of("id", 2, "name", "Bob", "city", "NY", "age", 30));

        List<Map<String, Object>> rows = db.select("users", List.of("*"), null);

        assertEquals(2, rows.size());
        assertEquals("Alice", rows.get(0).get("name"));
        assertEquals("Bob", rows.get(1).get("name"));
    }

    @Test
    void selectWithEqualityFilterUsesIndex() {
        db.insert("users", Map.of("id", 1, "name", "Alice", "city", "SF", "age", 25));
        db.insert("users", Map.of("id", 2, "name", "Bob", "city", "NY", "age", 30));
        db.insert("users", Map.of("id", 3, "name", "Carol", "city", "SF", "age", 19));

        List<Map<String, Object>> rows = db.select(
                "users", List.of("name"), Predicates.eq("city", "SF"));

        assertEquals(2, rows.size());
        assertEquals("Alice", rows.get(0).get("name"));
        assertEquals("Carol", rows.get(1).get("name"));
    }

    @Test
    void selectWithRangeAndAndFilter() {
        db.insert("users", Map.of("id", 1, "name", "Alice", "city", "SF", "age", 25));
        db.insert("users", Map.of("id", 2, "name", "Bob", "city", "NY", "age", 30));
        db.insert("users", Map.of("id", 3, "name", "Carol", "city", "SF", "age", 19));

        Predicate where = Predicates.and(Predicates.eq("city", "SF"), Predicates.gt("age", 20));
        List<Map<String, Object>> rows = db.select("users", List.of("name"), where);

        assertEquals(1, rows.size());
        assertEquals("Alice", rows.get(0).get("name"));
    }

    @Test
    void selectWithOrFilter() {
        db.insert("users", Map.of("id", 1, "name", "Alice", "city", "SF", "age", 25));
        db.insert("users", Map.of("id", 2, "name", "Bob", "city", "NY", "age", 30));

        Predicate where = Predicates.or(Predicates.eq("city", "SF"), Predicates.lt("age", 20));
        List<Map<String, Object>> rows = db.select("users", List.of("name"), where);

        assertEquals(1, rows.size());
        assertEquals("Alice", rows.get(0).get("name"));
    }

    @Test
    void rejectsDuplicateTable() {
        assertThrows(IllegalArgumentException.class, () ->
                db.createTable("users", List.of("id:int")));
    }

    @Test
    void rejectsMissingColumnOnInsert() {
        assertThrows(IllegalArgumentException.class, () ->
                db.insert("users", Map.of("id", 1, "name", "Alice")));
    }

    @Test
    void rejectsWrongTypeOnInsert() {
        assertThrows(IllegalArgumentException.class, () ->
                db.insert("users", Map.of("id", "bad", "name", "Alice", "city", "SF", "age", 25)));
    }
}
