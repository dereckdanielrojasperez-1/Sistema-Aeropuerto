package com.aeropuerto.estructuras;

// Estructura Pila LIFO (Last-In, First-Out) para deshacer reservas (Undo)
public class Pila implements TADPila {

    public static final int CAPACIDAD_POR_DEFECTO = 1000;
    private final int capacidadMaxima;
    private NodoPila tope;
    private NodoPila fondo;
    private int tamano;

    public Pila() {
        this(CAPACIDAD_POR_DEFECTO);
    }

    public Pila(int capacidadMaxima) {
        this.capacidadMaxima = Math.max(1, capacidadMaxima);
        this.tope = null;
        this.fondo = null;
        this.tamano = 0;
    }

    public int obtenerCapacidadMaxima() {
        return this.capacidadMaxima;
    }

    @Override
    public boolean estaVacia() {
        return this.tope == null || this.tamano <= 0;
    }

    @Override
    public int obtenerTamano() {
        return this.tamano;
    }

    // Inserción en el tope (Push) - Complejidad O(1) con guarda contra desbordamiento en memoria heap
    @Override
    public void apilar(Object dato) {
        if (dato == null) return;
        if (this.tamano >= this.capacidadMaxima) {
            // Política FIFO de descarte: se elimina la acción más antigua en el fondo en O(1)
            desapilarFondo();
        }
        NodoPila nodoNuevo = new NodoPila(dato);
        nodoNuevo.siguiente = this.tope;
        if (this.tope != null) {
            this.tope.anterior = nodoNuevo;
        }
        this.tope = nodoNuevo;
        if (this.fondo == null) {
            this.fondo = nodoNuevo;
        }
        this.tamano++;
    }

    // Eliminar el elemento más antiguo en el fondo en estricto O(1) mediante puntero fondo
    private void desapilarFondo() {
        if (this.fondo == null) return;
        if (this.fondo.anterior == null) {
            this.tope = null;
            this.fondo = null;
            this.tamano = 0;
            return;
        }
        this.fondo = this.fondo.anterior;
        this.fondo.siguiente = null;
        if (this.tamano > 0) this.tamano--;
    }

    // Extracción del tope (Pop) - Complejidad O(1)
    @Override
    public Object desapilar() {
        if (estaVacia()) {
            return null;
        }
        Object datoRetorno = this.tope.dato;
        this.tope = this.tope.siguiente;
        if (this.tope != null) {
            this.tope.anterior = null;
        } else {
            this.fondo = null;
        }
        this.tamano--;
        return datoRetorno;
    }

    // Inspección del elemento superior sin extraerlo (Peek / Top) - Complejidad O(1)
    @Override
    public Object cima() {
        if (estaVacia()) {
            return null;
        }
        return this.tope.dato;
    }

    // Vaciar completamente la pila
    @Override
    public void vaciar() {
        this.tope = null;
        this.fondo = null;
        this.tamano = 0;
    }

    // Representación en cadena para auditoría y visualización de la pila
    @Override
    public String listar() {
        if (estaVacia()) {
            return "Pila de acciones vacía (0 transacciones pendientes de deshacer).";
        }
        StringBuilder sb = new StringBuilder("Pila de acciones LIFO (Tope a Fondo):\n");
        NodoPila nodoActual = this.tope;
        int nivel = 1;
        int maxIter = this.tamano + 50;
        int iter = 0;
        while (nodoActual != null && iter < maxIter) {
            sb.append("[").append(nivel);
            if (nivel == 1) {
                sb.append(" - TOPE (Próximo a Deshacer)");
            } else {
                sb.append(" - Nivel Profundo");
            }
            sb.append("]: ").append(nodoActual.dato != null ? nodoActual.dato.toString() : "null").append("\n");
            nodoActual = nodoActual.siguiente;
            nivel++;
            iter++;
        }
        return sb.toString();
    }
}
