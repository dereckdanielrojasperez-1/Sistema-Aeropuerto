package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.Equipaje;

// Nodo para lista simple de equipaje
public class NodoSimple {
    public Equipaje dato;
    public NodoSimple siguiente;

    public NodoSimple(Equipaje dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Equipaje obtenerDato() {
        return dato;
    }

    public void establecerDato(Equipaje dato) {
        this.dato = dato;
    }

    public NodoSimple obtenerSiguiente() {
        return siguiente;
    }

    public void establecerSiguiente(NodoSimple siguiente) {
        this.siguiente = siguiente;
    }
}
