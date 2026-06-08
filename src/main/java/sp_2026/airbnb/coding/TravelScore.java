package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 

Ski Path — Max Score on a Weighted DAG
Given a weighted directed acyclic graph where edges carry a cost and nodes carry a reward, compute the maximum `sum(rewards) - sum(costs)` over any path from a fixed `START` to any `END` node.


Requirements
Input:
travel: list of (from, cost, to) edges — e.g. [["start","3","A"], ["A","4","B"], ["B","5","END1"]].
points: list of (node, reward) — e.g. [["A","5"], ["B","6"], ["END1","3"]].
Output: the maximum score sum(node rewards on the chosen path) - sum(edge costs on the chosen path) from START to any node whose id starts with END.
Multiple end nodes are possible; the start is fixed.
Notes
Topologically sort the DAG, then run a forward DP: best[v] = max(best[u] + reward[v] - cost(u, v)) for every incoming edge (u, v). Final answer: max(best[e]) over end nodes.
If the graph might contain cycles, the problem becomes longest-path-on-a-general-graph (NP-hard) — clarify upfront that the graph is a DAG, otherwise propose Bellman-Ford with a cycle check.
Reward applies to nodes, cost applies to edges — keep the two indexed separately. Many candidates fold them and lose the START-has-no-reward edge case.
Follow-up: "what if multiple skiers traverse the graph simultaneously and share the cost / reward?" The standard answer is an M-skier DP over the product state (visited_node_bitmask, skier_id) for small M, or an LP relaxation for large M. This follow-up is the gate to the senior signal — pre-script your answer.
The community version of this prompt has multiple wording variants; the algorithm is invariant.
Preparation
Implement topological sort + DP in under 25 minutes; verify on the cited 5-node example.
Pre-write the multi-skier follow-up sketch (bitmask DP for small M, LP / min-cost-flow framing for large M).
Hand-trace the example: start → A → B → END1 gives (5+6+3) - (3+4+5) = 14 - 12 = 2.
Prepare a one-sentence answer for "what if reward depends on the order of visits" (it becomes a TSP variant).


 */


public class TravelScore {

    public int maxScore(String[][] travel, String[][] point) {
        Map<String, List<Edge>> graph = buildGraph(travel);
        Map<String, Integer> reward = buildRewardMap(point);
        Map<String, Integer> bestScore = maxScoreFromStart(graph, reward);

        int best = Integer.MIN_VALUE;
        for (String node : bestScore.keySet()) {
            if (!isEndNode(node)) {
                continue;
            }
            best = Math.max(best, bestScore.get(node));
        }
        return best == Integer.MIN_VALUE ? 0 : best;
    }

    private Map<String, List<Edge>> buildGraph(String[][] travel) {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (String[] edge : travel) {
            String from = edge[0];
            int cost = Integer.parseInt(edge[1]);
            String to = edge[2];
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, cost));
        }
        return graph;
    }

    private Map<String, Integer> buildRewardMap(String[][] point) {
        Map<String, Integer> reward = new HashMap<>();
        for (String[] entry : point) {
            reward.put(entry[0], Integer.parseInt(entry[1]));
        }
        return reward;
    }

    /**
     * SPFA: only relax outgoing edges from reachable nodes.
     * dp[v] = max(dp[u] + reward(v) - cost(u, v))
     */
    private Map<String, Integer> maxScoreFromStart(
            Map<String, List<Edge>> graph,
            Map<String, Integer> reward) {
        Map<String, Integer> score = new HashMap<>();
        score.put("start", 0);

        Deque<String> queue = new ArrayDeque<>();
        queue.offer("start");

        while (!queue.isEmpty()) {
            String from = queue.pollFirst();
            int fromScore = score.get(from);
            for (Edge edge : graph.getOrDefault(from, List.of())) {
                int candidate = fromScore + getReward(reward, edge.to) - edge.cost;
                Integer known = score.get(edge.to);
                if (known == null || candidate > known) {
                    score.put(edge.to, candidate);
                    queue.offer(edge.to);
                }
            }
        }
        return score;
    }

    private boolean isEndNode(String node) {
        return !"start".equals(node) && node.toUpperCase().contains("END");
    }

    private int getReward(Map<String, Integer> reward, String node) {
        return reward.getOrDefault(node, 0);
    }

    private static final class Edge {
        final String to;
        final int cost;

        Edge(String to, int cost) {
            this.to = to;
            this.cost = cost;
        }
    }
}
