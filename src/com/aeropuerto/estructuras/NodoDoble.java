package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.Ruta;

// Nodo para lista doble de rutas
public class NodoDoble {
    public Ruta dato;
    public NodoDoble anterior;
    public NodoDoble siguiente;

    public NodoDoble(Ruta dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }

    public Ruta obtenerDato() {
        return dato;
    }

    public void establecerDato(Ruta dato) {
        this.dato = dato;
    }

    public NodoDoble obtenerAnterior() {
        return anterior;
    }

    public void establecerAnterior(NodoDoble anterior) {
        this.anterior = anterior;
    }

    public NodoDoble obtenerSiguiente() {
        return siguiente;
    }

    public void establecerSiguiente(NodoDoble siguiente) {
        this.siguiente = siguiente;
    }
}
