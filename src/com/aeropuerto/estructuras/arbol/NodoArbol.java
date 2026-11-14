package com.aeropuerto.estructuras.arbol;

/**
 * Nodo genérico para Estructuras Arborescentes (Árbol Binario de Búsqueda y AVL).
 * Diseñado con protección contra referencias nulas y balance de altura.
 *
 * @param <K> Tipo de la clave de ordenamiento (debe implementar Comparable)
 * @param <V> Tipo del valor o entidad asociada
 */
public class NodoArbol<K extends Comparable<K>, V> {

    public K clave;
    public V valor;
    public NodoArbol<K, V> izquierdo;
    public NodoArbol<K, V> derecho;
    public int altura;

    public NodoArbol(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave del nodo del árbol no puede ser nula.");
        }
        this.clave = clave;
        this.valor = valor;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }

    public K obtenerClave() {
        return clave;
    }

    public V obtenerValor() {
        return valor;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }

    public int obtenerAlturaIzquierda() {
        return (izquierdo != null) ? izquierdo.altura : 0;
    }

    public int obtenerAlturaDerecha() {
        return (derecho != null) ? derecho.altura : 0;
    }

    public int obtenerFactorBalance() {
        return obtenerAlturaDerecha() - obtenerAlturaIzquierda();
    }

    public void actualizarAltura() {
        this.altura = 1 + Math.max(obtenerAlturaIzquierda(), obtenerAlturaDerecha());
    }

    @Override
    public String toString() {
        return "[" + clave + " => " + (valor != null ? valor.toString() : "null") + "]";
    }
}
