package sp_2026.airbnb.coding;


import java.util.*;
/**
 * 
Refund Waterfall Across Payment Methods
Given an ordered history of payments (each with method, date, and amount) and an optional history of prior refunds against those payments, produce the refund allocation for a new refund amount. Refunds are issued in full per payment before moving to the next, prioritized by payment method (CREDIT > CREDIT_CARD > PAYPAL) and then by recency (most recent first).

Requirements
Input:
payments: list of (payment_id, method, date, amount).
prior_refunds: list of (refund_id, payment_id, amount) — amounts already refunded against specific payments.
refund_amount: integer amount to allocate now.
Output: list of refund allocations (refund_label, payment_id, method, amount).
Allocation rules:

A single refund against a payment is issued in full (up to the remaining balance) before considering the next payment.
Payments are prioritized by method order: CREDIT > CREDIT_CARD > PAYPAL.
Within the same method, prefer the more recent payment.
The refund stops once refund_amount is fully allocated.
Example:

payments = [
  (1, CREDIT,      2023-01-15, 40),
  (2, PAYPAL,      2023-01-10, 60),
  (3, PAYPAL,      2023-01-20, 40),
]
prior_refunds = [(R1, payment_id=1, 20)]
refund_amount = 50

=> [
  (a, payment_id=1, CREDIT, 20),  # remaining after prior R1 is 40-20=20
  (b, payment_id=3, PAYPAL, 30),  # most-recent paypal first
]
Notes
Build a per-payment "remaining" map by subtracting prior refunds before allocating.
Sort the payments by (method_priority, -date) once; then walk the list draining refund_amount into each payment's remaining balance.
Watch the rule wording: "refund in full for a payment before considering the next" means you do not split a single payment across two outputs unless the refund amount is smaller than the remaining balance (in which case the partial fills it).
Edge cases: refund amount > total remaining (clarify whether to short-fill or raise), zero-amount payments, prior refund that fully drained a payment (skip it), tie-breakers on date (clarify stable vs unstable order).
The output labels (a, b, c) are sequential per call; interviewers care that the order in the output matches the allocation order, not insertion order of payments.
Preparation
Implement the allocator in under 20 minutes; cover with 4 tests: (1) single payment fully covers, (2) waterfall across two methods, (3) prior-refund reduces remaining, (4) refund exceeds total balance.
Practice articulating the priority tuple before coding — the interviewer often pushes back on the ordering rule mid-stream.
Pair-prep with banking-transactions-class — both surface in the same family of OOD-flavored coding rounds.

 */
public class Fefund {
    
    public List<RefundRecord> refund(List<Payment> payments, List<RefundRecord> refundRecords, int refundAmount) {
        List<RefundRecord> res = new ArrayList<>();

        Map<String,List<Payment>> paymentMap = new HashMap<>();
        Map<Integer, Payment> paymentIdMap = new HashMap<>();
        for (Payment payment : payments) {
            List<Payment> list = paymentMap.computeIfAbsent(payment.method, k -> new ArrayList<>());
            list.add(payment);
            paymentIdMap.put(payment.paymentId, payment);
        }

        for (RefundRecord record : refundRecords){
            Payment payment = paymentIdMap.getOrDefault(record.paymentId,null);
            if (payment != null) {
                payment.amount -= record.amount;
            }
        }

        int counter = 0;

        String[] methodPriority = new String[]{"CREDIT", "CREDIT_CARD", "PAYPAL"};
        for (String method : methodPriority) {
            List<Payment> list = paymentMap.getOrDefault(method, new ArrayList<>());
            list.sort((a, b) -> b.date.compareTo(a.date));
            for (Payment payment : list) {
                if (payment.amount <= 0) {
                    continue;
                }

                int canRefund = Math.min(refundAmount, payment.amount);
                if (canRefund > 0) {
                    res.add(new RefundRecord(String.valueOf((char)('a' + counter)), 
                    payment.paymentId, canRefund, payment.method));
                    counter++;
                    refundAmount -= canRefund;
                }

                if(refundAmount <= 0) {
                    break;
                }
            }

            if(refundAmount <= 0) {
                break;
            }
        }


        return res;
    }


    class Payment {
        int paymentId;
        String method;
        String date;
        int amount;
    }

    class RefundRecord {
        String refundId;
        int paymentId;
        int amount;
        String method;

        public RefundRecord(String refundId, int paymentId, int amount, String method) {
            this.refundId = refundId;
            this.paymentId = paymentId;
            this.amount = amount;
            this.method = method;
        }
    }

}
