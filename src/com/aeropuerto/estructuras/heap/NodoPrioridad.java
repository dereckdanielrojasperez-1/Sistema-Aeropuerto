package com.aeropuerto.estructuras.heap;

/**
 * Elemento de datos con clave de prioridad numérica para el Montículo Binario.
 *
 * @param <T> Tipo del dato almacenado (Aeronave, Vuelo, Equipaje, Pasajero)
 */
public class NodoPrioridad<T> {

    public T dato;
    public int prioridad;

    public NodoPrioridad(T dato, int prioridad) {
        this.dato = dato;
        this.prioridad = prioridad;
    }

    @Override
    public String toString() {
        return "[" + prioridad + "] " + (dato != null ? dato.toString() : "null");
    }
}
