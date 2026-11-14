package com.aeropuerto.estructuras;

// Nodo dinamico para estructura Cola (FIFO y Cola con Prioridad)
public class NodoCola {
    public Object dato;
    public int prioridad; // 1 = Emergencia (Máxima), 2 = Prioritaria, 3 = Regular (FIFO estándar)
    public NodoCola siguiente;

    public NodoCola(Object dato) {
        this(dato, 3); // Prioridad 3 regular por defecto
    }

    public NodoCola(Object dato, int prioridad) {
        this.dato = dato;
        this.prioridad = (prioridad >= 1 && prioridad <= 3) ? prioridad : 3;
        this.siguiente = null;
    }

    public Object obtenerDato() {
        return dato;
    }

    public void establecerDato(Object dato) {
        this.dato = dato;
    }

    public int obtenerPrioridad() {
        return prioridad;
    }

    public void establecerPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public NodoCola obtenerSiguiente() {
        return siguiente;
    }

    public void establecerSiguiente(NodoCola siguiente) {
        this.siguiente = siguiente;
    }

    public String obtenerDescripcionPrioridad() {
        switch (prioridad) {
            case 1: return "ALTA (Emergencia / Auxilio Médico)";
            case 2: return "MEDIA (Vuelo Prioritario)";
            case 3: default: return "REGULAR (FIFO Estándar)";
        }
    }
}
