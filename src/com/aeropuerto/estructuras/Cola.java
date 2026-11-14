package com.aeropuerto.estructuras;

/**
 * ============================================================================
 * TAD COLA CON PRIORIDAD Y COLA FIFO
 * Paquete: com.aeropuerto.estructuras
 * ============================================================================
 * Propósito en CORPAC: Despacho y secuenciación de aeronaves en Torre ATC.
 * Implementa una estructura lineal dinámica con soporte de prioridad multinivel:
 * - Prioridad 1 (EMERGENCIA): Vuelos en emergencia médica o falla mecánica.
 * - Prioridad 2 (PRIORITARIO): Vuelos internacionales o con conexión crítica.
 * - Prioridad 3 (REGULAR): Vuelos comerciales regulares con orden FIFO de llegada.
 * 
 * Estabilidad: Entre aeronaves con igual prioridad, se garantiza el orden de llegada.
 * 
 * Complejidad Asintótica:
 * - Encolar FIFO: O(1)
 * - Encolar con prioridad: O(n)
 * - Desencolar (Dequeue): O(1)
 * - Consulta frente (Peek): O(1)
 */
public class Cola implements TADCola {

    public static final int PRIORIDAD_EMERGENCIA = 1;
    public static final int PRIORIDAD_PRIORITARIO = 2;
    public static final int PRIORIDAD_REGULAR = 3;

    private NodoCola frente;
    private NodoCola fin;
    private int tamano;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    @Override
    public boolean estaVacia() {
        return this.frente == null;
    }

    @Override
    public int obtenerTamano() {
        return this.tamano;
    }

    // Inserción regular FIFO (Prioridad Regular por defecto) - Complejidad O(1)
    @Override
    public void encolar(Object dato) {
        encolarConPrioridad(dato, PRIORIDAD_REGULAR);
    }

    // Inserción con nivel de prioridad (1 = Emergencia, 2 = Prioritario, 3 = Regular)
    @Override
    public void encolarConPrioridad(Object dato, int prioridad) {
        if (dato == null) return;
        // Validación defensiva del rango de prioridad
        if (prioridad < PRIORIDAD_EMERGENCIA) prioridad = PRIORIDAD_EMERGENCIA;
        if (prioridad > PRIORIDAD_REGULAR) prioridad = PRIORIDAD_REGULAR;

        NodoCola nodoNuevo = new NodoCola(dato, prioridad);

        // Caso 1: Cola vacía
        if (estaVacia()) {
            this.frente = nodoNuevo;
            this.fin = nodoNuevo;
            this.tamano++;
            return;
        }

        // Caso 2: El nuevo nodo tiene mayor prioridad estricta que el frente
        if (nodoNuevo.prioridad < this.frente.prioridad) {
            nodoNuevo.siguiente = this.frente;
            this.frente = nodoNuevo;
            this.tamano++;
            return;
        }

        // Caso 3: Inserción regular al final si su prioridad es >= que el último nodo
        if (nodoNuevo.prioridad >= this.fin.prioridad) {
            this.fin.siguiente = nodoNuevo;
            this.fin = nodoNuevo;
            this.tamano++;
            return;
        }

        // Caso 4: Inserción intermedia manteniendo estabilidad FIFO entre prioridades iguales
        NodoCola nodoActual = this.frente;
        int iteraciones = 0;
        int maxIter = this.tamano + 50;
        while (nodoActual.siguiente != null && nodoActual.siguiente.prioridad <= nodoNuevo.prioridad && iteraciones < maxIter) {
            nodoActual = nodoActual.siguiente;
            iteraciones++;
        }
        nodoNuevo.siguiente = nodoActual.siguiente;
        nodoActual.siguiente = nodoNuevo;
        if (nodoNuevo.siguiente == null) {
            this.fin = nodoNuevo;
        }
        this.tamano++;
    }

    // Métodos de conveniencia semántica para la Torre de Control ATC
    public void encolarEmergencia(Object dato) {
        encolarConPrioridad(dato, PRIORIDAD_EMERGENCIA);
    }

    public void encolarPrioritario(Object dato) {
        encolarConPrioridad(dato, PRIORIDAD_PRIORITARIO);
    }

    // Extracción por el frente de la cola (Dequeue) - O(1)
    @Override
    public Object desencolar() {
        if (estaVacia()) {
            return null;
        }
        Object datoRetorno = this.frente.dato;
        this.frente = this.frente.siguiente;
        if (this.frente == null) {
            this.fin = null;
        }
        this.tamano--;
        return datoRetorno;
    }

    // Consulta del elemento al frente sin extraerlo (Peek / Front) - O(1)
    @Override
    public Object frente() {
        if (estaVacia()) {
            return null;
        }
        return this.frente.dato;
    }

    public int obtenerPrioridadFrente() {
        if (estaVacia()) return -1;
        return this.frente.prioridad;
    }

    @Override
    public void vaciar() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    // Listado de la cola para trazabilidad y visualización en consola o UI
    @Override
    public String listar() {
        if (estaVacia()) {
            return "Cola de pista vacía (0 aeronaves en espera de despegue).";
        }
        StringBuilder sb = new StringBuilder("=== COLA DE PISTA CON PRIORIDAD ATC (FRENTE -> FIN) ===\n");
        NodoCola nodoActual = this.frente;
        int turno = 1;
        int iteraciones = 0;
        int maxIter = this.tamano + 50;
        while (nodoActual != null && iteraciones < maxIter) {
            sb.append("[").append(turno).append("º Turno - ").append(nodoActual.obtenerDescripcionPrioridad()).append("]: ")
              .append(nodoActual.dato != null ? nodoActual.dato.toString() : "null")
              .append("\n");
            nodoActual = nodoActual.siguiente;
            turno++;
            iteraciones++;
        }
        return sb.toString();
    }
}
