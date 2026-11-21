package com.aeropuerto.estructuras;

// Interfaz TAD Cola FIFO con prioridad para despegues de aeronaves
public interface TADCola {

    void encolar(Object dato);
    void encolarConPrioridad(Object dato, int prioridad);
    Object desencolar();
    Object frente();
    boolean estaVacia();
    int obtenerTamano();
    void vaciar();
    String listar();
}