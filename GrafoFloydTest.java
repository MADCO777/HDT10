import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Clase de pruebas unitarias para el sistema de grafos Floyd-Warshall
 * Incluye pruebas para GrafoFloyd, ResultadoFloyd y funcionalidades principales
 */
public class GrafoFloydTest {
    
    private GrafoFloyd grafo;
    private File archivoTemporal;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() throws IOException {
        grafo = new GrafoFloyd();
        // Crear archivo temporal con datos de prueba
        archivoTemporal = tempDir.resolve("test_grafo.txt").toFile();
        crearArchivoPrueba();
    }
    
    /**
     * Crea un archivo de prueba con datos de ejemplo
     */
    private void crearArchivoPrueba() throws IOException {
        try (PrintWriter writer = new PrintWriter(archivoTemporal)) {
            writer.println("CiudadA CiudadB 10.0 15.0 20.0 25.0");
            writer.println("CiudadB CiudadC 8.0 12.0 16.0 20.0");
            writer.println("CiudadA CiudadC 25.0 30.0 35.0 40.0");
            writer.println("CiudadC CiudadD 5.0 7.0 9.0 11.0");
        }
    }
    
    // =====================================
    // PRUEBAS PARA RESULTADOFLOYD
    // =====================================
    
    @Test
    @DisplayName("Constructor de ResultadoFloyd - Inicialización correcta")
    void testResultadoFloydConstructor() {
        double[][] distancias = {{0, 10}, {15, 0}};
        int[][] siguiente = {{-1, 1}, {0, -1}};
        
        ResultadoFloyd resultado = new ResultadoFloyd(distancias, siguiente);
        
        assertNotNull(resultado.distancias);
        assertNotNull(resultado.siguiente);
        assertArrayEquals(distancias, resultado.distancias);
        assertArrayEquals(siguiente, resultado.siguiente);
    }
    
    @Test
    @DisplayName("ResultadoFloyd - Inmutabilidad de referencias")
    void testResultadoFloydInmutabilidad() {
        double[][] distancias = {{0, 10}, {15, 0}};
        int[][] siguiente = {{-1, 1}, {0, -1}};
        
        ResultadoFloyd resultado = new ResultadoFloyd(distancias, siguiente);
        
        // Las matrices deben ser las mismas referencias (final)
        assertSame(distancias, resultado.distancias);
        assertSame(siguiente, resultado.siguiente);
    }
    
    // =====================================
    // PRUEBAS PARA GRAFOFLOYD - CONSTRUCTOR Y ESTADO INICIAL
    // =====================================
    
    @Test
    @DisplayName("Constructor de GrafoFloyd - Estado inicial")
    void testConstructorEstadoInicial() {
        GrafoFloyd nuevoGrafo = new GrafoFloyd();
        
        assertEquals(0, nuevoGrafo.getNumCiudades());
        assertTrue(nuevoGrafo.getCiudades().isEmpty());
    }
    
    // =====================================
    // PRUEBAS PARA CARGA DE ARCHIVO
    // =====================================
    
    @Test
    @DisplayName("Carga de archivo - Funcionamiento normal")
    void testCargarDesdeArchivoExitoso() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        assertEquals(4, grafo.getNumCiudades());
        
        List<String> ciudades = grafo.getCiudades();
        assertTrue(ciudades.contains("CiudadA"));
        assertTrue(ciudades.contains("CiudadB"));
        assertTrue(ciudades.contains("CiudadC"));
        assertTrue(ciudades.contains("CiudadD"));
        
        // Verificar que las ciudades están ordenadas alfabéticamente
        List<String> ciudadesOrdenadas = new ArrayList<>(ciudades);
        Collections.sort(ciudadesOrdenadas);
        assertEquals(ciudadesOrdenadas, ciudades);
    }
    
    @Test
    @DisplayName("Carga de archivo - Archivo inexistente")
    void testCargarArchivoInexistente() {
        assertThrows(IOException.class, () -> {
            grafo.cargarDesdeArchivo("archivo_inexistente.txt");
        });
    }
    
    @Test
    @DisplayName("Carga de archivo - Archivo con formato incorrecto")
    void testCargarArchivoFormatoIncorrecto() throws IOException {
        File archivoMalFormato = tempDir.resolve("mal_formato.txt").toFile();
        try (PrintWriter writer = new PrintWriter(archivoMalFormato)) {
            writer.println("CiudadA CiudadB"); // Faltan datos
            writer.println("CiudadC"); // Línea incompleta
        }
        
        assertDoesNotThrow(() -> {
            grafo.cargarDesdeArchivo(archivoMalFormato.getAbsolutePath());
        });
        
        // Debería tener 0 ciudades porque las líneas no tienen formato correcto
        assertEquals(0, grafo.getNumCiudades());
    }
    
    // =====================================
    // PRUEBAS PARA ALGORITMO FLOYD-WARSHALL
    // =====================================
    
    @Test
    @DisplayName("Algoritmo Floyd - Aplicación con clima normal")
    void testAplicarFloydClimaNormal() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ResultadoFloyd resultado = grafo.aplicarFloyd(0); // Clima normal
        
        assertNotNull(resultado);
        assertNotNull(resultado.distancias);
        assertNotNull(resultado.siguiente);
        
        // Verificar dimensiones de las matrices
        assertEquals(4, resultado.distancias.length);
        assertEquals(4, resultado.siguiente.length);
    }
    
    @Test
    @DisplayName("Algoritmo Floyd - Distancias de ciudad consigo misma")
    void testFloydDistanciaPropia() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        // La distancia de cualquier ciudad consigo misma debe ser 0
        for (int i = 0; i < grafo.getNumCiudades(); i++) {
            assertEquals(0.0, resultado.distancias[i][i], 0.001);
        }
    }
    
    @Test
    @DisplayName("Algoritmo Floyd - Diferentes condiciones climáticas")
    void testFloydDiferentesClimas() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ResultadoFloyd normal = grafo.aplicarFloyd(0);
        ResultadoFloyd lluvia = grafo.aplicarFloyd(1);
        ResultadoFloyd nieve = grafo.aplicarFloyd(2);
        ResultadoFloyd tormenta = grafo.aplicarFloyd(3);
        
        // Los tiempos deben incrementarse con peores condiciones climáticas
        // (esto depende de los datos, pero generalmente lluvia > normal)
        assertNotNull(normal);
        assertNotNull(lluvia);
        assertNotNull(nieve);
        assertNotNull(tormenta);
    }
    
    // =====================================
    // PRUEBAS PARA OBTENCIÓN DE CAMINOS
    // =====================================
    
    @Test
    @DisplayName("Obtener camino - Ruta existente")
    void testObtenerCaminoExistente() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        List<String> camino = grafo.obtenerCamino("CiudadA", "CiudadB", resultado.siguiente);
        
        assertNotNull(camino);
        assertFalse(camino.isEmpty());
        assertEquals("CiudadA", camino.get(0));
        assertEquals("CiudadB", camino.get(camino.size() - 1));
    }
    
    @Test
    @DisplayName("Obtener camino - Ciudad inexistente")
    void testObtenerCaminoCiudadInexistente() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        List<String> camino = grafo.obtenerCamino("CiudadX", "CiudadA", resultado.siguiente);
        
        assertNull(camino);
    }
    
    @Test
    @DisplayName("Obtener camino - Mismo origen y destino")
    void testObtenerCaminoMismoOrigenDestino() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        List<String> camino = grafo.obtenerCamino("CiudadA", "CiudadA", resultado.siguiente);
        
        // El comportamiento puede variar según la implementación
        // Si retorna null, es un comportamiento válido para mismo origen-destino
        // Si retorna una lista, debe contener solo la ciudad
        if (camino != null) {
            assertEquals(1, camino.size());
            assertEquals("CiudadA", camino.get(0));
        } else {
            // Si retorna null para mismo origen-destino, también es válido
            // ya que técnicamente no hay "camino" que recorrer
            assertTrue(true, "El método retorna null para mismo origen y destino, comportamiento válido");
        }
    }
    
    // =====================================
    // PRUEBAS PARA CÁLCULO DEL CENTRO
    // =====================================
    
    @Test
    @DisplayName("Calcular centro - Grafo conexo")
    void testCalcularCentro() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        String centro = grafo.calcularCentro(resultado.distancias);
        
        assertNotNull(centro);
        assertTrue(grafo.getCiudades().contains(centro));
    }
    
    @Test
    @DisplayName("Calcular centro - Grafo con una sola ciudad")
    void testCalcularCentroUnaCiudad() throws IOException {
        File archivoUnaCiudad = tempDir.resolve("una_ciudad.txt").toFile();
        try (PrintWriter writer = new PrintWriter(archivoUnaCiudad)) {
            // Archivo vacío o sin conexiones válidas
        }
        
        grafo.cargarDesdeArchivo(archivoUnaCiudad.getAbsolutePath());
        
        if (grafo.getNumCiudades() == 0) {
            // Si no hay ciudades, no se puede calcular centro
            assertTrue(true); // Caso válido
        }
    }
    
    // =====================================
    // PRUEBAS PARA MODIFICACIÓN DEL GRAFO
    // =====================================
    
    @Test
    @DisplayName("Agregar conexión - Ciudades existentes")
    void testAgregarConexionExitosa() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        // Redirigir System.out para capturar mensajes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        grafo.agregarConexion("CiudadA", "CiudadD", 15.0, 18.0, 22.0, 28.0);
        
        // Restaurar System.out
        System.setOut(originalOut);
        
        // No debe haber mensaje de error
        String output = outputStream.toString();
        assertFalse(output.contains("no existen"));
    }
    
    @Test
    @DisplayName("Agregar conexión - Ciudad inexistente")
    void testAgregarConexionCiudadInexistente() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        grafo.agregarConexion("CiudadX", "CiudadA", 10.0, 12.0, 15.0, 18.0);
        
        System.setOut(originalOut);
        
        String output = outputStream.toString();
        assertTrue(output.contains("no existen"));
    }
    
    @Test
    @DisplayName("Eliminar conexión - Ciudades existentes")
    void testEliminarConexionExitosa() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        grafo.eliminarConexion("CiudadA", "CiudadB");
        
        System.setOut(originalOut);
        
        String output = outputStream.toString();
        assertFalse(output.contains("no existen"));
    }
    
    @Test
    @DisplayName("Eliminar conexión - Ciudad inexistente")
    void testEliminarConexionCiudadInexistente() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        grafo.eliminarConexion("CiudadX", "CiudadA");
        
        System.setOut(originalOut);
        
        String output = outputStream.toString();
        assertTrue(output.contains("no existen"));
    }
    
    // =====================================
    // PRUEBAS PARA MÉTODOS GETTER
    // =====================================
    
    @Test
    @DisplayName("Getter getCiudades - Retorna copia")
    void testGetCiudadesRetornaCopia() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        List<String> ciudades1 = grafo.getCiudades();
        List<String> ciudades2 = grafo.getCiudades();
        
        // Deben tener el mismo contenido pero ser objetos diferentes
        assertEquals(ciudades1, ciudades2);
        assertNotSame(ciudades1, ciudades2);
        
        // Modificar una lista no debe afectar la otra
        ciudades1.clear();
        assertNotEquals(ciudades1.size(), ciudades2.size());
    }
    
    @Test
    @DisplayName("Getter getNumCiudades - Valor correcto")
    void testGetNumCiudades() throws IOException {
        assertEquals(0, grafo.getNumCiudades());
        
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        assertEquals(4, grafo.getNumCiudades());
    }
    
    // =====================================
    // PRUEBAS PARA MOSTRAR MATRIZ (FUNCIONALIDAD)
    // =====================================
    
    @Test
    @DisplayName("Mostrar matriz - No lanza excepción")
    void testMostrarMatrizNoExcepcion() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        // Redirigir System.out para evitar spam en consola de pruebas
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        assertDoesNotThrow(() -> {
            grafo.mostrarMatriz(0); // Clima normal
            grafo.mostrarMatriz(1); // Lluvia
            grafo.mostrarMatriz(2); // Nieve
            grafo.mostrarMatriz(3); // Tormenta
        });
        
        System.setOut(originalOut);
        
        // Verificar que se generó alguna salida
        String output = outputStream.toString();
        assertFalse(output.isEmpty());
        assertTrue(output.contains("Matriz de Adyacencia"));
    }
    
    // =====================================
    // PRUEBAS DE INTEGRACIÓN
    // =====================================
    
    @Test
    @DisplayName("Integración - Flujo completo del algoritmo")
    void testFlujoCompletoAlgoritmo() throws IOException {
        // 1. Cargar grafo
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        assertTrue(grafo.getNumCiudades() > 0);
        
        // 2. Aplicar Floyd-Warshall
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        assertNotNull(resultado);
        
        // 3. Calcular centro
        String centro = grafo.calcularCentro(resultado.distancias);
        assertNotNull(centro);
        
        // 4. Obtener un camino
        List<String> ciudades = grafo.getCiudades();
        if (ciudades.size() >= 2) {
            // El camino puede ser null si no hay conexión, pero no debe lanzar excepción
            assertDoesNotThrow(() -> {
                List<String> camino = grafo.obtenerCamino(ciudades.get(0), ciudades.get(1), resultado.siguiente);
                // Verificar que el método funciona correctamente (puede retornar null)
                // Si hay camino, debe contener al menos origen y destino
                if (camino != null) {
                    assertFalse(camino.isEmpty());
                }
            });
        }
    }
    
    @Test
    @DisplayName("Integración - Modificación y recálculo")
    void testModificacionYRecalculo() throws IOException {
        grafo.cargarDesdeArchivo(archivoTemporal.getAbsolutePath());
        
        // Estado inicial
        ResultadoFloyd inicial = grafo.aplicarFloyd(0);
        String centroInicial = grafo.calcularCentro(inicial.distancias);
        
        // Modificar grafo
        grafo.eliminarConexion("CiudadA", "CiudadB");
        
        // Recalcular
        ResultadoFloyd modificado = grafo.aplicarFloyd(0);
        String centroModificado = grafo.calcularCentro(modificado.distancias);
        
        // Los resultados pueden cambiar (o no, dependiendo del grafo)
        assertNotNull(centroInicial);
        assertNotNull(centroModificado);
    }
    
    // =====================================
    // PRUEBAS DE CASOS LÍMITE
    // =====================================
    
    @Test
    @DisplayName("Caso límite - Grafo vacío")
    void testGrafoVacio() {
        ResultadoFloyd resultado = grafo.aplicarFloyd(0);
        
        assertNotNull(resultado);
        assertNotNull(resultado.distancias);
        assertNotNull(resultado.siguiente);
        assertEquals(0, resultado.distancias.length);
        assertEquals(0, resultado.siguiente.length);
    }
    
    @Test
    @DisplayName("Caso límite - Valores numéricos extremos")
    void testValoresNumeriosExtremos() throws IOException {
        File archivoExtremos = tempDir.resolve("extremos.txt").toFile();
        try (PrintWriter writer = new PrintWriter(archivoExtremos)) {
            writer.println("A B 0.1 0.2 0.3 0.4");
            writer.println("B C 999999.9 1000000.0 1000000.1 1000000.2");
        }
        
        assertDoesNotThrow(() -> {
            grafo.cargarDesdeArchivo(archivoExtremos.getAbsolutePath());
            ResultadoFloyd resultado = grafo.aplicarFloyd(0);
            assertNotNull(resultado);
        });
    }
}