package sp_2026.airbnb;

import sp_2026.models.ListNode;

public class P1002 {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        if (l1 == null) return l2;
        if (l2 == null) return l1;

        int a = getLength(l1);
        int b = getLength(l2);

        ListNode longList = l1, shortList = l2;
        if (a < b) {
            longList = l2;
            shortList = l1;
        }

        ListNode head = longList;
        int carry = 0;

        while (longList != null) {
            longList.val += carry;
            if (shortList != null) {
                longList.val += shortList.val;
                shortList = shortList.next;
            }
            carry = longList.val / 10;
            longList.val %= 10;
            if (longList.next == null && carry > 0) {
                longList.next = new ListNode(carry);
                break;
            }
            longList = longList.next;
        }

        return head;
    }

    public int getLength(ListNode head) {
        int res = 0;

        while (head != null) {
            res++;
            head = head.next;
        }

        return res;
    }
}
