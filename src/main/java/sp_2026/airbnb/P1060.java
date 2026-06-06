package sp_2026.airbnb;

import sp_2026.models.ListNode;

public class P1060 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {

        int alength = getLength(headA);
        int blength = getLength(headB);
        if (alength > blength){
            for (int i=0;i<alength - blength;i++){
                headA = headA.next;
            }
        }else {
            for (int i=0;i<blength - alength;i++){
                headB = headB.next;
            }
        }

        while (headA != null) {
            if (headA == headB){
                return headA;
            }
            headA = headA.next;
            headB = headB.next;
        }

        return null;
    }

    public int getLength(ListNode node){
        int count = 0;

        while (node != null) {
            count++;
            node = node.next;
        }


        return count;
    }
}
