package com.aeropuerto.estructuras.heap;

import java.util.ArrayList;
import java.util.List;

// Monticulo Binario Min-Heap / Max-Heap para colas de prioridad
public class MonticuloBinario<T> {

    public static final int CAPACIDAD_INICIAL_DEFECTO = 32;

    private NodoPrioridad<T>[] elementos;
    private int tamano;
    private final boolean esMaxHeap;

    public MonticuloBinario() {
        this(false);
    }

    @SuppressWarnings("unchecked")
    public MonticuloBinario(boolean esMaxHeap) {
        this.esMaxHeap = esMaxHeap;
        this.elementos = new NodoPrioridad[CAPACIDAD_INICIAL_DEFECTO];
        this.tamano = 0;
    }

    public boolean esMaxHeap() {
        return esMaxHeap;
    }

    public boolean estaVacio() {
        return tamano == 0;
    }

    public int obtenerTamano() {
        return tamano;
    }

    @SuppressWarnings("unchecked")
    public void vaciar() {
        this.elementos = new NodoPrioridad[CAPACIDAD_INICIAL_DEFECTO];
        this.tamano = 0;
    }

    public T cima() {
        if (estaVacio()) return null;
        return elementos[0].dato;
    }

    public int prioridadCima() {
        if (estaVacio()) return -1;
        return elementos[0].prioridad;
    }

    /**
     * Inserción en tiempo O(log n).
     * Coloca el nuevo elemento al final del arreglo y lo hace flotar (Sift-Up)
     * hasta restaurar la propiedad de Min-Heap.
     */
    public void insertar(T dato, int prioridad) {
        if (dato == null) return;
        asegurarCapacidad();

        NodoPrioridad<T> nodoNuevo = new NodoPrioridad<>(dato, prioridad);
        elementos[tamano] = nodoNuevo;
        flotar(tamano);
        tamano++;
    }

    /**
     * Extracción de la raíz (mínimo en Min-Heap o máximo en Max-Heap) en tiempo O(log n).
     */
    private T extraerRaiz() {
        if (estaVacio()) return null;

        T datoRetorno = elementos[0].dato;
        elementos[0] = elementos[tamano - 1];
        elementos[tamano - 1] = null;
        tamano--;

        if (tamano > 0) {
            hundir(0);
        }

        return datoRetorno;
    }

    public T extraerMinimo() {
        if (!esMaxHeap) {
            return extraerRaiz();
        }
        if (estaVacio()) return null;
        int idxMin = 0;
        int primeraHoja = tamano / 2;
        for (int i = Math.max(0, primeraHoja); i < tamano; i++) {
            if (elementos[i] != null && elementos[i].prioridad < elementos[idxMin].prioridad) {
                idxMin = i;
            }
        }
        T datoRetorno = elementos[idxMin].dato;
        elementos[idxMin] = elementos[tamano - 1];
        elementos[tamano - 1] = null;
        tamano--;
        if (idxMin < tamano) {
            flotar(idxMin);
            hundir(idxMin);
        }
        return datoRetorno;
    }

    /**
     * Extracción del máximo en tiempo O(log n) cuando opera en modo Max-Heap.
     */
    public T extraerMaximo() {
        if (esMaxHeap) {
            return extraerRaiz();
        }
        if (estaVacio()) return null;
        int idxMax = 0;
        int primeraHoja = tamano / 2;
        for (int i = Math.max(0, primeraHoja); i < tamano; i++) {
            if (elementos[i] != null && elementos[i].prioridad > elementos[idxMax].prioridad) {
                idxMax = i;
            }
        }
        T datoRetorno = elementos[idxMax].dato;
        elementos[idxMax] = elementos[tamano - 1];
        elementos[tamano - 1] = null;
        tamano--;
        if (idxMax < tamano) {
            flotar(idxMax);
            hundir(idxMax);
        }
        return datoRetorno;
    }

    private boolean tieneMayorPrecedencia(int idxA, int idxB) {
        if (esMaxHeap) {
            return elementos[idxA].prioridad > elementos[idxB].prioridad;
        }
        return elementos[idxA].prioridad < elementos[idxB].prioridad;
    }

    /**
     * Operación Flotar (Sift-Up): Sube el nodo por el árbol según modo Min-Heap o Max-Heap.
     * Complejidad O(log n).
     */
    private void flotar(int indice) {
        int indiceActual = indice;
        while (indiceActual > 0) {
            int indicePadre = (indiceActual - 1) / 2;
            if (tieneMayorPrecedencia(indiceActual, indicePadre)) {
                intercambiar(indiceActual, indicePadre);
                indiceActual = indicePadre;
            } else {
                break;
            }
        }
    }

    /**
     * Operación Hundir (Sift-Down): Desciende el nodo por el árbol comparándolo
     * con sus hijos izquierdo y derecho según modo Min-Heap o Max-Heap.
     * Complejidad O(log n).
     */
    private void hundir(int indice) {
        int indiceActual = indice;
        while (indiceActual * 2 + 1 < tamano) {
            int indiceHijoIzq = indiceActual * 2 + 1;
            int indiceHijoDer = indiceActual * 2 + 2;
            int indiceMejorHijo = indiceHijoIzq;

            if (indiceHijoDer < tamano && tieneMayorPrecedencia(indiceHijoDer, indiceHijoIzq)) {
                indiceMejorHijo = indiceHijoDer;
            }

            if (tieneMayorPrecedencia(indiceMejorHijo, indiceActual)) {
                intercambiar(indiceActual, indiceMejorHijo);
                indiceActual = indiceMejorHijo;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        NodoPrioridad<T> nodoAuxiliar = elementos[i];
        elementos[i] = elementos[j];
        elementos[j] = nodoAuxiliar;
    }

    @SuppressWarnings("unchecked")
    private void asegurarCapacidad() {
        if (tamano >= elementos.length) {
            NodoPrioridad<T>[] nuevoArreglo = new NodoPrioridad[elementos.length * 2];
            System.arraycopy(elementos, 0, nuevoArreglo, 0, elementos.length);
            this.elementos = nuevoArreglo;
        }
    }

    public List<T> listarElementos() {
        List<T> lista = new ArrayList<>(tamano);
        for (int i = 0; i < tamano; i++) {
            if (elementos[i] != null) {
                lista.add(elementos[i].dato);
            }
        }
        return lista;
    }
}