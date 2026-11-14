package com.aeropuerto.estructuras.hash;

/**
 * Nodo de enlace para la resolución de colisiones por Encadenamiento Separado
 * (Separate Chaining) en la Tabla Hash.
 *
 * @param <K> Tipo de la clave
 * @param <V> Tipo del valor asociado
 */
public class NodoHash<K, V> {

    public K clave;
    public V valor;
    public NodoHash<K, V> siguiente;

    public NodoHash(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        this.clave = clave;
        this.valor = valor;
        this.siguiente = null;
    }

    @Override
    public String toString() {
        return "[" + clave + " => " + valor + "]";
    }
}
