package HDT10;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {
    private List<String> cities = new ArrayList<>();
    private Map<String, Integer> cityIndex = new HashMap<>();

    private int[][] tiempoNormal;
    private int[][] tiempoLluvia;
    private int[][] tiempoNieve;
    private int[][] tiempoTormenta;
    private int[][] activeMatrix;

    public Graph(String filename) throws FileNotFoundException {
        loadGraphFromFile(filename);
        setClimate("normal");
    }

    private void loadGraphFromFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        List<String[]> rawEdges = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(" ");
            rawEdges.add(tokens);
            addCityIfAbsent(tokens[0]);
            addCityIfAbsent(tokens[1]);
        }

        int n = cities.size();
        tiempoNormal = new int[n][n];
        tiempoLluvia = new int[n][n];
        tiempoNieve = new int[n][n];
        tiempoTormenta = new int[n][n];

        for (int[] row : tiempoNormal) Arrays.fill(row, Integer.MAX_VALUE / 2);
        for (int[] row : tiempoLluvia) Arrays.fill(row, Integer.MAX_VALUE / 2);
        for (int[] row : tiempoNieve) Arrays.fill(row, Integer.MAX_VALUE / 2);
        for (int[] row : tiempoTormenta) Arrays.fill(row, Integer.MAX_VALUE / 2);

        for (int i = 0; i < n; i++) {
            tiempoNormal[i][i] = 0;
            tiempoLluvia[i][i] = 0;
            tiempoNieve[i][i] = 0;
            tiempoTormenta[i][i] = 0;
        }

        for (String[] tokens : rawEdges) {
            int from = cityIndex.get(tokens[0]);
            int to = cityIndex.get(tokens[1]);
            tiempoNormal[from][to] = Integer.parseInt(tokens[2]);
            tiempoLluvia[from][to] = Integer.parseInt(tokens[3]);
            tiempoNieve[from][to] = Integer.parseInt(tokens[4]);
            tiempoTormenta[from][to] = Integer.parseInt(tokens[5]);
        }
    }

    private void addCityIfAbsent(String city) {
        if (!cityIndex.containsKey(city)) {
            cityIndex.put(city, cities.size());
            cities.add(city);
        }
    }

    public void setClimate(String climate) {
        switch (climate.toLowerCase()) {
            case "lluvia" -> activeMatrix = tiempoLluvia;
            case "nieve" -> activeMatrix = tiempoNieve;
            case "tormenta" -> activeMatrix = tiempoTormenta;
            default -> activeMatrix = tiempoNormal;
        }
    }

    public int[][] getActiveMatrix() {
        return activeMatrix;
    }

    public List<String> getCities() {
        return cities;
    }

    public int getCityIndex(String name) {
        return cityIndex.getOrDefault(name, -1);
    }

    public String getCityName(int index) {
        return cities.get(index);
    }

    public int size() {
        return cities.size();
    }

    public void interrumpirConexion(String from, String to) {
        int u = getCityIndex(from);
        int v = getCityIndex(to);
        if (u == -1 || v == -1) return;

        tiempoNormal[u][v] = Integer.MAX_VALUE / 2;
        tiempoLluvia[u][v] = Integer.MAX_VALUE / 2;
        tiempoNieve[u][v] = Integer.MAX_VALUE / 2;
        tiempoTormenta[u][v] = Integer.MAX_VALUE / 2;
    }

    public void agregarConexion(String from, String to, int normal, int lluvia, int nieve, int tormenta) {
        addCityIfAbsent(from);
        addCityIfAbsent(to);
        int u = getCityIndex(from);
        int v = getCityIndex(to);
        tiempoNormal[u][v] = normal;
        tiempoLluvia[u][v] = lluvia;
        tiempoNieve[u][v] = nieve;
        tiempoTormenta[u][v] = tormenta;
    }

    public void cambiarClimaEnConexion(String from, String to, String clima, int nuevoTiempo) {
        int u = getCityIndex(from);
        int v = getCityIndex(to);
        if (u == -1 || v == -1) return;

        switch (clima.toLowerCase()) {
            case "normal" -> tiempoNormal[u][v] = nuevoTiempo;
            case "lluvia" -> tiempoLluvia[u][v] = nuevoTiempo;
            case "nieve" -> tiempoNieve[u][v] = nuevoTiempo;
            case "tormenta" -> tiempoTormenta[u][v] = nuevoTiempo;
        }
    }
}
