package sp_2026.airbnb.low_level_design.in_mem_database;

final class Predicates {

    private Predicates() {
    }

    static Predicate eq(String column, Object value) {
        return new Eq(column, value);
    }

    static Predicate gt(String column, int value) {
        return new Gt(column, value);
    }

    static Predicate lt(String column, int value) {
        return new Lt(column, value);
    }

    static Predicate and(Predicate left, Predicate right) {
        return new And(left, right);
    }

    static Predicate or(Predicate left, Predicate right) {
        return new Or(left, right);
    }

    static EqualityClause extractEquality(Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        if (predicate instanceof Eq) {
            Eq eq = (Eq) predicate;
            return new EqualityClause(eq.column, eq.value);
        }
        if (predicate instanceof And) {
            And and = (And) predicate;
            EqualityClause left = extractEquality(and.left);
            EqualityClause right = extractEquality(and.right);
            if (left != null && right != null) {
                return left;
            }
            return left != null ? left : right;
        }
        return null;
    }

    static final class EqualityClause {
        final String column;
        final Object value;

        EqualityClause(String column, Object value) {
            this.column = column;
            this.value = value;
        }
    }

    private static final class Eq implements Predicate {
        private final String column;
        private final Object value;

        Eq(String column, Object value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public boolean evaluate(Row row) {
            Object actual = row.get(column);
            return actual != null && actual.equals(value);
        }
    }

    private static final class Gt implements Predicate {
        private final String column;
        private final int value;

        Gt(String column, int value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public boolean evaluate(Row row) {
            Object actual = row.get(column);
            if (!(actual instanceof Integer)) {
                return false;
            }
            return (Integer) actual > value;
        }
    }

    private static final class Lt implements Predicate {
        private final String column;
        private final int value;

        Lt(String column, int value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public boolean evaluate(Row row) {
            Object actual = row.get(column);
            if (!(actual instanceof Integer)) {
                return false;
            }
            return (Integer) actual < value;
        }
    }

    private static final class And implements Predicate {
        private final Predicate left;
        private final Predicate right;

        And(Predicate left, Predicate right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(Row row) {
            return left.evaluate(row) && right.evaluate(row);
        }
    }

    private static final class Or implements Predicate {
        private final Predicate left;
        private final Predicate right;

        Or(Predicate left, Predicate right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(Row row) {
            return left.evaluate(row) || right.evaluate(row);
        }
    }
}
