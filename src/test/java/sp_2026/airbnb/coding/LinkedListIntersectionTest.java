package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sp_2026.models.ListNode;

class LinkedListIntersectionTest {

    private final LinkedListIntersection solution = new LinkedListIntersection();

    @Test
    void hasIntersection_bothAcyclic_intersectAtTail() {
        ListNode shared = node(8, node(9));
        ListNode headA = node(1, node(2, shared));
        ListNode headB = node(3, node(4, node(5, shared)));

        assertTrue(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothAcyclic_differentLengths_intersect() {
        ListNode shared = node(3, node(4));
        ListNode headA = node(1, node(2, shared));
        ListNode headB = node(5, node(6, node(7, shared)));

        assertTrue(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothAcyclic_noIntersection() {
        ListNode headA = node(1, node(2));
        ListNode headB = node(3, node(4));

        assertFalse(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothAcyclic_sameHeadReference() {
        ListNode head = node(1, node(2));

        assertTrue(solution.hasIntersection(head, head));
    }

    @Test
    void hasIntersection_oneCyclicOneAcyclic_noIntersection() {
        ListNode cycle = node(3, null);
        cycle.next = node(4, cycle);
        ListNode headA = node(1, node(2, cycle));
        ListNode headB = node(5, node(6));

        assertFalse(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothCyclic_sameCycle_differentEntry() {
        ListNode c1 = node(10);
        ListNode c2 = node(11);
        c1.next = c2;
        c2.next = c1;
        ListNode headA = node(1, node(2, c1));
        ListNode headB = node(3, node(4, c2));

        assertTrue(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothCyclic_sharedPrefixBeforeCycle() {
        ListNode c1 = node(5);
        ListNode c2 = node(6);
        c1.next = c2;
        c2.next = c1;
        ListNode shared = node(3, c1);
        ListNode headA = node(1, node(2, shared));
        ListNode headB = node(7, shared);

        assertTrue(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothCyclic_disjointCycles() {
        ListNode aCycle = node(2);
        aCycle.next = node(3, aCycle);
        ListNode headA = node(1, aCycle);

        ListNode bCycle = node(5);
        bCycle.next = node(6, bCycle);
        ListNode headB = node(4, bCycle);

        assertFalse(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothCyclic_singleNodeSelfLoop_shared() {
        ListNode cycle = node(2);
        cycle.next = cycle;
        ListNode headA = node(1, cycle);
        ListNode headB = node(3, cycle);

        assertTrue(solution.hasIntersection(headA, headB));
    }

    @Test
    void hasIntersection_bothCyclic_singleNodeSelfLoop_disjoint() {
        ListNode cycleA = node(1);
        cycleA.next = cycleA;
        ListNode cycleB = node(2);
        cycleB.next = cycleB;

        assertFalse(solution.hasIntersection(cycleA, cycleB));
    }

    @Test
    void hasIntersection_nullHead_returnsFalse() {
        ListNode head = node(1);

        assertFalse(solution.hasIntersection(null, head));
        assertFalse(solution.hasIntersection(head, null));
        assertFalse(solution.hasIntersection(null, null));
    }

    @Test
    void hasCircle_detectsCycle() {
        ListNode cycle = node(3);
        cycle.next = node(4, cycle);
        ListNode head = node(1, node(2, cycle));

        assertTrue(solution.hasCircle(head));
    }

    @Test
    void hasCircle_acyclicList_returnsFalse() {
        ListNode head = node(1, node(2, node(3)));

        assertFalse(solution.hasCircle(head));
    }

    @Test
    void hasCircle_singleNode_returnsFalse() {
        assertFalse(solution.hasCircle(node(1)));
    }

    @Test
    void getCircleNode_returnsMeetingPointOnCycle() {
        ListNode c1 = node(3);
        ListNode c2 = node(4);
        c1.next = c2;
        c2.next = c1;
        ListNode head = node(1, node(2, c1));

        ListNode meet = solution.getCircleNode(head);

        assertTrue(meet == c1 || meet == c2);
    }

    @Test
    void getCircleNode_acyclicList_returnsNull() {
        ListNode head = node(1, node(2));

        assertNull(solution.getCircleNode(head));
    }

    @Test
    void getLength_countsNodesUntilNull() {
        ListNode head = node(1, node(2, node(3)));

        assertEquals(3, solution.getLength(head));
    }

    private static ListNode node(int val) {
        return new ListNode(val);
    }

    private static ListNode node(int val, ListNode next) {
        return new ListNode(val, next);
    }
}
