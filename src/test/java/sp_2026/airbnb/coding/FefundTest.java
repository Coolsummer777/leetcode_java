package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests derived from the Refund Waterfall problem statement:
 * allocate refund_amount across payments by method priority
 * (CREDIT &gt; CREDIT_CARD &gt; PAYPAL), then recency within the same method,
 * after subtracting prior_refunds from each payment's remaining balance.
 */
class FefundTest {

    private Fefund fefund;

    @BeforeEach
    void setUp() {
        fefund = new Fefund();
    }

    private Fefund.Payment payment(int paymentId, String method, String date, int amount) {
        Fefund.Payment p = fefund.new Payment();
        p.paymentId = paymentId;
        p.method = method;
        p.date = date;
        p.amount = amount;
        return p;
    }

    private Fefund.RefundRecord priorRefund(String refundId, int paymentId, int amount) {
        return fefund.new RefundRecord(refundId, paymentId, amount, null);
    }

    private Fefund.RefundRecord allocation(String label, int paymentId, String method, int amount) {
        return fefund.new RefundRecord(label, paymentId, amount, method);
    }

    private List<Fefund.RefundRecord> expected(Fefund.RefundRecord... rows) {
        return List.of(rows);
    }

    private void assertRefundAllocations(
            List<Fefund.RefundRecord> expected, List<Fefund.RefundRecord> actual) {
        assertEquals(expected.size(), actual.size(),
                () -> "expected " + formatAllocations(expected) + " but got " + formatAllocations(actual));
        for (int i = 0; i < expected.size(); i++) {
            Fefund.RefundRecord exp = expected.get(i);
            Fefund.RefundRecord act = actual.get(i);
            assertEquals(exp.refundId, act.refundId, "label at index " + i);
            assertEquals(exp.paymentId, act.paymentId, "paymentId at index " + i);
            assertEquals(exp.method, act.method, "method at index " + i);
            assertEquals(exp.amount, act.amount, "amount at index " + i);
        }
    }

    private static String formatAllocations(List<Fefund.RefundRecord> allocations) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < allocations.size(); i++) {
            Fefund.RefundRecord r = allocations.get(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('(').append(r.refundId).append(", payment=").append(r.paymentId)
                    .append(", method=").append(r.method)
                    .append(", amount=").append(r.amount).append(')');
        }
        return sb.append(']').toString();
    }

    @Test
    void problemExample_priorRefundAndWaterfall() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-15", 40),
                payment(2, "PAYPAL", "2023-01-10", 60),
                payment(3, "PAYPAL", "2023-01-20", 40));
        List<Fefund.RefundRecord> priorRefunds = List.of(priorRefund("R1", 1, 20));

        assertRefundAllocations(
                expected(
                        allocation("a", 1, "CREDIT", 20),
                        allocation("b", 3, "PAYPAL", 30)),
                fefund.refund(payments, priorRefunds, 50));
    }

    @Test
    void singlePayment_fullyCoversRefund() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-06-01", 100));

        assertRefundAllocations(
                expected(allocation("a", 1, "CREDIT", 50)),
                fefund.refund(payments, List.of(), 50));
    }

    @Test
    void waterfall_acrossTwoMethods() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "PAYPAL", "2023-01-01", 30),
                payment(2, "CREDIT", "2023-01-02", 40));

        assertRefundAllocations(
                expected(
                        allocation("a", 2, "CREDIT", 40),
                        allocation("b", 1, "PAYPAL", 10)),
                fefund.refund(payments, List.of(), 50));
    }

    @Test
    void priorRefund_reducesRemainingBeforeAllocation() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 80));
        List<Fefund.RefundRecord> priorRefunds = List.of(priorRefund("R1", 1, 30));

        assertRefundAllocations(
                expected(allocation("a", 1, "CREDIT", 50)),
                fefund.refund(payments, priorRefunds, 50));
    }

    @Test
    void refundExceedsTotalRemaining_shortFillsWithoutError() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 20),
                payment(2, "PAYPAL", "2023-01-02", 15));

        assertRefundAllocations(
                expected(
                        allocation("a", 1, "CREDIT", 20),
                        allocation("b", 2, "PAYPAL", 15)),
                fefund.refund(payments, List.of(), 100));
    }

    @Test
    void priorRefund_fullyDrainedPaymentIsSkipped() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 40),
                payment(2, "PAYPAL", "2023-01-02", 30));
        List<Fefund.RefundRecord> priorRefunds = List.of(priorRefund("R1", 1, 40));

        assertRefundAllocations(
                expected(allocation("a", 2, "PAYPAL", 20)),
                fefund.refund(payments, priorRefunds, 20));
    }

    @Test
    void methodPriority_creditBeforeCreditCardBeforePaypal() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "PAYPAL", "2023-01-01", 10),
                payment(2, "CREDIT_CARD", "2023-01-02", 10),
                payment(3, "CREDIT", "2023-01-03", 10));

        assertRefundAllocations(
                expected(
                        allocation("a", 3, "CREDIT", 10),
                        allocation("b", 2, "CREDIT_CARD", 10),
                        allocation("c", 1, "PAYPAL", 5)),
                fefund.refund(payments, List.of(), 25));
    }

    @Test
    void sameMethod_prefersMoreRecentPayment() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "PAYPAL", "2023-01-01", 50),
                payment(2, "PAYPAL", "2023-01-15", 30));

        assertRefundAllocations(
                expected(
                        allocation("a", 2, "PAYPAL", 30),
                        allocation("b", 1, "PAYPAL", 10)),
                fefund.refund(payments, List.of(), 40));
    }

    @Test
    void zeroRefundAmount_returnsEmpty() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 100));

        assertRefundAllocations(
                expected(),
                fefund.refund(payments, List.of(), 0));
    }

    @Test
    void zeroRemainingPayment_isSkipped() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 0),
                payment(2, "PAYPAL", "2023-01-02", 25));

        assertRefundAllocations(
                expected(allocation("a", 2, "PAYPAL", 25)),
                fefund.refund(payments, List.of(), 25));
    }

    @Test
    void emptyPayments_returnsEmpty() {
        assertRefundAllocations(
                expected(),
                fefund.refund(List.of(), List.of(), 50));
    }

    @Test
    void multiplePriorRefunds_onSamePayment() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 100));
        List<Fefund.RefundRecord> priorRefunds = List.of(
                priorRefund("R1", 1, 30),
                priorRefund("R2", 1, 20));

        assertRefundAllocations(
                expected(allocation("a", 1, "CREDIT", 50)),
                fefund.refund(payments, priorRefunds, 50));
    }

    @Test
    void partialLastAllocation_whenRefundSmallerThanRemaining() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 100));

        assertRefundAllocations(
                expected(allocation("a", 1, "CREDIT", 15)),
                fefund.refund(payments, List.of(), 15));
    }

    @Test
    void outputLabels_areSequentialInAllocationOrder() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 10),
                payment(2, "CREDIT_CARD", "2023-01-02", 10),
                payment(3, "PAYPAL", "2023-01-03", 10));

        List<Fefund.RefundRecord> result = fefund.refund(payments, List.of(), 25);

        assertEquals(List.of("a", "b", "c"), result.stream().map(r -> r.refundId).toList());
        assertEquals(List.of("CREDIT", "CREDIT_CARD", "PAYPAL"),
                result.stream().map(r -> r.method).toList());
    }

    @Test
    void priorRefund_forUnknownPaymentId_isIgnored() {
        List<Fefund.Payment> payments = List.of(
                payment(1, "CREDIT", "2023-01-01", 40));
        List<Fefund.RefundRecord> priorRefunds = List.of(priorRefund("R1", 999, 20));

        assertRefundAllocations(
                expected(allocation("a", 1, "CREDIT", 40)),
                fefund.refund(payments, priorRefunds, 40));
    }

    @Test
    void noPriorRefunds_emptyListWorks() {
        List<Fefund.Payment> payments = new ArrayList<>(List.of(
                payment(1, "PAYPAL", "2023-02-01", 35)));

        assertRefundAllocations(
                expected(allocation("a", 1, "PAYPAL", 35)),
                fefund.refund(payments, new ArrayList<>(), 35));
    }
}
