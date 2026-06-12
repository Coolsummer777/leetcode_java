package sp_2026.airbnb.low_level_design.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Banking — Deposit / Withdraw / Transaction / Balance
 * Implement a bank account class supporting `deposit(timestamp, amount)`, `withdraw(timestamp, amount)`,
 * `transaction_history(start, end)`, and `balance(timestamp)`. Time-range queries should run faster
 * than linear scan when histories grow large.
 *
 * Requirements
 * Operations:
 * deposit(t, amt) / withdraw(t, amt) — append a signed-amount entry at timestamp t.
 * transactions(start, end) — return all entries within [start, end].
 * balance(t) — running balance as of timestamp t.
 * Timestamps are non-decreasing in the simplest variant; relax for the follow-up.
 * Withdraw must not overdraft (clarify whether to throw or to short-fill).
 *
 * Notes
 * Append-only entries list keyed by (timestamp, signed_amount); maintain a parallel cum_balance[] so
 * balance(t) is one binary-search + lookup (O(log n)).
 * transactions(start, end) is two binary searches plus a slice (O(log n + k)).
 * Watch the boundary semantics: inclusive vs exclusive end; tie-breaking when multiple entries share a timestamp.
 * Overdraft policy is the most common clarifying question; pre-script your answer
 * ("throw a OverdraftError, return current balance unchanged").
 *
 * Follow-up variants:
 * Out-of-order timestamps → switch to a sorted structure (SortedList / balanced BST / segment tree on
 * timestamps) with O(log n) insert + prefix-sum maintenance.
 * Multi-account transfers → wrap in a Bank that owns a dict of accounts and enforces atomic two-sided posting.
 *
 * Preparation
 * Implement the basic class in 15 minutes with 4 tests (deposit + withdraw + balance + range query).
 * Layer the binary-search optimization for balance(t) on a second pass.
 * Drill the out-of-order follow-up using SortedList from sortedcontainers (or roll your own).
 * Be ready to talk through the multi-account transfer follow-up at the design level (no need to fully implement under time).
 *
 * Implementation (current):
 * - Out-of-order insert at upperBoundInclusive(t) + 1; same timestamp keeps insertion order.
 * - Withdraw: validate new balance and suffix (each later cumBalance - amount >= 0), then insert and shift suffix.
 * - Deposit: insert and add amount to suffix cumBalances.
 * - balance / transactions: O(log n); insert: O(n).
 *
 */
public class Bank {

    private static final String DEPOSIT = "deposit";
    private static final String WITHDRAW = "withdraw";

    private final List<Transaction> transactions;
    private final List<Integer> cumBalance;

    public Bank() {
        transactions = new ArrayList<>();
        cumBalance = new ArrayList<>();
    }

    public void deposit(int timestamp, int amount) {
        int index = insertIndex(timestamp);
        int newBalance = balanceBefore(index) + amount;

        transactions.add(index, new Transaction(timestamp, amount, DEPOSIT));
        cumBalance.add(index, newBalance);
        adjustSuffix(index + 1, amount);
    }

    public void withdraw(int timestamp, int amount) {
        int index = insertIndex(timestamp);
        int newBalance = balanceBefore(index) - amount;
        if (newBalance < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        for (int i = index; i < cumBalance.size(); i++) {
            if (cumBalance.get(i) - amount < 0) {
                throw new RuntimeException("Insufficient balance");
            }
        }

        transactions.add(index, new Transaction(timestamp, amount, WITHDRAW));
        cumBalance.add(index, newBalance);
        adjustSuffix(index + 1, -amount);
    }

    public List<Transaction> transactions(int start, int end) {
        int left = lowerBound(start);
        int right = upperBoundInclusive(end);
        if (left >= transactions.size() || right < 0 || left > right) {
            return Collections.emptyList();
        }
        return new ArrayList<>(transactions.subList(left, right + 1));
    }

    public int balance(int timestamp) {
        int idx = upperBoundInclusive(timestamp);
        return idx < 0 ? 0 : cumBalance.get(idx);
    }

    private int insertIndex(int timestamp) {
        return upperBoundInclusive(timestamp) + 1;
    }

    private int balanceBefore(int index) {
        return index == 0 ? 0 : cumBalance.get(index - 1);
    }

    private void adjustSuffix(int start, int delta) {
        for (int i = start; i < cumBalance.size(); i++) {
            cumBalance.set(i, cumBalance.get(i) + delta);
        }
    }

    // First index with transactions[i].timestamp >= ts; returns size if none.
    private int lowerBound(int ts) {
        int lo = 0;
        int hi = transactions.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (transactions.get(mid).timestamp < ts) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    // Last index with transactions[i].timestamp <= ts; returns -1 if none.
    private int upperBoundInclusive(int ts) {
        int lo = 0;
        int hi = transactions.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (transactions.get(mid).timestamp <= ts) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo - 1;
    }

    static class Transaction {
        final int timestamp;
        final int amount;
        final String operation;

        Transaction(int timestamp, int amount, String operation) {
            this.timestamp = timestamp;
            this.amount = amount;
            this.operation = operation;
        }
    }
}
