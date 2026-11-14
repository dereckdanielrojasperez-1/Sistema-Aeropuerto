package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.PuertaEmbarque;

// Nodo para lista circular de puertas de embarque
public class NodoCircular {
    public PuertaEmbarque dato;
    public NodoCircular siguiente;

    public NodoCircular(PuertaEmbarque dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public PuertaEmbarque obtenerDato() {
        return dato;
    }

    public void establecerDato(PuertaEmbarque dato) {
        this.dato = dato;
    }

    public NodoCircular obtenerSiguiente() {
        return siguiente;
    }

    public void establecerSiguiente(NodoCircular siguiente) {
        this.siguiente = siguiente;
    }
}
