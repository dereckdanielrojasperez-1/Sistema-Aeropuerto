package com.aeropuerto.estructuras;

// Interfaz TAD Pila (LIFO: Last-In, First-Out)
public interface TADPila {

    void apilar(Object dato);
    Object desapilar();
    Object cima();
    boolean estaVacia();
    int obtenerTamano();
    void vaciar();
    String listar();
}
