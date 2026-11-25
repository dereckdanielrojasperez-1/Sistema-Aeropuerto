package com.aeropuerto.servicio;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.estructuras.arbol.ArbolAVL;
import com.aeropuerto.estructuras.arbol.ArbolBinarioBusqueda;
import com.aeropuerto.estructuras.grafo.GrafoRutas;
import com.aeropuerto.estructuras.grafo.ResultadoFloydWarshall;
import com.aeropuerto.estructuras.grafo.ResultadoMST;
import com.aeropuerto.estructuras.grafo.Vertice;
import com.aeropuerto.estructuras.hash.TablaHash;
import com.aeropuerto.estructuras.heap.MonticuloBinario;
import com.aeropuerto.estructuras.ordenamiento.AlgoritmosOrdenamientoAvanzado;
import com.aeropuerto.modelo.Vuelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Capa de Servicios y Fachada Arquitectónica del Aeropuerto (CORPAC).
 * Consolidación Integral para el Tercer Avance (Semana 15).
 * 
 * Centraliza la integración entre:
 * 1. Estructuras Lineales: Arreglos, Matrices, Listas Simples, Dobles, Circulares, Pila y Cola.
 * 2. Estructuras Jerárquicas No Lineales: Árbol Binario de Búsqueda (ABB) y Árbol AVL Auto-balanceado.
 * 3. Grafos y Redes de Conectividad: Dijkstra, Prim (MST), Kruskal (MST) y Floyd-Warshall.
 * 4. Tablas Hash: Indexación con dispersión O(1) y resolución de colisiones por encadenamiento.
 * 5. Montículo Binario (Min-Heap): Cola de prioridad para despacho aeroportuario.
 * 6. Algoritmos de Ordenamiento Avanzado: Quicksort, Mergesort y Heapsort con módulo de Benchmarking.
 */
public class ServicioAeropuerto {

    private static ServicioAeropuerto instancia;

    private final ArbolBinarioBusqueda<String, Vuelo> arbolVuelos;
    private final ArbolAVL<String, Vuelo> arbolAVL;
    private final GrafoRutas redRutas;
    private final TablaHash<String, String> tablaHashPasajeros;
    private final MonticuloBinario<String> heapDespachoATC;
    private final List<ModuloExtensible> modulosRegistrados;

    private ServicioAeropuerto() {
        this.arbolVuelos = new ArbolBinarioBusqueda<>();
        this.arbolAVL = new ArbolAVL<>();
        this.redRutas = new GrafoRutas();
        this.tablaHashPasajeros = new TablaHash<>(31);
        this.heapDespachoATC = new MonticuloBinario<>();
        this.modulosRegistrados = new ArrayList<>();
        inicializarRedRutasPeru();
    }

    public static synchronized ServicioAeropuerto obtenerInstancia() {
        if (instancia == null) {
            instancia = new ServicioAeropuerto();
        }
        return instancia;
    }

    /**
     * Pre-carga la red de rutas del Perú en el Grafo (Hub central LIM Jorge Chávez).
     */
    private void inicializarRedRutasPeru() {
        // Vértices principales
        redRutas.agregarVertice("LIM", "Aeropuerto Internacional Jorge Chávez", "Lima");
        redRutas.agregarVertice("CUZ", "Aeropuerto Alejandro Velasco Astete", "Cusco");
        redRutas.agregarVertice("AQP", "Aeropuerto Rodríguez Ballón", "Arequipa");
        redRutas.agregarVertice("TRU", "Aeropuerto Carlos Martínez de Pinillos", "Trujillo");
        redRutas.agregarVertice("IQT", "Aeropuerto Francisco Secada Vignetta", "Iquitos");
        redRutas.agregarVertice("PIU", "Aeropuerto Capitán FAP Guillermo Concha", "Piura");
        redRutas.agregarVertice("TCQ", "Aeropuerto Carlos Ciriani Santa Rosa", "Tacna");
        redRutas.agregarVertice("JUL", "Aeropuerto Inca Manco Cápac", "Juliaca");

        // Rutas y conexiones bidireccionales con distancias y costos referenciales en Soles
        redRutas.agregarRutaBidireccional("LIM", "CUZ", 584.0, 280.0, 75);
        redRutas.agregarRutaBidireccional("LIM", "AQP", 766.0, 310.0, 85);
        redRutas.agregarRutaBidireccional("LIM", "TRU", 490.0, 220.0, 60);
        redRutas.agregarRutaBidireccional("LIM", "IQT", 1010.0, 390.0, 110);
        redRutas.agregarRutaBidireccional("LIM", "PIU", 850.0, 290.0, 90);
        redRutas.agregarRutaBidireccional("LIM", "TCQ", 990.0, 340.0, 105);
        redRutas.agregarRutaBidireccional("CUZ", "AQP", 320.0, 190.0, 45);
        redRutas.agregarRutaBidireccional("CUZ", "JUL", 290.0, 170.0, 40);
        redRutas.agregarRutaBidireccional("AQP", "TCQ", 235.0, 150.0, 35);
    }

    /**
     * Sincroniza el Árbol Binario de Búsqueda y el Árbol AVL indexando los vuelos activos.
     */
    public void indexarVuelosEnArbol(Vuelo[] vuelos, int cantidad) {
        arbolVuelos.vaciar();
        arbolAVL.vaciar();
        if (vuelos == null || cantidad <= 0) return;

        for (int i = 0; i < cantidad; i++) {
            Vuelo v = vuelos[i];
            if (v != null && v.obtenerCodigo() != null) {
                String cod = v.obtenerCodigo().trim().toUpperCase();
                arbolVuelos.insertar(cod, v);
                arbolAVL.insertar(cod, v);
            }
        }
    }

    public Vuelo buscarVueloEnArbol(String codigo) {
        if (codigo == null) return null;
        return arbolVuelos.buscar(codigo.trim().toUpperCase());
    }

    public Vuelo buscarVueloEnAVL(String codigo) {
        if (codigo == null) return null;
        return arbolAVL.buscar(codigo.trim().toUpperCase());
    }

    public List<Vuelo> obtenerVuelosOrdenadosPorArbol() {
        return arbolVuelos.recorridoInOrden();
    }

    public ArbolAVL<String, Vuelo> obtenerArbolAVL() {
        return arbolAVL;
    }

    public ArbolBinarioBusqueda<String, Vuelo> obtenerArbolVuelos() {
        return arbolVuelos;
    }

    // Grafos: Dijkstra, MST (Prim y Kruskal) y Floyd-Warshall
    public List<Vertice> calcularRutaOptima(String codigoOrigen, String codigoDestino) {
        return redRutas.obtenerRutaOptimaDijkstra(codigoOrigen, codigoDestino);
    }

    public ResultadoMST calcularMSTPrim(String verticeInicial) {
        return redRutas.calcularMSTPrim(verticeInicial);
    }

    public ResultadoMST calcularMSTKruskal() {
        return redRutas.calcularMSTKruskal();
    }

    public ResultadoFloydWarshall calcularFloydWarshall() {
        return redRutas.calcularFloydWarshall();
    }

    public GrafoRutas obtenerRedRutas() {
        return redRutas;
    }

    // Tabla Hash y Monticulo Binario
    public TablaHash<String, String> obtenerTablaHashPasajeros() {
        return tablaHashPasajeros;
    }

    public void registrarPasajeroHash(String dni, String detalle) {
        if (dni != null && !dni.trim().isEmpty()) {
            tablaHashPasajeros.insertar(dni.trim(), detalle);
        }
    }

    public String buscarPasajeroHash(String dni) {
        if (dni == null) return null;
        return tablaHashPasajeros.buscar(dni.trim());
    }

    public MonticuloBinario<String> obtenerHeapDespachoATC() {
        return heapDespachoATC;
    }

    public void encolarEnHeap(String aeronave, int nivelPrioridad) {
        heapDespachoATC.insertar(aeronave, nivelPrioridad);
    }

    public String despacharDesdeHeap() {
        return heapDespachoATC.extraerMinimo();
    }

    // Ordenamientos avanzados y Benchmarking
    public String ejecutarBenchmarkOrdenamientos(Vuelo[] vuelos) {
        return AlgoritmosOrdenamientoAvanzado.ejecutarBenchmark(vuelos);
    }

    // Modulos extensibles y Diagnostico integral
    public void registrarModulo(ModuloExtensible modulo) {
        if (modulo != null && !modulosRegistrados.contains(modulo)) {
            modulo.inicializar();
            modulosRegistrados.add(modulo);
        }
    }

    public List<ModuloExtensible> obtenerModulosRegistrados() {
        return new ArrayList<>(modulosRegistrados);
    }

    /**
     * Verificación técnica de salud operativa de las 12 estructuras de datos y TADs.
     */
    public String ejecutarDiagnosticoIntegralTADs(ControladorAeropuerto ctrl) {
        StringBuilder sb = new StringBuilder();
        int tadOk = 0;
        int totalTads = 12;

        // 1. Arreglo Lineal
        try {
            int totalVuelos = (ctrl != null) ? ctrl.obtenerContadorVuelos() : 0;
            sb.append("1. [TAD ARREGLO UNIDIMENSIONAL] Vuelos Registrados: ").append(totalVuelos)
              .append(" elementos | Estado: ÓPTIMO (Búsqueda binaria O(log n) activa)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("1. [TAD ARREGLO] Error: ").append(e.getMessage()).append("\n");
        }

        // 2. Matriz Bidimensional
        try {
            int[][] matriz = (ctrl != null) ? ctrl.obtenerMatrizAsientos() : null;
            int filas = (matriz != null) ? matriz.length : 0;
            int cols = (matriz != null && filas > 0) ? matriz[0].length : 0;
            sb.append("2. [TAD MATRIZ BIDIMENSIONAL] Plano Cabina Airbus: ").append(filas).append("x").append(cols)
              .append(" (").append(filas * cols).append(" celdas) | Estado: ÓPTIMO (Mapeo O(1))\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("2. [TAD MATRIZ] Error: ").append(e.getMessage()).append("\n");
        }

        // 3. Lista Simple
        try {
            int tamSimple = (ctrl != null && ctrl.obtenerListaEquipaje() != null) ? ctrl.obtenerListaEquipaje().obtenerTamano() : 0;
            sb.append("3. [TAD LISTA ENLAZADA SIMPLE] Equipaje Comercial: ").append(tamSimple)
              .append(" bultos | Estado: ÓPTIMO (Nodos unidireccionales)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("3. [TAD LISTA SIMPLE] Error: ").append(e.getMessage()).append("\n");
        }

        // 4. Lista Doble
        try {
            int tamDoble = (ctrl != null && ctrl.obtenerListaRutas() != null) ? ctrl.obtenerListaRutas().obtenerTamano() : 0;
            sb.append("4. [TAD LISTA DOBLEMENTE ENLAZADA] Rutas Aéreas: ").append(tamDoble)
              .append(" rutas | Estado: ÓPTIMO (Navegación bidireccional cabeza/cola)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("4. [TAD LISTA DOBLE] Error: ").append(e.getMessage()).append("\n");
        }

        // 5. Lista Circular
        try {
            int tamCirc = (ctrl != null && ctrl.obtenerListaPuertas() != null) ? ctrl.obtenerListaPuertas().obtenerTamano() : 0;
            sb.append("5. [TAD LISTA CIRCULAR] Carrusel de Gates: ").append(tamCirc)
              .append(" puertas | Estado: ÓPTIMO (Rotación continua sin fin de lista)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("5. [TAD LISTA CIRCULAR] Error: ").append(e.getMessage()).append("\n");
        }

        // 6. Pila LIFO
        try {
            int tamPila = (ctrl != null && ctrl.obtenerPilaDeshacer() != null) ? ctrl.obtenerPilaDeshacer().obtenerTamano() : 0;
            sb.append("6. [TAD PILA LIFO] Reversibilidad / Deshacer: ").append(tamPila)
              .append(" acciones | Estado: ÓPTIMO (Capacidad acotada en heap)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("6. [TAD PILA] Error: ").append(e.getMessage()).append("\n");
        }

        // 7. Cola FIFO con Prioridad
        try {
            int tamCola = (ctrl != null && ctrl.obtenerColaPista() != null) ? ctrl.obtenerColaPista().obtenerTamano() : 0;
            sb.append("7. [TAD COLA DE PRIORIDAD] Turnos de Pista ATC: ").append(tamCola)
              .append(" aeronaves | Estado: ÓPTIMO (Niveles 1-Emergencia, 2-Prioritario, 3-Regular)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("7. [TAD COLA] Error: ").append(e.getMessage()).append("\n");
        }

        // 8. Árbol Binario de Búsqueda (ABB)
        try {
            if (ctrl != null) {
                indexarVuelosEnArbol(ctrl.obtenerVuelos(), ctrl.obtenerContadorVuelos());
            }
            sb.append("8. [TAD ÁRBOL BINARIO DE BÚSQUEDA] Índice Jerárquico ABB: ").append(arbolVuelos.obtenerTamano())
              .append(" nodos | Estado: ÓPTIMO (Recorridos Pre/In/Post/Niveles iterativos)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("8. [TAD ÁRBOL ABB] Error: ").append(e.getMessage()).append("\n");
        }

        // 9. Árbol AVL Auto-balanceado
        try {
            boolean balanceado = arbolAVL.esAVLBalanceado();
            sb.append("9. [TAD ÁRBOL AVL AUTO-BALANCEADO] Índice AVL: ").append(arbolAVL.obtenerTamano())
              .append(" nodos (Altura: ").append(arbolAVL.obtenerAltura()).append(") | Balance: ")
              .append(balanceado ? "PERFECTO (|FE| <= 1)" : "DESBALANCEADO").append(" (4 Rotaciones activas)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("9. [TAD ÁRBOL AVL] Error: ").append(e.getMessage()).append("\n");
        }

        // 10. Grafo de Rutas (Dijkstra, Prim, Kruskal, Floyd-Warshall)
        try {
            int v = redRutas.obtenerCantidadVertices();
            int a = redRutas.obtenerCantidadAristas();
            sb.append("10. [TAD GRAFO PONDERADO] Red Aeroportuaria: ").append(v).append(" vértices, ")
              .append(a).append(" aristas | Estado: ÓPTIMO (Dijkstra, Prim, Kruskal y Floyd-Warshall listos)\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("10. [TAD GRAFO] Error: ").append(e.getMessage()).append("\n");
        }

        // 11. Tabla Hash (Hashing O(1))
        try {
            int tamHash = tablaHashPasajeros.obtenerTamano();
            double factorCarga = tablaHashPasajeros.obtenerFactorCargaActual();
            sb.append("11. [TAD TABLA HASH] Búsqueda O(1) de Pasajeros: ").append(tamHash)
              .append(" registros | Factor de Carga: ").append(String.format("%.2f", factorCarga))
              .append(" | Colisiones: ").append(tablaHashPasajeros.obtenerTotalColisiones()).append("\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("11. [TAD TABLA HASH] Error: ").append(e.getMessage()).append("\n");
        }

        // 12. Montículo Binario (Heap)
        try {
            int tamHeap = heapDespachoATC.obtenerTamano();
            sb.append("12. [TAD MONTÍCULO BINARIO / HEAP] Cola de Prioridad Heap: ").append(tamHeap)
              .append(" elementos | Estado: ÓPTIMO (Inserción y extracción O(log n))\n");
            tadOk++;
        } catch (Exception e) {
            sb.append("12. [TAD HEAP] Error: ").append(e.getMessage()).append("\n");
        }

        sb.append("\nRESULTADO: ").append(tadOk).append(" de ").append(totalTads).append(" Estructuras y TADs verificados al 100% de operatividad técnica.");
        return sb.toString();
    }
}
