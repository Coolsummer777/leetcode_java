package sp_2026.airbnb.low_level_design.bank;


/**
 *

Banking — Deposit / Withdraw / Transaction / Balance
Implement a bank account class supporting `deposit(timestamp, amount)`, `withdraw(timestamp, amount)`, `transaction_history(start, end)`, and `balance(timestamp)`. Time-range queries should run faster than linear scan when histories grow large.

Requirements
Operations:
deposit(t, amt) / withdraw(t, amt) — append a signed-amount entry at timestamp t.
transactions(start, end) — return all entries within [start, end].
balance(t) — running balance as of timestamp t.
Timestamps are non-decreasing in the simplest variant; relax for the follow-up.
Withdraw must not overdraft (clarify whether to throw or to short-fill).
Notes
Append-only entries list keyed by (timestamp, signed_amount); maintain a parallel cum_balance[] so balance(t) is one binary-search + lookup (O(log n)).
transactions(start, end) is two binary searches plus a slice (O(log n + k)).
Watch the boundary semantics: inclusive vs exclusive end; tie-breaking when multiple entries share a timestamp.
Overdraft policy is the most common clarifying question; pre-script your answer ("throw a OverdraftError, return current balance unchanged").
Follow-up variants:
Out-of-order timestamps → switch to a sorted structure (SortedList / balanced BST / segment tree on timestamps) with O(log n) insert + prefix-sum maintenance.
Multi-account transfers → wrap in a Bank that owns a dict of accounts and enforces atomic two-sided posting.
Preparation
Implement the basic class in 15 minutes with 4 tests (deposit + withdraw + balance + range query).
Layer the binary-search optimization for balance(t) on a second pass.
Drill the out-of-order follow-up using SortedList from sortedcontainers (or roll your own).
Be ready to talk through the multi-account transfer follow-up at the design level (no need to fully implement under time).



 */

public class Bank {

}
