package sp_2026.airbnb.coding;

import sp_2026.models.ListNode;
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
            if (slow == fast) return slow;
            slow = slow.next;
            fast = fast.next;
            if (fast != null) fast = fast.next;
        }
        return null;
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
