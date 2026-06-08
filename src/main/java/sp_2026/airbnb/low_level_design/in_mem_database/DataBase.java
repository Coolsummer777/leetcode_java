package sp_2026.airbnb.low_level_design.in_mem_database;

/**
 * 
In-Memory SQL Database
Open-ended prompt: implement an in-memory SQL-like database supporting basic table creation, insertion, and SELECT with WHERE filters. Notoriously under-specified; clarification carries most of the signal.

Requirements
The prompt is deliberately open — the interviewer expects the candidate to drive the scope before coding.
Typical clarified MVP:
create_table(name, columns) with a column-type schema.
insert(table, row).
select(table, columns, where=None) with simple equality / range filters.
Stretch goals if time allows: indexes (hash / B-tree), JOIN, GROUP BY, transactions, snapshot isolation.
Notes
The fail mode for this round is spending 20 minutes coding before pinning down scope. Spend the first 5 minutes clarifying: which SQL subset, single-table or multi-table, indexes required, transactional semantics.
A clean class hierarchy carries the day: Table (schema + rows), Row (dict keyed by column), Query (predicate AST), Engine (orchestrator).
For WHERE, an AST of predicates (Eq, Gt, And, Or) is easier to extend than a string parser. Avoid writing a real SQL parser unless explicitly asked.
Indexes: a dict-of-dict[value -> set[row_id]] is enough for the equality case; a sorted structure is needed for range.
Transactions: copy-on-write of the table's row map gives snapshot isolation with negligible code.
Many candidates pick this up after seeing the prompt in the wild and over-prepare a full Postgres clone; the interviewer is grading scoping, not feature completeness.
Preparation
Pre-design a 4-class skeleton (Engine / Table / Row / Predicate) so the first 5 minutes after clarification are pure typing.
Drill the predicate AST: write And(Eq("city", "SF"), Gt("age", 18)) and evaluate it against a row.
Hand-write a single-column hash index and demonstrate O(1) lookup for the equality case.
Pre-script the scope conversation: "What SQL subset? Single table? Do we need indexes? Transactions?" — this is the actual graded skill.
Pair-prep with implement-retryer — both are OOD-flavored prompts where the design conversation dominates.

 */
public class DataBase {

}
