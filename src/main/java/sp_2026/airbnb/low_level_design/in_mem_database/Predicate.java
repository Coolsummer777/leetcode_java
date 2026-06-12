package sp_2026.airbnb.low_level_design.in_mem_database;

interface Predicate {
    boolean evaluate(Row row);
}
