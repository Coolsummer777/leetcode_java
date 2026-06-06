package sp_2026.airbnb;

import java.util.Stack;

public class P1020 {
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.size() == 0) return false;
                char tmp = stack.peek();
                if ((c == ')' && tmp == '(') || (c == ']' && tmp == '[') || (c == '}' && tmp == '{')) {
                    stack.pop();
                } else {
                    return false;
                }
            }
        }

        return stack.size() == 0;
    }
}
