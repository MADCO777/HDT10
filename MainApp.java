package HDT10;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        Graph graph = new Graph("logistica.txt");
        FloydWarshall floyd = new FloydWarshall();
        GraphCenter centerFinder = new GraphCenter();

        while (true) {
            graph.setClimate("normal");
            floyd.compute(graph.getActiveMatrix());

            System.out.println("\n--- Menú ---");
            System.out.println("1. Ruta más corta");
            System.out.println("2. Ver centro del grafo");
            System.out.println("3. Modificar el grafo");
            System.out.println("4. Salir");
            System.out.print("Seleccione opción: ");
            int opcion = input.nextInt();
            input.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ciudad origen: ");
                    String origen = input.nextLine();
                    System.out.print("Ciudad destino: ");
                    String destino = input.nextLine();

                    int u = graph.getCityIndex(origen);
                    int v = graph.getCityIndex(destino);
                    if (u == -1 || v == -1) {
                        System.out.println("Ciudad no encontrada.");
                    } else {
                        System.out.println("Ruta más corta: " +
                                floyd.getPath(u, v, graph));
                        System.out.println("Tiempo: " +
                                floyd.getDistances()[u][v] + " horas");
                    }
                }
                case 2 -> {
                    int centro = centerFinder.getCenter(floyd.getDistances());
                    System.out.println("Centro del grafo: " +
                            graph.getCityName(centro));
                }
                case 3 -> {
                    System.out.println("\n-- Modificación del grafo --");
                    System.out.println("a) Interrumpir tráfico");
                    System.out.println("b) Establecer nueva conexión");
                    System.out.println("c) Cambiar clima entre ciudades");
                    System.out.print("Seleccione opción: ");
                    String mod = input.nextLine();

                    switch (mod.toLowerCase()) {
                        case "a" -> {
                            System.out.print("Ciudad origen: ");
                            String origen = input.nextLine();
                            System.out.print("Ciudad destino: ");
                            String destino = input.nextLine();
                            graph.interrumpirConexion(origen, destino);
                            System.out.println("Conexión interrumpida.");
                        }
                        case "b" -> {
                            System.out.print("Ciudad origen: ");
                            String origen = input.nextLine();
                            System.out.print("Ciudad destino: ");
                            String destino = input.nextLine();
                            System.out.print("Tiempo normal: ");
                            int normal = input.nextInt();
                            System.out.print("Tiempo lluvia: ");
                            int lluvia = input.nextInt();
                            System.out.print("Tiempo nieve: ");
                            int nieve = input.nextInt();
                            System.out.print("Tiempo tormenta: ");
                            int tormenta = input.nextInt();
                            input.nextLine(); // limpiar buffer
                            graph.agregarConexion(origen, destino, normal, lluvia, nieve, tormenta);
                            System.out.println("Conexión agregada.");
                        }
                        case "c" -> {
                            System.out.print("Ciudad origen: ");
                            String origen = input.nextLine();
                            System.out.print("Ciudad destino: ");
                            String destino = input.nextLine();
                            System.out.print("Tipo de clima (normal, lluvia, nieve, tormenta): ");
                            String clima = input.nextLine();
                            System.out.print("Nuevo tiempo (en horas): ");
                            int tiempo = input.nextInt();
                            input.nextLine(); // limpiar buffer
                            graph.cambiarClimaEnConexion(origen, destino, clima, tiempo);
                            System.out.println("Clima actualizado en conexión.");
                        }
                        default -> System.out.println("Opción inválida.");
                    }

                    // Recalcular Floyd y centro
                    floyd.compute(graph.getActiveMatrix());
                    int centro = centerFinder.getCenter(floyd.getDistances());
                    System.out.println("Nuevo centro del grafo: " + graph.getCityName(centro));
                }
                case 4 -> {
                    System.out.println("Programa finalizado.");
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }
}
