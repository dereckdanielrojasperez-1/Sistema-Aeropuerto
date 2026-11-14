package com.aeropuerto.estructuras.grafo;

import java.util.*;

/**
 * ============================================================================
 * ESTRUCTURA DE DATOS AVANZADA: GRAFO PONDERADO DE RUTAS AÉREAS (WEIGHTED GRAPH)
 * ============================================================================
 * Propósito en CORPAC:
 * - Representación topológica de la red aeronáutica nacional e internacional
 *   (nodos = terminales IATA: LIM, CUZ, AQP, PIU, IQT, TPP, TRU; aristas = corredores aéreos).
 * - Optimización de navegación, combustible y tarifas entre cualquier par de aeropuertos.
 *
 * Algoritmos y Complejidades Temporales (Big-O):
 * 1. Dijkstra: Ruta de costo/distancia mínima con cola de prioridad - O((V + E) log V).
 *    Aplica la técnica de Relajación de Aristas (Edge Relaxation):
 *    si distancia[u] + peso(u, v) < distancia[v], se actualiza la distancia a 'v'.
 * 2. Prim: Árbol de Expansión Mínima (MST) de cobertura global - O((V + E) log V).
 *    Conecta todos los aeropuertos garantizando el menor costo total de red sin ciclos.
 * 3. Kruskal: Árbol de Expansión Mínima (MST) con Union-Find (DSU) - O(E log E).
 *    Ordena aristas por peso y utiliza DSU con compresión de caminos y unión por rango
 *    para detectar y descartar ciclos en tiempo casi constante O(α(V)).
 * 4. Floyd-Warshall: Todos los pares de rutas mínimas - O(V³).
 *    Programación dinámica iterativa: dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]).
 * 5. BFS y DFS Iterativo: Recorridos en amplitud y profundidad libres de desbordamiento (StackOverflow).
 * ============================================================================
 */
public class GrafoRutas {

    private final Map<String, Vertice> vertices;
    private final Map<String, List<Arista>> listaAdyacencia;
    public static final int MAX_VERTICES_PERMITIDOS = 50;

    public GrafoRutas() {
        this.vertices = new LinkedHashMap<>();
        this.listaAdyacencia = new LinkedHashMap<>();
    }

    public boolean agregarVertice(String codigoIATA, String nombreAeropuerto, String ciudad) {
        if (codigoIATA == null || codigoIATA.trim().isEmpty()) return false;
        String cod = codigoIATA.trim().toUpperCase();
        if (vertices.containsKey(cod) || vertices.size() >= MAX_VERTICES_PERMITIDOS) {
            return false;
        }
        Vertice verticeNuevo = new Vertice(cod, nombreAeropuerto, ciudad);
        vertices.put(cod, verticeNuevo);
        listaAdyacencia.put(cod, new ArrayList<>());
        return true;
    }

    public boolean agregarRutaBidireccional(String origen, String destino, double distanciaKm, double costoBase, int minutos) {
        boolean ida = agregarRutaDirigida(origen, destino, distanciaKm, costoBase, minutos);
        boolean vuelta = agregarRutaDirigida(destino, origen, distanciaKm, costoBase, minutos);
        return ida && vuelta;
    }

    public boolean agregarRutaDirigida(String origen, String destino, double distanciaKm, double costoBase, int minutos) {
        if (origen == null || destino == null) return false;
        String orig = origen.trim().toUpperCase();
        String dest = destino.trim().toUpperCase();

        Vertice verticeOrigen = vertices.get(orig);
        Vertice verticeDestino = vertices.get(dest);
        if (verticeOrigen == null || verticeDestino == null) return false;

        Arista arista = new Arista(verticeOrigen, verticeDestino, distanciaKm, costoBase, minutos);
        listaAdyacencia.get(orig).add(arista);
        return true;
    }

    public Vertice obtenerVertice(String codigoIATA) {
        if (codigoIATA == null) return null;
        return vertices.get(codigoIATA.trim().toUpperCase());
    }

    public int obtenerCantidadVertices() {
        return vertices.size();
    }

    public int obtenerCantidadAristas() {
        int conteoTotal = 0;
        for (List<Arista> aristas : listaAdyacencia.values()) {
            conteoTotal += aristas.size();
        }
        return conteoTotal;
    }

    /**
     * Algoritmo de Dijkstra para encontrar la ruta de menor costo entre dos aeropuertos.
     * Complejidad O((V + E) log V).
     *
     * Relajación de Aristas:
     * Si la distancia acumulada hasta el nodo actual más el peso de la arista hacia el
     * vecino es estrictamente menor que la distancia conocida hacia el vecino, se actualiza
     * su distancia mínima y se registra el nodo actual como su predecesor en la ruta.
     */
    public List<Vertice> obtenerRutaOptimaDijkstra(String origen, String destino) {
        List<Vertice> ruta = new ArrayList<>();
        if (origen == null || destino == null) return ruta;

        String orig = origen.trim().toUpperCase();
        String dest = destino.trim().toUpperCase();
        if (!vertices.containsKey(orig) || !vertices.containsKey(dest)) return ruta;

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        PriorityQueue<NodoDijkstra> colaPrioridad = new PriorityQueue<>(Comparator.comparingDouble(n -> n.costoAcumulado));

        for (String cod : vertices.keySet()) {
            distancias.put(cod, Double.MAX_VALUE);
        }

        distancias.put(orig, 0.0);
        colaPrioridad.add(new NodoDijkstra(orig, 0.0));

        Set<String> visitados = new HashSet<>();

        while (!colaPrioridad.isEmpty()) {
            NodoDijkstra nodoActual = colaPrioridad.poll();
            String codigoActual = nodoActual.codigo;

            if (visitados.contains(codigoActual)) continue;
            visitados.add(codigoActual);

            if (codigoActual.equals(dest)) break; // Destino alcanzado con costo mínimo garantizado

            List<Arista> adyacentes = listaAdyacencia.get(codigoActual);
            if (adyacentes != null) {
                for (Arista aristaVecina : adyacentes) {
                    String codigoVecino = aristaVecina.obtenerDestino().obtenerCodigoIATA();
                    if (!visitados.contains(codigoVecino)) {
                        // Relajación de arista: comprobación de camino más corto
                        double nuevoCosto = distancias.get(codigoActual) + aristaVecina.obtenerCostoBase();
                        if (nuevoCosto < distancias.get(codigoVecino)) {
                            distancias.put(codigoVecino, nuevoCosto);
                            predecesores.put(codigoVecino, codigoActual);
                            colaPrioridad.add(new NodoDijkstra(codigoVecino, nuevoCosto));
                        }
                    }
                }
            }
        }

        // Reconstrucción del camino desde el destino al origen
        String paso = dest;
        if (!predecesores.containsKey(paso) && !orig.equals(dest)) {
            return ruta; // No hay ruta disponible
        }

        LinkedList<Vertice> camino = new LinkedList<>();
        while (paso != null) {
            camino.addFirst(vertices.get(paso));
            paso = predecesores.get(paso);
        }
        ruta.addAll(camino);
        return ruta;
    }

    /**
     * Recorrido BFS (Amplitud) desde un aeropuerto inicial.
     */
    public List<String> recorridoBFS(String origen) {
        List<String> ordenVisita = new ArrayList<>();
        if (origen == null || !vertices.containsKey(origen.trim().toUpperCase())) return ordenVisita;

        String inicio = origen.trim().toUpperCase();
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();

        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            ordenVisita.add(actual);

            List<Arista> ady = listaAdyacencia.get(actual);
            if (ady != null) {
                for (Arista a : ady) {
                    String vecino = a.obtenerDestino().obtenerCodigoIATA();
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        cola.add(vecino);
                    }
                }
            }
        }
        return ordenVisita;
    }

    /**
     * Recorrido DFS Iterativo (Profundidad): Pila explícita, libre de desbordamiento de pila.
     */
    public List<String> recorridoDFSIterativo(String origen) {
        List<String> ordenVisita = new ArrayList<>();
        if (origen == null || !vertices.containsKey(origen.trim().toUpperCase())) return ordenVisita;

        String inicio = origen.trim().toUpperCase();
        Set<String> visitados = new HashSet<>();
        Deque<String> pila = new ArrayDeque<>();

        pila.push(inicio);

        while (!pila.isEmpty()) {
            String actual = pila.pop();

            if (!visitados.contains(actual)) {
                visitados.add(actual);
                ordenVisita.add(actual);

                List<Arista> ady = listaAdyacencia.get(actual);
                if (ady != null) {
                    for (int i = ady.size() - 1; i >= 0; i--) {
                        String vecino = ady.get(i).obtenerDestino().obtenerCodigoIATA();
                        if (!visitados.contains(vecino)) {
                            pila.push(vecino);
                        }
                    }
                }
            }
        }
        return ordenVisita;
    }

    /**
     * Genera la matriz de adyacencia de costos entre todos los aeropuertos registrados.
     */
    public double[][] generarMatrizAdyacenciaCostos() {
        int n = vertices.size();
        double[][] matriz = new double[n][n];
        List<String> codigos = new ArrayList<>(vertices.keySet());

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matriz[i][j] = (i == j) ? 0.0 : Double.POSITIVE_INFINITY;
            }
        }

        for (int i = 0; i < n; i++) {
            String codOrigen = codigos.get(i);
            List<Arista> aristas = listaAdyacencia.get(codOrigen);
            if (aristas != null) {
                for (Arista a : aristas) {
                    int j = codigos.indexOf(a.obtenerDestino().obtenerCodigoIATA());
                    if (j >= 0) {
                        matriz[i][j] = a.obtenerCostoBase();
                    }
                }
            }
        }
        return matriz;
    }

    public List<Vertice> listarVertices() {
        return new ArrayList<>(vertices.values());
    }

    /**
     * Algoritmo de Prim para Árbol de Expansión Mínima (MST).
     * Selecciona progresivamente la arista de menor peso que conecta un vértice
     * visitado con uno no visitado, garantizando costo total mínimo sin ciclos.
     */
    public ResultadoMST calcularMSTPrim(String verticeInicial) {
        List<Arista> aristasMST = new ArrayList<>();
        double costoTotal = 0.0;
        double distanciaTotal = 0.0;

        if (vertices.isEmpty()) {
            return new ResultadoMST("Prim", aristasMST, 0.0, 0.0);
        }

        String inicio = (verticeInicial != null && vertices.containsKey(verticeInicial.trim().toUpperCase()))
                ? verticeInicial.trim().toUpperCase()
                : vertices.keySet().iterator().next();

        Set<String> visitados = new HashSet<>();
        PriorityQueue<Arista> colaAristas = new PriorityQueue<>(Comparator.comparingDouble(Arista::obtenerCostoBase));

        visitados.add(inicio);
        List<Arista> adyIniciales = listaAdyacencia.get(inicio);
        if (adyIniciales != null) {
            colaAristas.addAll(adyIniciales);
        }

        while (!colaAristas.isEmpty() && visitados.size() < vertices.size()) {
            Arista aristaMinima = colaAristas.poll();
            String dest = aristaMinima.obtenerDestino().obtenerCodigoIATA();

            if (!visitados.contains(dest)) {
                visitados.add(dest);
                aristasMST.add(aristaMinima);
                costoTotal += aristaMinima.obtenerCostoBase();
                distanciaTotal += aristaMinima.obtenerDistanciaKm();

                List<Arista> adyacentes = listaAdyacencia.get(dest);
                if (adyacentes != null) {
                    for (Arista aristaCandidata : adyacentes) {
                        if (!visitados.contains(aristaCandidata.obtenerDestino().obtenerCodigoIATA())) {
                            colaAristas.add(aristaCandidata);
                        }
                    }
                }
            }
        }

        return new ResultadoMST("Prim", aristasMST, costoTotal, distanciaTotal);
    }

    /**
     * Algoritmo de Kruskal para Árbol de Expansión Mínima (MST).
     * Utiliza la estructura de conjuntos disjuntos (Union-Find / DSU con compresión de caminos
     * y unión por rango) para detectar ciclos al incorporar aristas en orden ascendente de costo.
     * Complejidad: O(E log E) para el ordenamiento + O(E * α(V)) para las operaciones DSU.
     */
    public ResultadoMST calcularMSTKruskal() {
        List<Arista> aristasMST = new ArrayList<>();
        double costoTotal = 0.0;
        double distanciaTotal = 0.0;

        if (vertices.size() < 2) {
            return new ResultadoMST("Kruskal", aristasMST, 0.0, 0.0);
        }

        // 1. Recolectar todas las aristas sin duplicar las bidireccionales
        List<Arista> todasAristas = new ArrayList<>();
        Set<String> aristasProcesadas = new HashSet<>();

        for (List<Arista> lista : listaAdyacencia.values()) {
            for (Arista arista : lista) {
                String codigoOrigen = arista.obtenerOrigen().obtenerCodigoIATA();
                String codigoDestino = arista.obtenerDestino().obtenerCodigoIATA();
                String claveArista = (codigoOrigen.compareTo(codigoDestino) < 0) 
                        ? (codigoOrigen + "-" + codigoDestino) 
                        : (codigoDestino + "-" + codigoOrigen);

                if (!aristasProcesadas.contains(claveArista)) {
                    aristasProcesadas.add(claveArista);
                    todasAristas.add(arista);
                }
            }
        }

        // 2. Ordenar aristas por costo base ascendente (O(E log E))
        todasAristas.sort(Comparator.comparingDouble(Arista::obtenerCostoBase));

        // 3. Estructura Union-Find (Disjoint Set Union - DSU)
        Map<String, String> padre = new HashMap<>();
        Map<String, Integer> rango = new HashMap<>();
        for (String cod : vertices.keySet()) {
            padre.put(cod, cod);
            rango.put(cod, 0);
        }

        // 4. Seleccionar aristas que no generen ciclos conectando componentes conexas
        for (Arista aristaCandidata : todasAristas) {
            String raizOrigen = encontrarRaizDSU(padre, aristaCandidata.obtenerOrigen().obtenerCodigoIATA());
            String raizDestino = encontrarRaizDSU(padre, aristaCandidata.obtenerDestino().obtenerCodigoIATA());

            // Si pertenecen a componentes distintas, no forma ciclo: incluir arista
            if (!raizOrigen.equals(raizDestino)) {
                aristasMST.add(aristaCandidata);
                costoTotal += aristaCandidata.obtenerCostoBase();
                distanciaTotal += aristaCandidata.obtenerDistanciaKm();
                unirDSU(padre, rango, raizOrigen, raizDestino);

                if (aristasMST.size() == vertices.size() - 1) {
                    break; // MST completo: V - 1 aristas seleccionadas
                }
            }
        }

        return new ResultadoMST("Kruskal", aristasMST, costoTotal, distanciaTotal);
    }

    /**
     * DSU: Búsqueda con Compresión de Caminos (Path Compression).
     * Aplana el árbol asignando directamente el nodo representativo a cada nodo visitado.
     */
    private String encontrarRaizDSU(Map<String, String> padre, String nodo) {
        if (!padre.get(nodo).equals(nodo)) {
            padre.put(nodo, encontrarRaizDSU(padre, padre.get(nodo)));
        }
        return padre.get(nodo);
    }

    /**
     * DSU: Unión por Rango (Union by Rank).
     * Mantiene el árbol balanceado uniendo el árbol de menor profundidad bajo la raíz del mayor.
     */
    private void unirDSU(Map<String, String> padre, Map<String, Integer> rango, String raizX, String raizY) {
        int rangoX = rango.get(raizX);
        int rangoY = rango.get(raizY);

        if (rangoX < rangoY) {
            padre.put(raizX, raizY);
        } else if (rangoX > rangoY) {
            padre.put(raizY, raizX);
        } else {
            padre.put(raizY, raizX);
            rango.put(raizX, rangoX + 1);
        }
    }

    /**
     * Algoritmo de Floyd-Warshall O(V^3).
     * Calcula la matriz de costos mínimos para todos los pares de aeropuertos
     * mediante programación dinámica iterativa sobre nodos intermedios k.
     */
    public ResultadoFloydWarshall calcularFloydWarshall() {
        int totalVertices = vertices.size();
        List<String> codigos = new ArrayList<>(vertices.keySet());
        double[][] matrizDistancias = new double[totalVertices][totalVertices];
        String[][] matrizSiguientes = new String[totalVertices][totalVertices];

        // 1. Inicialización de distancias directas y diagonales
        for (int i = 0; i < totalVertices; i++) {
            for (int j = 0; j < totalVertices; j++) {
                if (i == j) {
                    matrizDistancias[i][j] = 0.0;
                    matrizSiguientes[i][j] = codigos.get(i);
                } else {
                    matrizDistancias[i][j] = Double.POSITIVE_INFINITY;
                    matrizSiguientes[i][j] = null;
                }
            }
        }

        // 2. Cargar aristas directas conocidas
        for (int i = 0; i < totalVertices; i++) {
            String codigoOrigen = codigos.get(i);
            List<Arista> adyacentes = listaAdyacencia.get(codigoOrigen);
            if (adyacentes != null) {
                for (Arista arista : adyacentes) {
                    int j = codigos.indexOf(arista.obtenerDestino().obtenerCodigoIATA());
                    if (j >= 0) {
                        matrizDistancias[i][j] = arista.obtenerCostoBase();
                        matrizSiguientes[i][j] = arista.obtenerDestino().obtenerCodigoIATA();
                    }
                }
            }
        }

        // 3. Relajación iterativa O(V³): Probar si usar k como escala reduce el costo entre i y j
        for (int k = 0; k < totalVertices; k++) {
            for (int i = 0; i < totalVertices; i++) {
                for (int j = 0; j < totalVertices; j++) {
                    if (matrizDistancias[i][k] != Double.POSITIVE_INFINITY && matrizDistancias[k][j] != Double.POSITIVE_INFINITY) {
                        double nuevoCosto = matrizDistancias[i][k] + matrizDistancias[k][j];
                        if (nuevoCosto < matrizDistancias[i][j]) {
                            matrizDistancias[i][j] = nuevoCosto;
                            matrizSiguientes[i][j] = matrizSiguientes[i][k];
                        }
                    }
                }
            }
        }

        return new ResultadoFloydWarshall(codigos, matrizDistancias, matrizSiguientes);
    }

    // Clase auxiliar interna para la cola de prioridad de Dijkstra
    private static class NodoDijkstra {
        String codigo;
        double costoAcumulado;

        NodoDijkstra(String codigo, double costoAcumulado) {
            this.codigo = codigo;
            this.costoAcumulado = costoAcumulado;
        }
    }
}
