import java.util.*;
import java.io.*;

public class ProgramaPrincipal {
    //----------------------------------------------------------------------
    // ATRIBUTOS ESTÁTICOS DE LA CLASE PRINCIPAL
    //----------------------------------------------------------------------
    private static Scanner scanner = new Scanner(System.in);           
    private static GrafoFloyd grafo = new GrafoFloyd();              
    private static ResultadoFloyd resultado;              

    public static void main(String[] args) {
        try {
            System.out.println("=== Sistema de Optimización Logística ===");
            System.out.println("Cargando grafo desde archivo guategrafo.txt...");
            
            // Cargar datos del archivo y aplicar algoritmo inicial
            grafo.cargarDesdeArchivo("guategrafo.txt");
            System.out.println("Grafo cargado exitosamente.");
            
            // Aplicar algoritmo de Floyd con clima normal por defecto
            resultado = grafo.aplicarFloyd(0); // 0 = clima normal
            
            // Mostrar información inicial
            grafo.mostrarMatriz(0);
            String centro = grafo.calcularCentro(resultado.distancias);
            System.out.println("\nCentro del grafo: " + centro);
            
            // Iniciar bucle principal del menú
            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                int opcion = leerOpcion();
                
                switch (opcion) {
                    case 1:
                        consultarRutaMasCorta();
                        break;
                    case 2:
                        mostrarCentroGrafo();
                        break;
                    case 3:
                        modificarGrafo();
                        break;
                    case 4:
                        continuar = false;
                        System.out.println("¡Gracias por usar el sistema!");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo: " + e.getMessage());
            System.err.println("Asegúrese de que el archivo 'guategrafo.txt' existe.");
        }
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA MOSTRAR EL MENÚ PRINCIPAL
    //----------------------------------------------------------------------
    private static void mostrarMenu() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Consultar ruta más corta entre ciudades");
        System.out.println("2. Mostrar centro del grafo");
        System.out.println("3. Modificar grafo");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA LEER LA OPCIÓN DEL USUARIO
    //----------------------------------------------------------------------
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;  // Retorna -1 para opción inválida
        }
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA CONSULTAR LA RUTA MÁS CORTA
    //----------------------------------------------------------------------
    private static void consultarRutaMasCorta() {
        System.out.print("Ingrese ciudad origen: ");
        String origen = scanner.nextLine();
        System.out.print("Ingrese ciudad destino: ");
        String destino = scanner.nextLine();

        // Obtener el camino usando la matriz de rutas
        List<String> camino = grafo.obtenerCamino(origen, destino, resultado.siguiente);
        
        if (camino == null) {
            System.out.println("No existe ruta entre " + origen + " y " + destino);
            return;
        }

        // Calcular y mostrar la distancia total
        int indiceOrigen = grafo.getCiudades().indexOf(origen);
        int indiceDestino = grafo.getCiudades().indexOf(destino);
        
        if (indiceOrigen == -1 || indiceDestino == -1) {
            System.out.println("Una o ambas ciudades no existen.");
            return;
        }

        double distancia = resultado.distancias[indiceOrigen][indiceDestino];
        
        // Mostrar resultado completo
        System.out.println("\nRuta más corta de " + origen + " a " + destino + ":");
        System.out.println("Distancia total: " + distancia + " horas");
        System.out.print("Camino: ");
        for (int i = 0; i < camino.size(); i++) {
            System.out.print(camino.get(i));
            if (i < camino.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA MOSTRAR EL CENTRO DEL GRAFO
    //----------------------------------------------------------------------
    private static void mostrarCentroGrafo() {
        String centro = grafo.calcularCentro(resultado.distancias);
        System.out.println("\nEl centro del grafo es: " + centro);
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA MODIFICAR EL GRAFO
    //----------------------------------------------------------------------
    private static void modificarGrafo() {
        System.out.println("\n=== MODIFICAR GRAFO ===");
        System.out.println("1. Interrumpir tráfico entre ciudades");
        System.out.println("2. Establecer nueva conexión");
        System.out.println("3. Cambiar condición climática");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                interrumpirTrafico();
                break;
            case 2:
                establecerConexion();
                break;
            case 3:
                cambiarClima();
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }
        
        // Recalcular rutas y centro después de modificaciones
        resultado = grafo.aplicarFloyd(0);
        String nuevoCentro = grafo.calcularCentro(resultado.distancias);
        System.out.println("Nuevo centro del grafo: " + nuevoCentro);
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA INTERRUMPIR TRÁFICO ENTRE CIUDADES
    //----------------------------------------------------------------------
    private static void interrumpirTrafico() {
        System.out.print("Ciudad 1: ");
        String ciudad1 = scanner.nextLine();
        System.out.print("Ciudad 2: ");
        String ciudad2 = scanner.nextLine();
        
        grafo.eliminarConexion(ciudad1, ciudad2);
        System.out.println("Conexión eliminada entre " + ciudad1 + " y " + ciudad2);
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA ESTABLECER NUEVA CONEXIÓN
    //----------------------------------------------------------------------
    private static void establecerConexion() {
        System.out.print("Ciudad origen: ");
        String ciudad1 = scanner.nextLine();
        System.out.print("Ciudad destino: ");
        String ciudad2 = scanner.nextLine();
        
        try {
            System.out.print("Tiempo con clima normal: ");
            double normal = Double.parseDouble(scanner.nextLine());
            System.out.print("Tiempo con lluvia: ");
            double lluvia = Double.parseDouble(scanner.nextLine());
            System.out.print("Tiempo con nieve: ");
            double nieve = Double.parseDouble(scanner.nextLine());
            System.out.print("Tiempo con tormenta: ");
            double tormenta = Double.parseDouble(scanner.nextLine());
            
            grafo.agregarConexion(ciudad1, ciudad2, normal, lluvia, nieve, tormenta);
            System.out.println("Nueva conexión establecida.");
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numéricos válidos.");
        }
    }
    
    //----------------------------------------------------------------------
    // MÉTODO PARA CAMBIAR CONDICIÓN CLIMÁTICA
    //----------------------------------------------------------------------
    private static void cambiarClima() {
        System.out.println("Tipos de clima:");
        System.out.println("0 - Normal, 1 - Lluvia, 2 - Nieve, 3 - Tormenta");
        System.out.print("Seleccione tipo de clima: ");
        
        try {
            int tipoClima = Integer.parseInt(scanner.nextLine());
            if (tipoClima >= 0 && tipoClima <= 3) {
                resultado = grafo.aplicarFloyd(tipoClima);
                grafo.mostrarMatriz(tipoClima);
                String[] nombres = {"Normal", "Lluvia", "Nieve", "Tormenta"};
                System.out.println("Algoritmo aplicado con clima: " + nombres[tipoClima]);
            } else {
                System.out.println("Tipo de clima no válido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido.");
        }
    }
}