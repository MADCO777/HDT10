package HDT10;

public class GraphCenter {
    public int getCenter(int[][] dist) {
        int minEccentricity = Integer.MAX_VALUE;
        int center = -1;

        for (int i = 0; i < dist.length; i++) {
            int maxDist = 0;
            for (int j = 0; j < dist.length; j++) {
                if (i != j) maxDist = Math.max(maxDist, dist[i][j]);
            }
            if (maxDist < minEccentricity) {
                minEccentricity = maxDist;
                center = i;
            }
        }
        return center;
    }
}
