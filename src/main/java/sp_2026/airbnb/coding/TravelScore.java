package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
