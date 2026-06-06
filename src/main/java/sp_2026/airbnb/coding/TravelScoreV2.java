package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TravelScoreV2 {

    public int maxScore(String[][] travel, String[][] point) {
        Map<String, List<Edge>> graph = buildGraph(travel);
        Map<String, Integer> reward = buildRewardMap(point);
        Map<String, Integer> bestScore = maxScoreFromStart(graph, reward);

        int max = Integer.MIN_VALUE;
        for (String node:bestScore.keySet()){
            if (isEndNode(node)){
                max = Math.max(max, bestScore.get(node));
            }
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }
    
    private Map<String, Integer> maxScoreFromStart(Map<String, List<Edge>> graph, Map<String, Integer> reward){
        Map<String, Integer> bestScore = new HashMap<>();
        bestScore.put("start", 0);
        Deque<String> queue = new ArrayDeque<>();
        queue.addLast("start");

        while(queue.size() >0){
            String current = queue.pollFirst();
            List<Edge> edges = graph.getOrDefault(current, new ArrayList<>());
            int currentScore = bestScore.get(current);
            for (Edge edge:edges){
                if (currentScore + reward.getOrDefault(edge.to, 0) - edge.cost > bestScore.getOrDefault(edge.to,Integer.MIN_VALUE) ){
                    bestScore.put(edge.to, currentScore + reward.getOrDefault(edge.to, 0) - edge.cost);
                    queue.addLast(edge.to);
                }
            }
        }
        return bestScore;
    }

    private boolean isEndNode(String node){
        return node.toLowerCase().startsWith("end");
    }

    private Map<String, List<Edge>> buildGraph(String[][] travel){
        Map<String, List<Edge>> graph = new HashMap<>();

        for (String[] edge:travel){
            String from = edge[0];
            Edge e = new Edge(edge[2], Integer.parseInt(edge[1]));
            List<Edge> edges = graph.getOrDefault(from, new ArrayList<>());
            edges.add(e);
            graph.put(from, edges);
        }
        return graph;
    }

    private Map<String, Integer> buildRewardMap(String[][] point){
        Map<String, Integer> reward = new HashMap<>();
        for (String[] entry:point){
            reward.put(entry[0], Integer.parseInt(entry[1]));
        }
        return reward;
    }

    static class Edge{
        String to;
        int cost;

        Edge(String to, int cost){
            this.to = to;
            this.cost = cost;
        }
    }

}
