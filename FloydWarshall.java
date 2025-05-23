package HDT10;

import java.util.Arrays;

public class FloydWarshall {
    private int[][] dist;
    private int[][] next;

    public void compute(int[][] graph) {
        int n = graph.length;
        dist = new int[n][n];
        next = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(next[i], -1);
            for (int j = 0; j < n; j++) {
                dist[i][j] = graph[i][j];
                if (graph[i][j] != Integer.MAX_VALUE / 2) next[i][j] = j;
            }
        }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
    }

    public int[][] getDistances() {
        return dist;
    }

    public String getPath(int u, int v, Graph graph) {
        if (next[u][v] == -1) return "Sin ruta.";
        StringBuilder sb = new StringBuilder(graph.getCityName(u));
        while (u != v) {
            u = next[u][v];
            sb.append(" -> ").append(graph.getCityName(u));
        }
        return sb.toString();
    }
}
