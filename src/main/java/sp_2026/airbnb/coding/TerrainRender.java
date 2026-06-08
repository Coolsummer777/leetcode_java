package sp_2026.airbnb.coding;


/**
 * 

Terrain Rendering + Water Drop Simulation
Two-part problem. Part 1: given an array of column heights, print the terrain as an ASCII column chart. Part 2: drop `W` units of water at column `c` and print the terrain after the water settles; water flows downhill and pools in valleys.

Requirements
Part 1 — Render terrain
Input: heights = [5, 4, 3, 2, 1, 3, 4, 0, 3, 4].

Print an ASCII picture where each column has heights[i] + characters stacked on top of a shared baseline.

+
++   + +
+++ ++ ++
++++ ++ ++
+++++++ ++
++++++++++   <-- base layer
Part 2 — Drop water
dumpWater(terrain, waterAmount=8, column=1) drops W units of water at column c. Each unit:

Tries to flow left if the resting height to the left is strictly lower than the current resting height.
Otherwise tries to flow right under the same rule.
Otherwise rests on top of the current column.
When choosing between left and right, the side with the strictly lower minimum reachable height wins; ties go left (clarify convention).

Output: re-render the terrain with W to mark settled water and + for ground.

Example output for the input above with W=8, column=1:

+
++WWWW + +
+++WW++ ++
++++W++ ++
+++++++W++
++++++++++   <-- base layer
Notes
This is essentially a single-source variant of "Trapping Rain Water" with explicit ASCII rendering.
The two-pointer "expand outward" simulation is the cleanest approach: at each step, recompute the resting height as terrain[c] + water[c]; scan left and right until reaching a column whose resting height is >= current; pick the lower-min side; if both block, the unit rests at c.
Edge cases that bite: water dropping at index 0 / n-1, columns of equal height (tie-breaker), a unit that cannot flow either direction (settles in place), repeated drops at the same column (state must persist between units).
Many candidates over-engineer with BFS over the grid; the array-level simulation is simpler and runs in O(W · n) worst case.
Rendering: build a height_grid[max_height][n] of characters, write + / W bottom-up, then print top-down. Skip empty top rows.
Preparation
Implement Part 1 (rendering) in under 8 minutes — it is the easier half but consumes time if you fumble the row order.
Implement the single-drop simulation, then loop W times for Part 2.
Hand-trace a 4-unit drop on [3, 1, 2, 1, 3] at column 2; verify water spills left-then-right correctly.
Be ready for the follow-up: "what if W is 1e9?" Drill an O(n log n) precompute-the-valleys approach using a monotonic stack identical to the canonical trapping-rain-water solution.


 */

public class TerrainRender {

    public static void handle(){
        int[] terrain = new int[] {5, 4, 3, 2, 1, 3, 4, 0, 3, 4};
        render(terrain);
    }

    public static void render(int[] terrain) {
        int maxHeight = 0;
        for (int height : terrain) {
            maxHeight = Math.max(maxHeight, height);
        }

        for (int i = maxHeight - 1; i >= 0; i--) {
            for (int j=0;j<terrain.length;j++){
                if (terrain[j] > i) {
                    System.out.print("+");
                } else {
                    System.out.print(" ");
                }
            }


            System.out.println();
        }
    }
}
