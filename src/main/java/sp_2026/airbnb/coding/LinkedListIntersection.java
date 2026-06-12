package sp_2026.airbnb.coding;

import sp_2026.models.ListNode;


/**
 * 

Linked-List Intersection With Cycles
Given two singly-linked lists that may each contain a cycle, determine whether they share any node. The intersection (if any) can be a shared chain or a shared cycle.

Requirements
Input: two ListNode heads, each list may or may not contain a cycle.
Output: boolean — True if the two lists share at least one node, False otherwise.
Optional: return the first shared node when the answer is True.
Notes
The cycle-free case is the canonical "two-pointer length-difference" trick: walk to each tail measuring length, then advance the longer head by the difference, then walk in lockstep; the first equal pointer is the intersection.
With cycles, first detect each list's cycle with Floyd's algorithm. Cases:
Neither cycles → linear intersection check (above).
Exactly one cycles → no intersection (a cycle cannot end, and the other list terminates at null).
Both cycle → check whether the two cycles are the same cycle: pick any node A on list 1's cycle; walk list 2's cycle for at most one loop; if A is encountered, they share the cycle; otherwise they do not. If they share the cycle, the first intersection is at the join point (or earlier if a tail merges in).
Hashmap fallback (O(n + m) space): traverse list 1 inserting every node id into a set; traverse list 2 checking membership. Acceptable for the warmup, but the interviewer will push for O(1) space.
Edge cases: one or both lists empty, single-node list, lists that share head, two lists that are the same cycle but start at different "entry tails".
Preparation
Implement Floyd's cycle detection cold; identify both the meeting point and the cycle entry.
Write the cycle-free intersection in under 10 minutes; layer the cycle-handling on top.
Hand-trace a Y-shaped example (two tails merging into a shared chain) and a pq-shaped example (one tail merging into the middle of a cycle).
Pre-write a one-sentence answer for "what if both lists could be infinite generators rather than fixed linked lists?" (the answer flips to streaming + bloom-filter dedup).

 */

public class LinkedListIntersection {
    public boolean hasIntersection(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return false;

        // a not circle, b circle
        if (hasCircle(headA) && !hasCircle(headB)) return false;
        // a circle, b not circle
        if (!hasCircle(headA) && hasCircle(headB)) return false;
        // a circle, b circle
        if (hasCircle(headA) && hasCircle(headB)) {
            ListNode circleNodeA = getCircleNode(headA);
            ListNode circleNodeB = getCircleNode(headB);
            if (circleNodeA == circleNodeB) return true;
            ListNode tmp = circleNodeA.next;
            while (tmp != circleNodeA){
                if (tmp == circleNodeB) return true;
                tmp = tmp.next;
            }
            return false;
        }
        // a not circle, b not circle
        int lengthA = getLength(headA);
        int lengthB = getLength(headB);
        if (lengthA > lengthB) {
            for (int i=0;i<lengthA - lengthB;i++){
                headA = headA.next;
            }
        }else {
            for (int i=0;i<lengthB - lengthA;i++){
                headB = headB.next;
            }
        }
        
        while (headA != null && headB != null){
            if (headA == headB) return true;
            headA = headA.next;
            headB = headB.next;
        }
        return false;
    }

    public boolean hasCircle(ListNode head) {

        if (head == null || head.next == null) return false;
        ListNode slow = head;
        ListNode fast = head.next;
        while (fast != null){
            if (slow == fast) return true;
            slow = slow.next;
            fast = fast.next;
            if (fast != null) fast = fast.next;
        }

        return false;
    }

    public ListNode getCircleNode(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode slow = head;
        ListNode fast = head.next;
        while (fast != null){
            if (slow == fast) break;
            slow = slow.next;
            fast = fast.next;
            if (fast != null) fast = fast.next;
        }

        if (fast == null) {
            return null;
        }

        slow = head;
        while (slow != fast){
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

    public int getLength(ListNode head) {
        if (head == null) return 0;
        int length = 0;
        while (head != null){
            length++;
            head = head.next;
        }
        return length;
    }
}
