public class Main {
    public static void main(String[] args) {
        // 在这里调用你想要执行的 LeetCode 代码
        System.out.println("LeetCode Java 入口已启动");

        // 示例调用
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);
        System.out.println("twoSum result: [" + result[0] + ", " + result[1] + "]");
    }

    // 示例 LeetCode 解题方法
    public static int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
