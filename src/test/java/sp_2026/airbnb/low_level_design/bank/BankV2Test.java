package sp_2026.airbnb.low_level_design.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankV2Test {

    private BankV2 bank;

    @BeforeEach
    void setUp() {
        bank = new BankV2();
    }

    @Test
    void depositUpdatesBalance() {
        bank.deposit(1, 100);
        bank.deposit(5, 50);
        assertEquals(150, bank.balance(5));
        assertEquals(100, bank.balance(3));
        assertEquals(0, bank.balance(0));
    }

    @Test
    void withdrawRejectsOverdraft() {
        bank.deposit(1, 100);
        assertThrows(RuntimeException.class, () -> bank.withdraw(2, 150));
        assertEquals(100, bank.balance(2));
        bank.withdraw(2, 40);
        assertEquals(60, bank.balance(2));
    }

    @Test
    void sameTimestampOperationsApplyInOrder() {
        bank.deposit(10, 100);
        bank.deposit(10, 50);
        bank.withdraw(10, 30);
        assertEquals(120, bank.balance(10));
    }

    @Test
    void transactionsReturnsInclusiveRange() {
        bank.deposit(10, 100);
        bank.withdraw(20, 30);
        bank.deposit(30, 50);

        assertTrue(bank.transactions(25, 29).isEmpty());
        assertTrue(bank.transactions(35, 40).isEmpty());
        assertEquals(1, bank.transactions(10, 10).size());
        assertEquals(3, bank.transactions(10, 30).size());
        assertEquals(1, bank.transactions(15, 25).size());
    }

    @Test
    void allowsOutOfOrderDeposit() {
        bank.deposit(20, 50);
        bank.deposit(10, 100);

        assertEquals(100, bank.balance(10));
        assertEquals(150, bank.balance(20));
        assertEquals(2, bank.transactions(10, 20).size());
    }

    @Test
    void outOfOrderWithdrawRejectsWhenBalanceWouldGoNegative() {
        bank.deposit(20, 100);
        bank.withdraw(20, 40);

        assertThrows(RuntimeException.class, () -> bank.withdraw(10, 80));
        assertEquals(60, bank.balance(20));
        assertEquals(2, bank.transactions(10, 20).size());
    }

    @Test
    void outOfOrderWithdrawRejectsWhenSuffixWouldOverdraft() {
        bank.deposit(10, 100);
        bank.withdraw(20, 30);

        assertThrows(RuntimeException.class, () -> bank.withdraw(15, 80));
        assertEquals(70, bank.balance(20));
        assertEquals(2, bank.transactions(10, 20).size());
    }

    @Test
    void failedInsertDoesNotCorruptLaterBalances() {
        bank.deposit(10, 100);
        bank.withdraw(25, 10);
        bank.withdraw(30, 10);

        assertThrows(RuntimeException.class, () -> bank.withdraw(20, 85));
        assertEquals(100, bank.balance(10));
        assertEquals(90, bank.balance(25));
        assertEquals(80, bank.balance(30));
        assertEquals(3, bank.transactions(10, 30).size());
    }

    @Test
    void outOfOrderWithdrawSucceedsWhenTimelineRemainsValid() {
        bank.deposit(10, 100);
        bank.withdraw(20, 30);

        bank.withdraw(15, 20);
        assertEquals(50, bank.balance(20));
    }
}
