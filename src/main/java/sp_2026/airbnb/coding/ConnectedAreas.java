package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ConnectedAreas {

    public static void handle(){
        String[][] grid = new String[][] {
            {"G1", "G2", "W0", "W1", "S1"},
            {"G2", "G3", "W0", "W1", "S1"},
            {"S2", "S3", "S1", "G1", "S1"},
            {"G1", "G2", "W0", "W1", "S1"},
            {"G1", "G2", "W0", "W1", "S1"}
        };
        System.out.println(calculateScore(grid));
    }

    public static String[][] parseString(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[0][];
        }

        String[] lines = raw.trim().split("\\R");
        List<String[]> rows = new ArrayList<>();
        int expectedCols = -1;

        for (int row = 0; row < lines.length; row++) {
            String line = lines[row].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("[,\\s]+");
            if (expectedCols == -1) {
                expectedCols = tokens.length;
            } else if (tokens.length != expectedCols) {
                throw new IllegalArgumentException(
                    "Ragged grid at row " + row + ": expected " + expectedCols + " columns, got " + tokens.length);
            }

            String[] normalizedRow = new String[tokens.length];
            for (int col = 0; col < tokens.length; col++) {
                normalizedRow[col] = validateTile(tokens[col], row, col);
            }
            rows.add(normalizedRow);
        }

        return rows.toArray(new String[0][]);
    }

    private static String validateTile(String tile, int row, int col) {
        if (tile == null || tile.length() < 2 || !Character.isLetter(tile.charAt(0))) {
            throw new IllegalArgumentException("Invalid tile at (" + row + ", " + col + "): " + tile);
        }
        try {
            Integer.parseInt(tile.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid crown at (" + row + ", " + col + "): " + tile);
        }
        return tile.toUpperCase();
    }

    public static int calculateScore(String[][] grid){
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int score = 0;
        int m = grid.length;
        int n = grid[0].length;
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (!visited[i][j] ) {
                    score += getScoreForOneArea(grid, visited, i, j);
                }
            }
        }

        return score;
    }

    public static int getScoreForOneArea(String[][] grid, boolean[][] visited, int row, int col){
        int m = grid.length;
        int n = grid[0].length;
        char color = grid[row][col].charAt(0);
        int count = 1;
        int crown = Integer.parseInt(grid[row][col].substring(1));
        int[][] directions = new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {row, col});
        visited[row][col] = true;
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int currRow = curr[0];
            int currCol = curr[1];

            for (int[] direction : directions) {
                int newRow = currRow + direction[0];
                int newCol = currCol + direction[1];
                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && !visited[newRow][newCol] && grid[newRow][newCol].charAt(0) == color) {
                    queue.offer(new int[] {newRow, newCol});
                    visited[newRow][newCol] = true;
                    count++;
                    crown += Integer.parseInt(grid[newRow][newCol].substring(1));
                }
            }

        }
        return count * crown;
    }   
}
