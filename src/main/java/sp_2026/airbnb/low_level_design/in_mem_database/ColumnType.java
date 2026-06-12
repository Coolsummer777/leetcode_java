package sp_2026.airbnb.low_level_design.in_mem_database;

enum ColumnType {
    INT,
    STRING;

    static ColumnType fromToken(String token) {
        switch (token.toLowerCase()) {
            case "int":
                return INT;
            case "str":
            case "string":
                return STRING;
            default:
                throw new IllegalArgumentException("Unknown column type: " + token);
        }
    }

    void validate(Object value, String column) {
        if (value == null) {
            throw new IllegalArgumentException("Null value for column: " + column);
        }
        switch (this) {
            case INT:
                if (!(value instanceof Integer)) {
                    throw new IllegalArgumentException("Column " + column + " expects int");
                }
                break;
            case STRING:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException("Column " + column + " expects string");
                }
                break;
            default:
                break;
        }
    }
}
