package sp_2026.airbnb.low_level_design.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Banking — Deposit / Withdraw / Transaction / Balance (TreeMap variant)
 *
 * Same requirements as {@link Bank}. {@link TreeMap} key = timestamp; multiple operations at
 * the same timestamp live in a list (insertion order). Out-of-order insert uses suffix +/- on
 * cumBalance; read paths use floorKey / subMap.
 */
public class BankV2 {

    private static final String DEPOSIT = "deposit";
    private static final String WITHDRAW = "withdraw";

    private final TreeMap<Integer, List<Entry>> entries;

    public BankV2() {
        entries = new TreeMap<>();
    }

    public void deposit(int timestamp, int amount) {
        int balanceBefore = balanceBefore(timestamp);
        List<Entry> bucket = entries.computeIfAbsent(timestamp, k -> new ArrayList<>());
        bucket.add(new Entry(amount, DEPOSIT, balanceBefore + amount));
        adjustSuffixAfter(timestamp, amount);
    }

    public void withdraw(int timestamp, int amount) {
        int balanceBefore = balanceBefore(timestamp);
        int newBalance = balanceBefore - amount;
        if (newBalance < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        for (Entry entry : suffixAfter(timestamp)) {
            if (entry.cumBalance - amount < 0) {
                throw new RuntimeException("Insufficient balance");
            }
        }

        List<Entry> bucket = entries.computeIfAbsent(timestamp, k -> new ArrayList<>());
        bucket.add(new Entry(amount, WITHDRAW, newBalance));
        adjustSuffixAfter(timestamp, -amount);
    }

    public List<Transaction> transactions(int start, int end) {
        List<Transaction> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Entry>> bucket : entries.subMap(start, true, end, true).entrySet()) {
            int timestamp = bucket.getKey();
            for (Entry entry : bucket.getValue()) {
                result.add(new Transaction(timestamp, entry.amount, entry.operation));
            }
        }
        return result;
    }

    public int balance(int timestamp) {
        Integer floorTs = entries.floorKey(timestamp);
        if (floorTs == null) {
            return 0;
        }
        List<Entry> bucket = entries.get(floorTs);
        return bucket.get(bucket.size() - 1).cumBalance;
    }

    /** Balance immediately before appending another entry at this timestamp. */
    private int balanceBefore(int timestamp) {
        List<Entry> bucket = entries.get(timestamp);
        if (bucket != null && !bucket.isEmpty()) {
            return bucket.get(bucket.size() - 1).cumBalance;
        }
        Map.Entry<Integer, List<Entry>> previous = entries.lowerEntry(timestamp);
        if (previous == null) {
            return 0;
        }
        List<Entry> previousBucket = previous.getValue();
        return previousBucket.get(previousBucket.size() - 1).cumBalance;
    }

    /** All entries strictly after the last entry at timestamp (global timeline order). */
    private List<Entry> suffixAfter(int timestamp) {
        List<Entry> suffix = new ArrayList<>();
        for (List<Entry> bucket : entries.tailMap(timestamp, false).values()) {
            suffix.addAll(bucket);
        }
        return suffix;
    }

    private void adjustSuffixAfter(int timestamp, int delta) {
        for (List<Entry> bucket : entries.tailMap(timestamp, false).values()) {
            for (Entry entry : bucket) {
                entry.cumBalance += delta;
            }
        }
    }

    private static final class Entry {
        final int amount;
        final String operation;
        int cumBalance;

        Entry(int amount, String operation, int cumBalance) {
            this.amount = amount;
            this.operation = operation;
            this.cumBalance = cumBalance;
        }
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
