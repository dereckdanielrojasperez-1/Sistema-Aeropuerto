package com.aeropuerto.estructuras.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ESTRUCTURA DE DATOS AVANZADA: MONTÍCULO BINARIO (MIN-HEAP / COLA DE PRIORIDAD)
 * ============================================================================
 * Propósito en CORPAC:
 * - Control de tráfico aéreo (ATC) en pista de aterrizaje / despegue.
 * - Asignación estricta por niveles de prioridad operacional:
 *     Nivel 1 (Prioridad Máxima): Emergencia aeronáutica (combustible crítico, falla mecánica).
 *     Nivel 2 (Prioridad Alta):   Vuelo comercial en aproximación o vuelo de Estado.
 *     Nivel 3 (Prioridad Normal): Tráfico comercial en itinerario regular.
 *
 * Propiedad Invariante de Min-Heap:
 * - Para todo nodo no raíz con índice 'i': prioridad(padre(i)) <= prioridad(i).
 * - La raíz en el índice 0 siempre contiene el elemento con el valor de prioridad menor
 *   (es decir, la mayor urgencia operacional).
 *
 * Análisis de Complejidad Temporal (Big-O):
 * - Inserción (Push / Flotar - Sift-Up):      O(log n) - Ascenso por niveles del árbol binario completo
 * - Extracción del mínimo (Pop / Hundir):    O(log n) - Reemplazo por último elemento y descenso
 * - Consulta de la cima (Peek):              O(1)     - Acceso inmediato a elementos[0]
 * - Redimensionamiento dinámico:             O(n)     - Duplicación de capacidad al saturar arreglo
 * ============================================================================
 *
 * @param <T> Tipo del dato administrado (ej. Código de vuelo, Aeronave, Ticket de equipaje)
 */
public class MonticuloBinario<T> {

    public static final int CAPACIDAD_INICIAL_DEFECTO = 32;

    private NodoPrioridad<T>[] elementos;
    private int tamano;

    @SuppressWarnings("unchecked")
    public MonticuloBinario() {
        this.elementos = new NodoPrioridad[CAPACIDAD_INICIAL_DEFECTO];
        this.tamano = 0;
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
     * Extracción de la raíz (mínimo) en tiempo O(log n).
     * Extrae el elemento prioritario en índice 0, mueve el último elemento
     * a la cima y lo hunde (Sift-Down) para reequilibrar el montículo.
     */
    public T extraerMinimo() {
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

    /**
     * Operación Flotar (Sift-Up): Sube el nodo por el árbol mientras su prioridad
     * sea menor que la de su nodo padre. Complejidad O(log n).
     */
    private void flotar(int indice) {
        int indiceActual = indice;
        while (indiceActual > 0) {
            int indicePadre = (indiceActual - 1) / 2;
            if (elementos[indiceActual].prioridad < elementos[indicePadre].prioridad) {
                intercambiar(indiceActual, indicePadre);
                indiceActual = indicePadre;
            } else {
                break;
            }
        }
    }

    /**
     * Operación Hundir (Sift-Down): Desciende el nodo por el árbol comparándolo
     * con sus hijos izquierdo y derecho, permutando con el hijo de menor prioridad.
     * Complejidad O(log n).
     */
    private void hundir(int indice) {
        int indiceActual = indice;
        while (indiceActual * 2 + 1 < tamano) {
            int indiceHijoIzq = indiceActual * 2 + 1;
            int indiceHijoDer = indiceActual * 2 + 2;
            int indiceHijoMenor = indiceHijoIzq;

            if (indiceHijoDer < tamano && elementos[indiceHijoDer].prioridad < elementos[indiceHijoIzq].prioridad) {
                indiceHijoMenor = indiceHijoDer;
            }

            if (elementos[indiceHijoMenor].prioridad < elementos[indiceActual].prioridad) {
                intercambiar(indiceActual, indiceHijoMenor);
                indiceActual = indiceHijoMenor;
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
