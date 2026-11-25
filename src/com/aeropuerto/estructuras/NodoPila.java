package com.aeropuerto.estructuras;

// Nodo dinamico para estructura Pila (LIFO)
public class NodoPila {
    public Object dato;
    public NodoPila siguiente;
    public NodoPila anterior;

    public NodoPila(Object dato) {
        this.dato = dato;
        this.siguiente = null;
        this.anterior = null;
    }

    public Object obtenerDato() {
        return dato;
    }

    public void establecerDato(Object dato) {
        this.dato = dato;
    }

    public NodoPila obtenerSiguiente() {
        return siguiente;
    }

    public void establecerSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}
