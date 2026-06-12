package sp_2026.jiukun;

import java.util.*;

public class Solution {

    private int n, m;
    private boolean[][] blocked;
    private int[][] wDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private int[][] bDirs;
    private Map<String, Integer> memo = new HashMap<>();

    // 1=黑赢, 0=平局, -1=黑输
    public String play(int n, int m, int k1, int k2, int o,
                       List<List<Integer>> b, List<List<Integer>> w,
                       List<List<Integer>> x, List<String> d) {
        this.n = n;
        this.m = m;
        blocked = new boolean[n][m];
        for (List<Integer> cell : x) {
            blocked[cell.get(0)][cell.get(1)] = true;
        }

        bDirs = new int[d.size()][2];
        for (int i = 0; i < d.size(); i++) {
            bDirs[i] = parseDir(d.get(i));
        }

        List<int[]> blacks = toCoords(b);
        List<int[]> whites = toCoords(w);

        int result = dfs(blacks, whites, true, new HashSet<>());
        if (result == 1) return "Win";
        if (result == -1) return "Lose";
        return "Draw";
    }

    private int dfs(List<int[]> blacks, List<int[]> whites, boolean whiteTurn, Set<String> path) {
        String key = encode(blacks, whites, whiteTurn);
        if (memo.containsKey(key)) return memo.get(key);
        if (path.contains(key)) return 0;

        if (blackOnTop(blacks)) {
            memo.put(key, 1);
            return 1;
        }

        path.add(key);
        int result;

        if (whiteTurn) {
            result = 1;
            boolean hasMove = false;
            for (int i = 0; i < whites.size(); i++) {
                int r = whites.get(i)[0], c = whites.get(i)[1];
                for (int[] dir : wDirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (!canMoveTo(nr, nc, blacks, whites, true)) continue;
                    hasMove = true;
                    if (hasBlack(blacks, nr, nc)) {
                        path.remove(key);
                        memo.put(key, -1);
                        return -1;
                    }
                    List<int[]> nextBlacks = copy(blacks);
                    List<int[]> nextWhites = copy(whites);
                    nextWhites.get(i)[0] = nr;
                    nextWhites.get(i)[1] = nc;
                    result = Math.min(result, dfs(nextBlacks, nextWhites, false, path));
                }
            }
            if (!hasMove) result = 1;
        } else {
            result = -1;
            boolean hasMove = false;
            for (int i = 0; i < blacks.size(); i++) {
                int r = blacks.get(i)[0], c = blacks.get(i)[1];
                for (int[] dir : bDirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (!canMoveTo(nr, nc, blacks, whites, false)) continue;
                    hasMove = true;
                    if (hasWhite(whites, nr, nc) || nr == 0) {
                        path.remove(key);
                        memo.put(key, 1);
                        return 1;
                    }
                    List<int[]> nextBlacks = copy(blacks);
                    List<int[]> nextWhites = copy(whites);
                    nextBlacks.get(i)[0] = nr;
                    nextBlacks.get(i)[1] = nc;
                    result = Math.max(result, dfs(nextBlacks, nextWhites, true, path));
                }
            }
            if (!hasMove) result = -1;
        }

        path.remove(key);
        memo.put(key, result);
        return result;
    }

    private boolean canMoveTo(int r, int c, List<int[]> blacks, List<int[]> whites, boolean isWhite) {
        if (r < 0 || r >= n || c < 0 || c >= m || blocked[r][c]) return false;
        if (isWhite) {
            if (hasBlack(blacks, r, c)) return true;
            return !hasWhite(whites, r, c);
        }
        if (hasWhite(whites, r, c)) return true;
        return !hasBlack(blacks, r, c);
    }

    private boolean hasBlack(List<int[]> blacks, int r, int c) {
        for (int[] p : blacks) {
            if (p[0] == r && p[1] == c) return true;
        }
        return false;
    }

    private boolean hasWhite(List<int[]> whites, int r, int c) {
        for (int[] p : whites) {
            if (p[0] == r && p[1] == c) return true;
        }
        return false;
    }

    private boolean blackOnTop(List<int[]> blacks) {
        for (int[] p : blacks) {
            if (p[0] == 0) return true;
        }
        return false;
    }

    private List<int[]> copy(List<int[]> list) {
        List<int[]> res = new ArrayList<>();
        for (int[] p : list) {
            res.add(new int[]{p[0], p[1]});
        }
        return res;
    }

    private List<int[]> toCoords(List<List<Integer>> cells) {
        List<int[]> res = new ArrayList<>();
        for (List<Integer> cell : cells) {
            res.add(new int[]{cell.get(0), cell.get(1)});
        }
        return res;
    }

    private String encode(List<int[]> blacks, List<int[]> whites, boolean whiteTurn) {
        StringBuilder sb = new StringBuilder();
        sb.append(whiteTurn ? 'W' : 'B');
        for (int[] p : blacks) sb.append(p[0]).append(',').append(p[1]).append(';');
        sb.append('|');
        for (int[] p : whites) sb.append(p[0]).append(',').append(p[1]).append(';');
        return sb.toString();
    }

    private int[] parseDir(String dir) {
        return switch (dir) {
            case "U" -> new int[]{-1, 0};
            case "D" -> new int[]{1, 0};
            case "L" -> new int[]{0, -1};
            case "R" -> new int[]{0, 1};
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        };
    }
}
