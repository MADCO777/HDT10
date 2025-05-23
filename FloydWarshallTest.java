import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FloydWarshallTest {
    private FloydWarshall floydWarshall;
    private Graph graph;

    @Before
    public void setUp() {
        floydWarshall = new FloydWarshall();
        // Configurar un grafo de ejemplo para las pruebas
        String[] cities = {"A", "B", "C", "D"};
        graph = new Graph(cities);
        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 3);
        graph.addEdge("C", "D", 1);
        graph.addEdge("A", "D", 10);
    }

    @Test
    public void testComputeWithNormalGraph() {
        int[][] adjacencyMatrix = graph.getAdjacencyMatrix();
        floydWarshall.compute(adjacencyMatrix);
        
        int[][] distances = floydWarshall.getDistances();
        
        // Verificar algunas distancias conocidas
        assertEquals(0, distances[0][0]); // A -> A
        assertEquals(5, distances[0][1]); // A -> B
        assertEquals(8, distances[0][2]); // A -> C (A->B->C)
        assertEquals(9, distances[0][3]); // A -> D (A->B->C->D)
        assertEquals(10, distances[0][3]); // A -> D (directo)
    }

    @Test
    public void testGetPathWithValidPath() {
        int[][] adjacencyMatrix = graph.getAdjacencyMatrix();
        floydWarshall.compute(adjacencyMatrix);
        
        String path = floydWarshall.getPath(0, 3, graph); // A -> D
        assertEquals("A -> B -> C -> D", path);
    }

    @Test
    public void testGetPathWithNoPath() {
        // Crear un grafo sin conexiones
        String[] cities = {"A", "B", "C"};
        Graph disconnectedGraph = new Graph(cities);
        int[][] matrix = disconnectedGraph.getAdjacencyMatrix();
        floydWarshall.compute(matrix);
        
        String path = floydWarshall.getPath(0, 2, disconnectedGraph); // A -> C
        assertEquals("Sin ruta.", path);
    }

    @Test
    public void testComputeWithSingleNodeGraph() {
        String[] cities = {"A"};
        Graph singleNodeGraph = new Graph(cities);
        int[][] matrix = singleNodeGraph.getAdjacencyMatrix();
        floydWarshall.compute(matrix);
        
        int[][] distances = floydWarshall.getDistances();
        assertEquals(0, distances[0][0]);
    }

    @Test
    public void testComputeWithDisconnectedGraph() {
        String[] cities = {"A", "B", "C"};
        Graph disconnectedGraph = new Graph(cities);
        // No añadir ninguna arista
        int[][] matrix = disconnectedGraph.getAdjacencyMatrix();
        floydWarshall.compute(matrix);
        
        int[][] distances = floydWarshall.getDistances();
        assertEquals(Integer.MAX_VALUE / 2, distances[0][1]); // A -> B sin conexión
        assertEquals(0, distances[0][0]); // A -> A
    }

    @Test
    public void testGetPathSameSourceAndTarget() {
        int[][] adjacencyMatrix = graph.getAdjacencyMatrix();
        floydWarshall.compute(adjacencyMatrix);
        
        String path = floydWarshall.getPath(1, 1, graph); // B -> B
        assertEquals("B", path);
    }

    @Test
    public void testComputeWithNegativeEdges() {
        String[] cities = {"A", "B", "C"};
        Graph graphWithNegatives = new Graph(cities);
        graphWithNegatives.addEdge("A", "B", -1);
        graphWithNegatives.addEdge("B", "C", -2);
        
        int[][] matrix = graphWithNegatives.getAdjacencyMatrix();
        floydWarshall.compute(matrix);
        
        int[][] distances = floydWarshall.getDistances();
        assertEquals(-1, distances[0][1]); // A -> B
        assertEquals(-3, distances[0][2]); // A -> C (A->B->C)
    }
}