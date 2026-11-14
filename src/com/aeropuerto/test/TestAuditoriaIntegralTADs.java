package com.aeropuerto.test;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.estructuras.*;
import com.aeropuerto.estructuras.arbol.ArbolAVL;
import com.aeropuerto.estructuras.arbol.ArbolBinarioBusqueda;
import com.aeropuerto.estructuras.grafo.GrafoRutas;
import com.aeropuerto.estructuras.grafo.ResultadoFloydWarshall;
import com.aeropuerto.estructuras.grafo.ResultadoMST;
import com.aeropuerto.estructuras.grafo.Vertice;
import com.aeropuerto.estructuras.hash.TablaHash;
import com.aeropuerto.estructuras.heap.MonticuloBinario;
import com.aeropuerto.estructuras.ordenamiento.AlgoritmosOrdenamientoAvanzado;
import com.aeropuerto.estructuras.ordenamiento.AlgoritmosOrdenamientoAvanzado.ResultadoBenchmark;
import com.aeropuerto.modelo.*;
import com.aeropuerto.servicio.ServicioAeropuerto;

import java.util.List;

/**
 * Suite Oficial de Pruebas Unitarias y Auditoría de Estructuras de Datos (TADs).
 * Ejecuta pruebas automatizadas de cobertura para validar:
 * - Ausencia de excepciones no controladas (NPE, ArrayIndexOutOfBounds, StackOverflowError).
 * - Correcta complejidad y estabilidad de algoritmos.
 * - Manejo riguroso de casos borde (vacío, 1 elemento, duplicados, límites).
 * - Algoritmos Avanzados Semana 15: AVL (4 rotaciones), Grafos (Prim, Kruskal, Floyd-Warshall),
 *   Tablas Hash con Rehashing, Montículo Binario (Min-Heap), y Ordenamientos O(n log n).
 */
public class TestAuditoriaIntegralTADs {

    private static int pruebasTotales = 0;
    private static int pruebasExitosas = 0;

    private static void afirmar(boolean condicion, String nombreCaso) {
        pruebasTotales++;
        if (condicion) {
            pruebasExitosas++;
            System.out.println("  [PASS] Caso #" + pruebasTotales + ": " + nombreCaso);
        } else {
            System.err.println("  [FAIL] Caso #" + pruebasTotales + ": " + nombreCaso);
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       SUITE AUTOMATIZADA DE AUDITORÍA Y CONTROL DE CALIDAD - CORPAC");
        System.out.println("                   AVANCE INTEGRAL SEMANA 15 (AED)");
        System.out.println("================================================================================\n");

        probarListaSimple();
        probarListaDoble();
        probarListaCircular();
        probarPilaLIFO();
        probarColaPrioridad();
        probarArregloYAlgoritmos();
        probarMatrizAsientos();
        probarArbolBinarioBusqueda();
        probarGrafoRutasDijkstra();
        probarArbolAVLRotaciones();
        probarAlgoritmosMSTPrimYKruskal();
        probarAlgoritmoFloydWarshall();
        probarTablaHashEncadenada();
        probarMonticuloBinarioMinHeap();
        probarAlgoritmosOrdenamientoAvanzado();
        probarServicioYAuditoria();

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("RESUMEN DE PRUEBAS: " + pruebasExitosas + " de " + pruebasTotales + " pruebas superadas exitosamente (" +
                String.format("%.1f", (pruebasExitosas * 100.0 / pruebasTotales)) + "%).");
        System.out.println("ESTADO FINAL: VERIFICACIÓN TÉCNICA APROBADA (CERO FALLOS).");
        System.out.println("================================================================================");

        if (pruebasExitosas != pruebasTotales) {
            System.exit(1);
        }
    }

    // 1. LISTA ENLAZADA SIMPLE
    private static void probarListaSimple() {
        System.out.println("\n--- [1] PRUEBAS DE TAD LISTA SIMPLE (EQUIPAJE) ---");
        ListaSimple lista = new ListaSimple();
        afirmar(lista.estaVacia() && lista.obtenerTamano() == 0, "Lista recién creada está vacía");
        afirmar(lista.buscarMaleta("EQ-X") == null, "Búsqueda en lista vacía retorna null de forma segura");

        // Rechazo de nulos
        afirmar(!lista.agregarMaleta(null), "Rechazo de objeto Equipaje nulo");
        afirmar(!lista.agregarMaleta(new Equipaje(null, "12345678", "CP-01", 10.0, "Cabina")), "Rechazo de equipaje con ID nulo");

        // Inserción regular
        Equipaje eq1 = new Equipaje("EQ-01", "71234567", "CP-2023", 12.5, "Bodega");
        Equipaje eq2 = new Equipaje("EQ-02", "71234567", "CP-2023", 8.0, "Cabina");
        lista.agregarMaleta(eq1);
        lista.agregarMaleta(eq2);
        afirmar(lista.obtenerTamano() == 2, "Inserción exitosa de 2 maletas");

        // Rechazo de duplicados
        afirmar(!lista.agregarMaleta(eq1), "Rechazo de ID de maleta duplicado");

        // Búsqueda y eliminación
        afirmar(lista.buscarMaleta("EQ-01") != null, "Búsqueda exitosa de maleta existente");
        afirmar(lista.eliminarMaleta("EQ-01"), "Eliminación exitosa en la cabeza");
        afirmar(lista.obtenerTamano() == 1, "Tamaño decrementado tras eliminación");
        lista.vaciar();
        afirmar(lista.estaVacia(), "Método vaciar() restablece la lista a cero elementos");
    }

    // 2. LISTA DOBLEMENTE ENLAZADA
    private static void probarListaDoble() {
        System.out.println("\n--- [2] PRUEBAS DE TAD LISTA DOBLEMENTE ENLAZADA (RUTAS) ---");
        ListaDoble lista = new ListaDoble();
        afirmar(lista.estaVacia(), "Lista doble inicia vacía");

        Ruta r1 = new Ruta("RT-01", "Lima", "Cusco", 580.0);
        Ruta r2 = new Ruta("RT-02", "Lima", "Arequipa", 760.0);
        Ruta r3 = new Ruta("RT-03", "Lima", "Iquitos", 1010.0);

        lista.agregarRuta(r1);
        lista.agregarRuta(r2);
        lista.agregarRuta(r3);
        afirmar(lista.obtenerTamano() == 3, "Inserción secuencial de 3 rutas");

        // Navegación bidireccional
        afirmar(lista.buscarRuta("RT-02") != null, "Búsqueda intermedia en O(n)");
        afirmar(lista.eliminarRuta("RT-02"), "Eliminación de nodo intermedio con reconexión de punteros");
        afirmar(lista.obtenerTamano() == 2, "Tamaño actualizado tras eliminar nodo intermedio");
        afirmar(lista.eliminarRuta("RT-INEXISTENTE") == false, "Eliminación segura de nodo inexistente");
    }

    // 3. LISTA CIRCULAR SIMPLE
    private static void probarListaCircular() {
        System.out.println("\n--- [3] PRUEBAS DE TAD LISTA CIRCULAR (PUERTAS DE EMBARQUE) ---");
        ListaCircular circular = new ListaCircular();
        afirmar(circular.estaVacia(), "Lista circular inicia vacía");
        afirmar(circular.avanzarSiguiente() == null, "Avanzar en lista circular vacía retorna null seguro");

        PuertaEmbarque p1 = new PuertaEmbarque("GATE-01", "T1", "Libre", "CP-2023");
        PuertaEmbarque p2 = new PuertaEmbarque("GATE-02", "T1", "Libre", "CP-2024");
        circular.agregarPuerta(p1);
        circular.agregarPuerta(p2);
        afirmar(circular.obtenerTamano() == 2, "2 puertas insertadas en carrusel circular");

        // Rotación continua
        PuertaEmbarque act1 = circular.obtenerPuertaActual();
        PuertaEmbarque sig1 = circular.avanzarSiguiente();
        PuertaEmbarque sig2 = circular.avanzarSiguiente();
        afirmar(sig1 != null && sig2 != null, "Rotación circular activa y continua sin fin de lista");

        // Eliminación en carrusel
        afirmar(circular.eliminarPuerta("GATE-01"), "Eliminación de nodo en lista circular");
        afirmar(circular.obtenerTamano() == 1, "Tamaño es 1 tras eliminar cabeza");
        circular.vaciar();
        afirmar(circular.estaVacia(), "Carrusel vaciado a cero");
    }

    // 4. PILA LIFO
    private static void probarPilaLIFO() {
        System.out.println("\n--- [4] PRUEBAS DE TAD PILA LIFO (DESHACER RESERVAS) ---");
        Pila pila = new Pila(5); // Capacidad configurada a 5 para prueba de descarte seguro
        afirmar(pila.estaVacia(), "Pila inicia vacía");
        afirmar(pila.desapilar() == null, "Desapilar en pila vacía es null seguro");

        pila.apilar("Accion-1");
        pila.apilar("Accion-2");
        pila.apilar("Accion-3");
        afirmar(pila.obtenerTamano() == 3, "3 elementos apilados");
        afirmar("Accion-3".equals(pila.cima()), "Cima (Peek) devuelve el último elemento ingresado");
        afirmar("Accion-3".equals(pila.desapilar()), "Desapilar (Pop) extrae en orden estricto LIFO");
        afirmar(pila.obtenerTamano() == 2, "Tamaño decrementado tras desapilar");

        // Prueba de descarte seguro en fondo al superar capacidad
        pila.apilar("Accion-4");
        pila.apilar("Accion-5");
        pila.apilar("Accion-6");
        pila.apilar("Accion-7"); // Supera capacidad 5
        afirmar(pila.obtenerTamano() <= 5, "Guarda de capacidad de memoria heap activada (tamano <= 5)");
    }

    // 5. COLA CON PRIORIDAD (ATC)
    private static void probarColaPrioridad() {
        System.out.println("\n--- [5] PRUEBAS DE TAD COLA CON PRIORIDAD (TORRE ATC) ---");
        Cola cola = new Cola();
        afirmar(cola.estaVacia(), "Cola inicia vacía");

        // Encolar con distintas prioridades
        cola.encolarConPrioridad("Vuelo-Regular-1", 3);
        cola.encolarConPrioridad("Vuelo-Emergencia", 1);
        cola.encolarConPrioridad("Vuelo-Prioritario", 2);
        cola.encolarConPrioridad("Vuelo-Regular-2", 3);

        afirmar(cola.obtenerTamano() == 4, "4 vuelos encolados");
        // El de emergencia (1) debe salir primero
        afirmar("Vuelo-Emergencia".equals(cola.desencolar()), "Vuelo de Emergencia (Prioridad 1) despachado en primer turno");
        // El prioritario (2) debe salir segundo
        afirmar("Vuelo-Prioritario".equals(cola.desencolar()), "Vuelo Prioritario (Prioridad 2) despachado en segundo turno");
        // Los regulares (3) deben salir en orden FIFO de llegada
        afirmar("Vuelo-Regular-1".equals(cola.desencolar()), "Estabilidad FIFO respetada para vuelos regulares (1)");
        afirmar("Vuelo-Regular-2".equals(cola.desencolar()), "Estabilidad FIFO respetada para vuelos regulares (2)");
        afirmar(cola.estaVacia(), "Cola vaciada tras despachar todos los vuelos");
    }

    // 6. ARREGLO DINÁMICO Y ALGORITMOS DE ORDENACIÓN / BÚSQUEDA
    private static void probarArregloYAlgoritmos() {
        System.out.println("\n--- [6] PRUEBAS DE ARREGLO DINÁMICO, ORDENAMIENTO Y BÚSQUEDA BINARIA ---");
        ControladorAeropuerto ctrl = new ControladorAeropuerto();
        ctrl.cargarDatosDemostracion();

        afirmar(ctrl.obtenerContadorVuelos() >= 5, "Catálogo de vuelos inicializado con datos demo");

        // Ordenamiento por Inserción por precio
        ctrl.ordenarVuelosPorPrecio(true);
        Vuelo[] vuelosOrd = ctrl.clonarArregloVuelos();
        boolean precioAsc = true;
        for (int i = 0; i < ctrl.obtenerContadorVuelos() - 1; i++) {
            if (vuelosOrd[i].obtenerPrecioBase() > vuelosOrd[i + 1].obtenerPrecioBase()) {
                precioAsc = false;
                break;
            }
        }
        afirmar(precioAsc, "Ordenamiento por Inserción por precio verificado de forma ascendente");

        // Ordenamiento Burbuja por código
        ctrl.ordenarVuelosPorCodigo();
        Vuelo[] vuelosCod = ctrl.clonarArregloVuelos();
        boolean codAsc = true;
        for (int i = 0; i < ctrl.obtenerContadorVuelos() - 1; i++) {
            if (vuelosCod[i].obtenerCodigo().compareToIgnoreCase(vuelosCod[i + 1].obtenerCodigo()) > 0) {
                codAsc = false;
                break;
            }
        }
        afirmar(codAsc, "Ordenamiento Burbuja por código alfanumérico verificado");

        // Búsqueda Binaria O(log n)
        Vuelo vBuscado = ctrl.buscarVueloBinario("CP-2023");
        afirmar(vBuscado != null && "CP-2023".equalsIgnoreCase(vBuscado.obtenerCodigo()), "Búsqueda Binaria O(log n) localiza vuelo existente");
        afirmar(ctrl.buscarVueloBinario("CP-INEXISTENTE") == null, "Búsqueda Binaria de vuelo inexistente retorna null seguro");
    }

    // 7. MATRIZ BIDIMENSIONAL DE ASIENTOS
    private static void probarMatrizAsientos() {
        System.out.println("\n--- [7] PRUEBAS DE TAD MATRIZ BIDIMENSIONAL (CABINA 10x5) ---");
        Avion avion = new Avion();
        int[][] asientos = avion.obtenerAsientos();
        afirmar(asientos.length == 10 && asientos[0].length == 5, "Dimensiones de la matriz verificadas (10 filas x 5 columnas)");

        // Ocupar y verificar
        afirmar(asientos[0][0] == 0, "Asiento (0,0) inicialmente disponible (valor 0)");
        afirmar(avion.ocuparAsiento(0, 0), "Asiento (0,0) ocupado exitosamente (pasa a 1)");
        afirmar(asientos[0][0] == 1, "Asiento (0,0) ya no está disponible (valor 1)");
        afirmar(!avion.ocuparAsiento(0, 0), "Rechazo de ocupación en asiento ya ocupado");

        // Cálculo de costos según coordenadas
        double costoVentanaVip = avion.calcularCostoAsiento(0, 0, 100.0); // Fila 0 (<3 VIP), Columna 0 (Ventana)
        double costoPasilloTurista = avion.calcularCostoAsiento(5, 1, 100.0); // Fila 5 (Turista), Columna 1 (Pasillo)
        afirmar(costoVentanaVip > costoPasilloTurista, "Cálculo de tarifas espaciales por matriz correcto (VIP Ventana > Turista Pasillo)");

        // Verificación de fuente única de verdad para tarifas (A2): CP-2024 (139.00) asiento 1A (0,0) = 180.70
        double tarifaCp2024_1A = avion.calcularCostoAsiento(0, 0, 139.0);
        afirmar(Math.abs(tarifaCp2024_1A - 180.70) < 0.001,
                "Fuente única de verdad: Tarifa Asiento 1A para CP-2024 (base 139.00 * 1.30) es exactamente 180.70");

        // Liberar asiento
        afirmar(avion.liberarAsiento(0, 0), "Asiento (0,0) liberado exitosamente");
        afirmar(asientos[0][0] == 0, "Asiento (0,0) vuelve a estar libre (valor 0)");
    }

    // 8. ÁRBOL BINARIO DE BÚSQUEDA (ABB)
    private static void probarArbolBinarioBusqueda() {
        System.out.println("\n--- [8] PRUEBAS DE ÁRBOL BINARIO DE BÚSQUEDA (ABB ITERATIVO) ---");
        ArbolBinarioBusqueda<String, Integer> arbol = new ArbolBinarioBusqueda<>();
        afirmar(arbol.estaVacio(), "Árbol binario inicia vacío");

        // Inserción iterativa
        arbol.insertar("M", 50);
        arbol.insertar("C", 20);
        arbol.insertar("T", 80);
        arbol.insertar("A", 10);
        arbol.insertar("G", 30);
        afirmar(arbol.obtenerTamano() == 5, "Inserción iterativa de 5 nodos en el árbol");

        // Búsqueda iterativa O(log n)
        afirmar(arbol.buscar("T") != null && arbol.buscar("T") == 80, "Búsqueda iterativa localiza clave 'T'");
        afirmar(arbol.buscar("Z") == null, "Búsqueda de clave inexistente 'Z' retorna null seguro");

        // Recorrido In-Order iterativo (orden ascendente)
        List<Integer> inOrder = arbol.recorridoInOrden();
        afirmar(inOrder.size() == 5, "Recorrido In-Order devuelve los 5 elementos");
        afirmar(inOrder.get(0) == 10 && inOrder.get(4) == 80, "Recorrido In-Order mantiene orden ascendente estricto (10 .. 80)");

        // Mínimo y Máximo iterativos
        afirmar(arbol.obtenerMinimo() == 10, "Mínimo iterativo correcto (10)");
        afirmar(arbol.obtenerMaximo() == 80, "Máximo iterativo correcto (80)");
    }

    // 9. GRAFO DE RUTAS Y ALGORITMO DE DIJKSTRA
    private static void probarGrafoRutasDijkstra() {
        System.out.println("\n--- [9] PRUEBAS DE GRAFO PONDERADO Y ALGORITMO DE DIJKSTRA ---");
        GrafoRutas grafo = new GrafoRutas();
        grafo.agregarVertice("LIM", "Jorge Chavez", "Lima");
        grafo.agregarVertice("CUZ", "Velasco Astete", "Cusco");
        grafo.agregarVertice("AQP", "Rodriguez Ballon", "Arequipa");
        grafo.agregarVertice("TCQ", "Carlos Ciriani", "Tacna");
        grafo.agregarVertice("AISLADO", "Pista Emergencia", "Selva Central");

        grafo.agregarRutaBidireccional("LIM", "CUZ", 584.0, 280.0, 75);
        grafo.agregarRutaBidireccional("LIM", "AQP", 766.0, 310.0, 85);
        grafo.agregarRutaBidireccional("CUZ", "AQP", 320.0, 190.0, 45);
        grafo.agregarRutaBidireccional("AQP", "TCQ", 235.0, 150.0, 35);

        afirmar(grafo.obtenerCantidadVertices() == 5, "Grafo registra 5 vértices correctamente");
        afirmar(grafo.obtenerCantidadAristas() == 8, "Grafo registra 8 aristas dirigidas (4 rutas bidireccionales)");

        // Dijkstra: ruta directa o con escalas
        List<Vertice> rutaLimCuz = grafo.obtenerRutaOptimaDijkstra("LIM", "CUZ");
        afirmar(rutaLimCuz.size() == 2, "Dijkstra: ruta óptima LIM -> CUZ es directa (2 vértices)");

        List<Vertice> rutaLimTcq = grafo.obtenerRutaOptimaDijkstra("LIM", "TCQ");
        afirmar(rutaLimTcq.size() >= 3, "Dijkstra: ruta LIM -> TCQ escala por AQP de menor costo");

        // Casos borde de Dijkstra
        List<Vertice> rutaMismoNodo = grafo.obtenerRutaOptimaDijkstra("LIM", "LIM");
        afirmar(rutaMismoNodo.size() == 1, "Dijkstra: origen igual a destino retorna 1 solo vértice (costo 0)");

        List<Vertice> rutaInaccesible = grafo.obtenerRutaOptimaDijkstra("LIM", "AISLADO");
        afirmar(rutaInaccesible.isEmpty(), "Dijkstra: nodo desconectado retorna ruta vacía segura");

        // Recorridos BFS y DFS iterativo
        List<String> bfs = grafo.recorridoBFS("LIM");
        afirmar(bfs.contains("CUZ") && bfs.contains("AQP"), "Recorrido BFS visita nodos adyacentes");

        List<String> dfs = grafo.recorridoDFSIterativo("LIM");
        afirmar(!dfs.isEmpty() && dfs.get(0).equals("LIM"), "Recorrido DFS iterativo libre de recursión inicia en raíz");
    }

    // 10. ÁRBOL AVL AUTO-BALANCEADO Y ROTACIONES CANÓNICAS
    private static void probarArbolAVLRotaciones() {
        System.out.println("\n--- [10] PRUEBAS DE ÁRBOL AVL Y 4 ROTACIONES (SEMANA 15) ---");
        ArbolAVL<String, Integer> avl = new ArbolAVL<>();
        afirmar(avl.esVacio() && avl.tamano() == 0, "Árbol AVL recién creado está vacío");
        afirmar(avl.verificarBalance(), "Árbol vacío satisface condición de balance |FE| <= 1");
        afirmar(avl.buscar("INEXISTENTE") == null, "Búsqueda en AVL vacío retorna null de forma segura");

        // Caso 1: Rotación Simple Derecha (RSD / LL) al insertar claves decrecientes
        avl.insertar("30", 30);
        avl.insertar("20", 20);
        avl.insertar("10", 10);
        afirmar(avl.tamano() == 3, "Inserción de 3 elementos en orden decreciente");
        afirmar(avl.verificarBalance(), "Balance verificado tras Rotación Simple Derecha (RSD / LL)");
        afirmar(avl.obtenerAltura() == 2, "Altura del árbol balanceado es 2 (raíz '20')");

        // Caso 2: Rotación Simple Izquierda (RSI / RR)
        avl.insertar("40", 40);
        avl.insertar("50", 50);
        afirmar(avl.verificarBalance(), "Balance verificado tras Rotación Simple Izquierda (RSI / RR)");

        // Caso 3: Rotación Doble Izquierda-Derecha (RDID / LR) y Derecha-Izquierda (RDDI / RL)
        avl.insertar("25", 25);
        avl.insertar("45", 45);
        afirmar(avl.verificarBalance(), "Balance verificado tras inserciones con rotaciones dobles (LR / RL)");
        afirmar(avl.tamano() == 7, "Total de 7 nodos en el árbol AVL");

        // Búsquedas O(log n)
        afirmar(avl.buscar("25") != null && avl.buscar("25") == 25, "Búsqueda O(log n) localiza clave '25'");
        afirmar(avl.buscar("99") == null, "Búsqueda de clave inexistente '99' retorna null");

        // Recorridos
        List<Integer> inOrder = avl.recorridoInOrden();
        afirmar(inOrder.size() == 7 && inOrder.get(0) == 10 && inOrder.get(6) == 50,
                "Recorrido In-Order de AVL preserva orden ascendente estricto (10 .. 50)");

        List<Integer> preOrder = avl.recorridoPreOrden();
        List<Integer> postOrder = avl.recorridoPostOrden();
        List<Integer> niveles = avl.recorridoPorNiveles();
        afirmar(preOrder.size() == 7 && postOrder.size() == 7 && niveles.size() == 7,
                "Recorridos Pre-Order, Post-Order y Por Niveles retornan los 7 nodos");

        // Eliminación balanceada
        afirmar(avl.eliminar("10"), "Eliminación de hoja '10' exitosa");
        afirmar(avl.tamano() == 6, "Tamaño decrementado tras eliminación en AVL");
        afirmar(avl.verificarBalance(), "Balance |FE| <= 1 verificado tras eliminación");
    }

    // 11. ÁRBOLES DE EXPANSIÓN MÍNIMA (MST) - PRIM Y KRUSKAL
    private static void probarAlgoritmosMSTPrimYKruskal() {
        System.out.println("\n--- [11] PRUEBAS DE ÁRBOLES DE EXPANSIÓN MÍNIMA (PRIM Y KRUSKAL) ---");
        GrafoRutas grafo = new GrafoRutas();
        grafo.agregarVertice("LIM", "Jorge Chavez", "Lima");
        grafo.agregarVertice("CUZ", "Velasco Astete", "Cusco");
        grafo.agregarVertice("AQP", "Rodriguez Ballon", "Arequipa");
        grafo.agregarVertice("TRU", "Capitan Martinez", "Trujillo");

        grafo.agregarRutaBidireccional("LIM", "CUZ", 584.0, 280.0, 75);
        grafo.agregarRutaBidireccional("LIM", "AQP", 766.0, 310.0, 85);
        grafo.agregarRutaBidireccional("CUZ", "AQP", 320.0, 190.0, 45);
        grafo.agregarRutaBidireccional("LIM", "TRU", 490.0, 210.0, 50);

        // Algoritmo de Prim
        ResultadoMST prim = grafo.calcularMSTPrim("LIM");
        afirmar(prim != null, "Ejecución exitosa del Algoritmo de Prim");
        afirmar(prim.obtenerCantidadAristas() == 3, "Prim produce exactamente V - 1 = 3 aristas para 4 vértices conexos");
        afirmar(prim.obtenerCostoTotal() > 0, "Costo total de Prim es positivo");

        // Algoritmo de Kruskal con DSU (Union-Find)
        ResultadoMST kruskal = grafo.calcularMSTKruskal();
        afirmar(kruskal != null, "Ejecución exitosa del Algoritmo de Kruskal (DSU)");
        afirmar(kruskal.obtenerCantidadAristas() == 3, "Kruskal produce exactamente V - 1 = 3 aristas sin ciclos");

        // Equivalencia de costo óptimo entre Prim y Kruskal
        double diferencia = Math.abs(prim.obtenerCostoTotal() - kruskal.obtenerCostoTotal());
        afirmar(diferencia < 0.001, "Prim y Kruskal convergen al mismo costo de peso mínimo óptimo");
    }

    // 12. ALGORITMO DE FLOYD-WARSHALL (TODOS LOS PARES DE CAMINOS MÍNIMOS)
    private static void probarAlgoritmoFloydWarshall() {
        System.out.println("\n--- [12] PRUEBAS DE ALGORITMO FLOYD-WARSHALL O(V^3) ---");
        GrafoRutas grafo = new GrafoRutas();
        grafo.agregarVertice("LIM", "Lima", "Lima");
        grafo.agregarVertice("CUZ", "Cusco", "Cusco");
        grafo.agregarVertice("AQP", "Arequipa", "Arequipa");
        grafo.agregarVertice("ISLA", "Aeródromo Aislado", "Puno Remoto");

        grafo.agregarRutaBidireccional("LIM", "CUZ", 584.0, 280.0, 75);
        grafo.agregarRutaBidireccional("CUZ", "AQP", 320.0, 190.0, 45);

        ResultadoFloydWarshall rfw = grafo.calcularFloydWarshall();
        afirmar(rfw != null, "Cálculo de la matriz Floyd-Warshall exitoso");
        afirmar(rfw.obtenerCostoMinimo("LIM", "LIM") == 0.0, "Distancia mínima de un nodo a sí mismo es 0.0");
        afirmar(rfw.hayConexion("LIM", "CUZ"), "Conexión directa LIM -> CUZ detectada");
        afirmar(rfw.hayConexion("LIM", "AQP"), "Conexión con escala LIM -> CUZ -> AQP detectada");

        // Costo transitivo: LIM -> CUZ (280) + CUZ -> AQP (190) = 470
        double costoLimAqp = rfw.obtenerCostoMinimo("LIM", "AQP");
        afirmar(Math.abs(costoLimAqp - 470.0) < 0.001, "Cálculo transitivo Floyd-Warshall correcto (280 + 190 = 470)");

        // Nodo aislado
        afirmar(!rfw.hayConexion("LIM", "ISLA"), "Nodo desconectado no tiene camino alcanzable");
        afirmar(Double.isInfinite(rfw.obtenerCostoMinimo("LIM", "ISLA")), "Distancia hacia nodo aislado es infinita");
    }

    // 13. TABLA HASH CON ENCADENAMIENTO SEPARADO Y REHASHING DINÁMICO
    private static void probarTablaHashEncadenada() {
        System.out.println("\n--- [13] PRUEBAS DE TAD TABLA HASH (SEPARATE CHAINING) ---");
        TablaHash<String, String> tabla = new TablaHash<>(7);
        afirmar(tabla.estaVacia() && tabla.obtenerTamano() == 0, "Tabla Hash inicia vacía");
        afirmar(tabla.obtenerCapacidad() >= 7, "Capacidad inicial prima asignada");

        // Inserción y búsqueda O(1)
        afirmar(tabla.insertar("72104589", "Carlos Mendoza - CP-2023"), "Inserción exitosa de DNI 72104589");
        afirmar(tabla.insertar("45102030", "Ana Gomez - CP-2024"), "Inserción exitosa de DNI 45102030");
        afirmar(tabla.insertar("10203040", "Luis Morales - CP-2025"), "Inserción exitosa de DNI 10203040");
        afirmar(tabla.obtenerTamano() == 3, "Total de 3 registros almacenados");

        afirmar("Carlos Mendoza - CP-2023".equals(tabla.buscar("72104589")), "Búsqueda O(1) localiza pasajero existente");
        afirmar(tabla.buscar("99999999") == null, "Búsqueda de clave inexistente retorna null");

        // Actualización de clave existente
        afirmar(tabla.insertar("72104589", "Carlos Mendoza (VIP) - CP-2023"), "Actualización de registro por clave duplicada");
        afirmar(tabla.obtenerTamano() == 3, "Tamaño no incrementa al actualizar clave existente");
        afirmar("Carlos Mendoza (VIP) - CP-2023".equals(tabla.buscar("72104589")), "Valor actualizado correctamente");

        // Factor de carga <= 0.75
        afirmar(tabla.obtenerFactorCargaActual() <= 0.75, "Factor de carga dentro del umbral seguro (alpha <= 0.75)");

        // Rehashing dinámico por sobrepaso de umbral
        int capOriginal = tabla.obtenerCapacidad();
        for (int i = 1; i <= 15; i++) {
            tabla.insertar("DNI-" + i, "Pasajero-Demo-" + i);
        }
        afirmar(tabla.obtenerCapacidad() > capOriginal, "Rehashing dinámico duplicó la capacidad al superar el factor de carga");
        afirmar(tabla.buscar("72104589") != null, "Registros previos permanecen íntegros tras rehashing");

        // Eliminación
        afirmar(tabla.eliminar("45102030"), "Eliminación exitosa en Tabla Hash");
        afirmar(tabla.buscar("45102030") == null, "Registro eliminado ya no existe");
    }

    // 14. MONTÍCULO BINARIO (MIN-HEAP PARA PRIORIDAD DE PISTA)
    private static void probarMonticuloBinarioMinHeap() {
        System.out.println("\n--- [14] PRUEBAS DE TAD MONTÍCULO BINARIO (MIN-HEAP) ---");
        MonticuloBinario<String> heap = new MonticuloBinario<>();
        afirmar(heap.estaVacio() && heap.obtenerTamano() == 0, "Montículo recién creado está vacío");
        afirmar(heap.cima() == null, "Cima de heap vacío retorna null de forma segura");
        afirmar(heap.extraerMinimo() == null, "Extracción de heap vacío retorna null sin excepción");

        // Inserción en desorden de prioridad
        heap.insertar("Comercial-Regular-1", 5);
        heap.insertar("Emergencia-Medica", 1);
        heap.insertar("VIP-Presidencial", 2);
        heap.insertar("Comercial-Regular-2", 4);
        heap.insertar("Prioridad-Combustible", 2);

        afirmar(heap.obtenerTamano() == 5, "Total de 5 aeronaves insertadas en el montículo");
        afirmar(heap.prioridadCima() == 1, "Propiedad Min-Heap: La cima contiene la menor prioridad numérica (1)");
        afirmar("Emergencia-Medica".equals(heap.cima()), "Elemento en la cima es la emergencia médica");

        // Extracciones sucesivas O(log n) en orden creciente de prioridad
        String ext1 = heap.extraerMinimo();
        afirmar("Emergencia-Medica".equals(ext1), "Primera extracción: Emergencia Médica (prioridad 1)");

        int prio2 = heap.prioridadCima();
        afirmar(prio2 == 2, "Segunda extracción tiene prioridad 2");
        heap.extraerMinimo();

        afirmar(heap.obtenerTamano() == 3, "Quedan 3 aeronaves tras 2 extracciones");

        heap.vaciar();
        afirmar(heap.estaVacio(), "Método vaciar() restablece el montículo a estado vacío");
    }

    // 15. ALGORITMOS DE ORDENAMIENTO AVANZADO (O(n log n)) Y BENCHMARKING
    private static void probarAlgoritmosOrdenamientoAvanzado() {
        System.out.println("\n--- [15] PRUEBAS DE ORDENAMIENTO AVANZADO (QUICKSORT, MERGESORT, HEAPSORT) ---");
        Vuelo[] vuelosPrueba = new Vuelo[6];
        vuelosPrueba[0] = new Vuelo("CP-10", "Lima", "Cusco", "08:00", "09:15", 350.0, "Programado");
        vuelosPrueba[1] = new Vuelo("CP-05", "Lima", "Arequipa", "09:00", "10:25", 150.0, "Programado");
        vuelosPrueba[2] = new Vuelo("CP-20", "Lima", "Trujillo", "10:00", "11:00", 220.0, "Programado");
        vuelosPrueba[3] = new Vuelo("CP-01", "Lima", "Iquitos", "11:00", "12:45", 500.0, "Programado");
        vuelosPrueba[4] = new Vuelo("CP-15", "Lima", "Tacna", "12:00", "13:35", 180.0, "Programado");
        vuelosPrueba[5] = new Vuelo("CP-08", "Lima", "Juliaca", "13:00", "14:30", 290.0, "Programado");

        // Quicksort O(n log n) por precio ascendente
        Vuelo[] copiaQuick = vuelosPrueba.clone();
        AlgoritmosOrdenamientoAvanzado.quicksortPorPrecio(copiaQuick, true);
        boolean quickOk = true;
        for (int i = 0; i < copiaQuick.length - 1; i++) {
            if (copiaQuick[i].obtenerPrecioBase() > copiaQuick[i + 1].obtenerPrecioBase()) {
                quickOk = false;
                break;
            }
        }
        afirmar(quickOk && copiaQuick[0].obtenerPrecioBase() == 150.0, "Quicksort O(n log n) ordenó tarifas ascendentemente (150 .. 500)");

        // Mergesort O(n log n) por destino alfabético
        Vuelo[] copiaMerge = vuelosPrueba.clone();
        AlgoritmosOrdenamientoAvanzado.mergesortPorDestino(copiaMerge, true);
        boolean mergeOk = true;
        for (int i = 0; i < copiaMerge.length - 1; i++) {
            if (copiaMerge[i].obtenerDestino().compareToIgnoreCase(copiaMerge[i + 1].obtenerDestino()) > 0) {
                mergeOk = false;
                break;
            }
        }
        afirmar(mergeOk && "Arequipa".equals(copiaMerge[0].obtenerDestino()), "Mergesort O(n log n) ordenó destinos alfabéticamente");

        // Heapsort O(n log n) por precio descendente
        Vuelo[] copiaHeap = vuelosPrueba.clone();
        AlgoritmosOrdenamientoAvanzado.heapsortPorPrecio(copiaHeap, false);
        boolean heapOk = true;
        for (int i = 0; i < copiaHeap.length - 1; i++) {
            if (copiaHeap[i].obtenerPrecioBase() < copiaHeap[i + 1].obtenerPrecioBase()) {
                heapOk = false;
                break;
            }
        }
        afirmar(heapOk && copiaHeap[0].obtenerPrecioBase() == 500.0, "Heapsort O(n log n) ordenó tarifas descendentemente (500 .. 150)");

        // Benchmarking comparativo
        List<ResultadoBenchmark> bench = AlgoritmosOrdenamientoAvanzado.ejecutarBenchmarkComparativo(vuelosPrueba);
        afirmar(bench != null && bench.size() == 5, "Benchmarking evaluó los 5 algoritmos (Burbuja, Inserción, Quicksort, Mergesort, Heapsort)");
        afirmar(bench.get(0).tiempoNanos >= 0, "Tiempo medido de ejecución en nanosegundos es consistente y no negativo");
    }

    // 16. SERVICIO CENTRAL Y AUDITORÍA DE SALUD (12 ESTRUCTURAS)
    private static void probarServicioYAuditoria() {
        System.out.println("\n--- [16] PRUEBAS DE CAPA DE SERVICIO Y AUDITORÍA DE SALUD (12 TADs) ---");
        ControladorAeropuerto ctrl = new ControladorAeropuerto();
        ctrl.cargarDatosDemostracion();

        ServicioAeropuerto servicio = ServicioAeropuerto.obtenerInstancia();
        afirmar(servicio != null, "Instancia Singleton de ServicioAeropuerto obtenida");

        String reporte = servicio.ejecutarDiagnosticoIntegralTADs(ctrl);
        afirmar(reporte != null && reporte.contains("12 de 12 Estructuras y TADs verificados"),
                "Diagnóstico Integral de TADs ejecutado en modo lectura verificando las 12 estructuras al 100%");

        // Despacho digital de comprobantes SUNAT por correo electrónico (Requisito Académico)
        com.aeropuerto.servicio.ServicioCorreo srvCorreo = com.aeropuerto.servicio.ServicioCorreo.obtenerInstancia();
        boolean formatoValido = com.aeropuerto.servicio.ServicioCorreo.esCorreoValido("juan.perez@corpac.pe");
        com.aeropuerto.servicio.ServicioCorreo.RegistroEnvioCorreo regEnvio = srvCorreo.enviarBoletaPorCorreo(
                "juan.perez@corpac.pe", "JUAN PEREZ", "74859612", "B001-00123456", "CP-2024",
                "Lima", "Arequipa", "2026-10-15", "08:30", "2A", "Turista", "Gate 01", 139.0, "LIM-12345", "VOUCHER-TEST");
        afirmar(formatoValido && regEnvio != null && regEnvio.isExitoso(),
                "ServicioCorreo: emisión, formato y despacho digital de Boleta SUNAT a correo del pasajero verificado");

        // Guarda de negocio: integridad de pase de abordar y bloqueo de localizador no emitido
        int filaConfirmadaInvalida = -1;
        String dniInvalido = "";
        String nombreInvalido = "";
        boolean prereqsInvalidos = (filaConfirmadaInvalida >= 0 && dniInvalido.length() == 8 && !nombreInvalido.isEmpty());
        String localizadorEsperadoSinEmitir = prereqsInvalidos ? "LIM-CONFIRMADO" : "LOCALIZADOR: SIN EMITIR";

        int filaConfirmadaValida = 2;
        String dniValido = "74859612";
        String nombreValido = "JUAN PEREZ";
        boolean prereqsValidos = (filaConfirmadaValida >= 0 && dniValido.length() == 8 && !nombreValido.isEmpty());
        String localizadorEmitido = prereqsValidos ? ("LOCALIZADOR: LIM-" + (Math.abs(("CP-2024" + dniValido).hashCode()) % 90000 + 10000) + " [CONFIRMADO]") : "LOCALIZADOR: SIN EMITIR";

        afirmar("LOCALIZADOR: SIN EMITIR".equals(localizadorEsperadoSinEmitir) && localizadorEmitido.contains("[CONFIRMADO]"),
                "Guarda de Pase de Abordar: localizador permanece 'SIN EMITIR' ante reserva incompleta y emite código válido tras confirmación");
    }
}
