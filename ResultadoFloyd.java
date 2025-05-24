public class ResultadoFloyd {
   
    public final double[][] distancias;  // Matriz de distancias mínimas entre todos los pares de ciudades
    public final int[][] siguiente;      // Matriz para reconstruir las rutas más cortas


    public ResultadoFloyd(double[][] distancias, int[][] siguiente) {
        this.distancias = distancias;
        this.siguiente = siguiente;
    }
}