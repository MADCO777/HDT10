import java.util.*;
import java.io.*;

public class GrafoFloyd {
    
    private Map<String, Integer> ciudadIndice;    
    private List<String> ciudades;                
    private double[][][] matriz;                  
    private int numCiudades;                      
    

    private static final int CLIMA_NORMAL = 0;     // Condiciones normales de viaje
    private static final int CLIMA_LLUVIA = 1;     // Condiciones con lluvia 
    private static final int CLIMA_NIEVE = 2;      // Condiciones con nieve 
    private static final int CLIMA_TORMENTA = 3;   // Condiciones de tormenta 
    private static final double INFINITO = Double.MAX_VALUE;  // Representa rutas inexistentes
    
 
    public GrafoFloyd() {
        ciudadIndice = new HashMap<>();
        ciudades = new ArrayList<>();
        numCiudades = 0;
    }
    
    
    public void cargarDesdeArchivo(String nombreArchivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
        String linea;
        Set<String> ciudadesUnicas = new HashSet<>();
        List<String[]> datos = new ArrayList<>();

     
        while ((linea = br.readLine()) != null) {
            String[] partes = linea.trim().split("\\s+");
            if (partes.length >= 6) {  
                ciudadesUnicas.add(partes[0]);
                ciudadesUnicas.add(partes[1]);
                datos.add(partes);
            }
        }
        br.close();

       
        ciudades = new ArrayList<>(ciudadesUnicas);
        Collections.sort(ciudades);  // Ordenar alfabéticamente para consistencia
        numCiudades = ciudades.size();
        
        for (int i = 0; i < numCiudades; i++) {
            ciudadIndice.put(ciudades.get(i), i);
        }

        matriz = new double[4][numCiudades][numCiudades];
        for (int clima = 0; clima < 4; clima++) {
            for (int i = 0; i < numCiudades; i++) {
                for (int j = 0; j < numCiudades; j++) {
                    if (i == j) {
                        matriz[clima][i][j] = 0;  // Distancia de una ciudad a sí misma es 0
                    } else {
                        matriz[clima][i][j] = INFINITO;  // Inicialmente no hay conexiones
                    }
                }
            }
        }

       
        for (String[] partes : datos) {
            String ciudad1 = partes[0];
            String ciudad2 = partes[1];
            int indice1 = ciudadIndice.get(ciudad1);
            int indice2 = ciudadIndice.get(ciudad2);

            // Asignar tiempos para cada condición climática
            matriz[CLIMA_NORMAL][indice1][indice2] = Double.parseDouble(partes[2]);
            matriz[CLIMA_LLUVIA][indice1][indice2] = Double.parseDouble(partes[3]);
            matriz[CLIMA_NIEVE][indice1][indice2] = Double.parseDouble(partes[4]);
            matriz[CLIMA_TORMENTA][indice1][indice2] = Double.parseDouble(partes[5]);
        }
    }
    
   
    public ResultadoFloyd aplicarFloyd(int tipoClima) {
        double[][] distancias = new double[numCiudades][numCiudades];
        int[][] siguiente = new int[numCiudades][numCiudades];

        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                distancias[i][j] = matriz[tipoClima][i][j];
                if (i != j && matriz[tipoClima][i][j] != INFINITO) {
                    siguiente[i][j] = j;  
                } else {
                    siguiente[i][j] = -1;  
                }
            }
        }

        
        for (int k = 0; k < numCiudades; k++) {           // k = nodo intermedio
            for (int i = 0; i < numCiudades; i++) {       // i = nodo origen
                for (int j = 0; j < numCiudades; j++) {   // j = nodo destino
                    // Si existe ruta i->k y k->j, y es más corta que la ruta directa i->j
                    if (distancias[i][k] != INFINITO && 
                        distancias[k][j] != INFINITO &&
                        distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                        siguiente[i][j] = siguiente[i][k];  // Actualizar ruta
                    }
                }
            }
        }

        return new ResultadoFloyd(distancias, siguiente);
    }
    
 
    public List<String> obtenerCamino(String origen, String destino, int[][] siguiente) {
        // Verificar que ambas ciudades existen en el grafo
        if (!ciudadIndice.containsKey(origen) || !ciudadIndice.containsKey(destino)) {
            return null;
        }

        int i = ciudadIndice.get(origen);
        int j = ciudadIndice.get(destino);
        
        // Verificar que existe una ruta
        if (siguiente[i][j] == -1) {
            return null; // No hay camino
        }

        // Reconstruir el camino siguiendo la matriz de rutas
        List<String> camino = new ArrayList<>();
        camino.add(origen);
        
        while (i != j) {
            i = siguiente[i][j];
            camino.add(ciudades.get(i));
        }
        
        return camino;
    }
    
   
    public String calcularCentro(double[][] distancias) {
        double[] excentricidades = new double[numCiudades];
        
        // Calcular la excentricidad de cada ciudad
        for (int i = 0; i < numCiudades; i++) {
            double maxDistancia = 0;
            for (int j = 0; j < numCiudades; j++) {
                if (i != j && distancias[i][j] != INFINITO) {
                    maxDistancia = Math.max(maxDistancia, distancias[i][j]);
                }
            }
            excentricidades[i] = maxDistancia;
        }

        // Encontrar el vértice con mínima excentricidad (centro del grafo)
        double minExcentricidad = excentricidades[0];
        int indiceCentro = 0;
        
        for (int i = 1; i < numCiudades; i++) {
            if (excentricidades[i] < minExcentricidad) {
                minExcentricidad = excentricidades[i];
                indiceCentro = i;
            }
        }

        return ciudades.get(indiceCentro);
    }
    
   
    public void agregarConexion(String ciudad1, String ciudad2, 
                               double tiempoNormal, double tiempoLluvia, 
                               double tiempoNieve, double tiempoTormenta) {
        // Verificar que ambas ciudades existen en el grafo
        if (!ciudadIndice.containsKey(ciudad1) || !ciudadIndice.containsKey(ciudad2)) {
            System.out.println("Una o ambas ciudades no existen en el grafo.");
            return;
        }

        int indice1 = ciudadIndice.get(ciudad1);
        int indice2 = ciudadIndice.get(ciudad2);

        // Agregar conexión para todas las condiciones climáticas
        matriz[CLIMA_NORMAL][indice1][indice2] = tiempoNormal;
        matriz[CLIMA_LLUVIA][indice1][indice2] = tiempoLluvia;
        matriz[CLIMA_NIEVE][indice1][indice2] = tiempoNieve;
        matriz[CLIMA_TORMENTA][indice1][indice2] = tiempoTormenta;
    }
    
  
    public void eliminarConexion(String ciudad1, String ciudad2) {
        // Verificar que ambas ciudades existen en el grafo
        if (!ciudadIndice.containsKey(ciudad1) || !ciudadIndice.containsKey(ciudad2)) {
            System.out.println("Una o ambas ciudades no existen en el grafo.");
            return;
        }

        int indice1 = ciudadIndice.get(ciudad1);
        int indice2 = ciudadIndice.get(ciudad2);

        // Eliminar conexión para todas las condiciones climáticas
        for (int clima = 0; clima < 4; clima++) {
            matriz[clima][indice1][indice2] = INFINITO;
        }
    }
    
    
    public void mostrarMatriz(int tipoClima) {
        String[] tiposClima = {"Normal", "Lluvia", "Nieve", "Tormenta"};
        System.out.println("\nMatriz de Adyacencia - Clima " + tiposClima[tipoClima] + ":");
        
        // Encabezados de columna: nombres de ciudades
        System.out.print(String.format("%15s", ""));
        for (String ciudad : ciudades) {
            System.out.print(String.format("%15s", ciudad));
        }
        System.out.println();

        // Filas: cada fila representa una ciudad origen
        for (int i = 0; i < numCiudades; i++) {
            System.out.print(String.format("%15s", ciudades.get(i)));
            for (int j = 0; j < numCiudades; j++) {
                if (matriz[tipoClima][i][j] == INFINITO) {
                    System.out.print(String.format("%15s", "∞"));
                } else {
                    System.out.print(String.format("%15.1f", matriz[tipoClima][i][j]));
                }
            }
            System.out.println();
        }
    }
    
   
    public List<String> getCiudades() {
        return new ArrayList<>(ciudades);  // Retorna copia para evitar modificaciones externas
    }

    public int getNumCiudades() {
        return numCiudades;
    }
}
