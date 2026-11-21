package com.aeropuerto.estructuras.arbol;

import com.aeropuerto.estructuras.Cola;
import com.aeropuerto.estructuras.Pila;
import java.util.ArrayList;
import java.util.List;

// Arbol Binario de Busqueda (ABB) con recorridos inorden, preorden, postorden y niveles
public class ArbolBinarioBusqueda<K extends Comparable<K>, V> {

    public static final int MAX_PROFUNDIDAD_SEGURA = 2000;

    private NodoArbol<K, V> raiz;
    private int tamano;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
        this.tamano = 0;
    }

    public boolean estaVacio() {
        return raiz == null || tamano == 0;
    }

    public int obtenerTamano() {
        return tamano;
    }

    public int tamano() {
        return tamano;
    }

    public boolean esVacio() {
        return estaVacio();
    }

    public void vaciar() {
        this.raiz = null;
        this.tamano = 0;
    }

    /**
     * Inserción Iterativa O(log n): Garantiza 0% riesgo de StackOverflowError.
     * Si la clave ya existe, actualiza el valor asociado.
     */
    public boolean insertar(K clave, V valor) {
        if (clave == null) return false;

        NodoArbol<K, V> nodoNuevo = new NodoArbol<>(clave, valor);
        if (this.raiz == null) {
            this.raiz = nodoNuevo;
            this.tamano++;
            return true;
        }

        NodoArbol<K, V> nodoActual = this.raiz;
        NodoArbol<K, V> nodoPadre = null;
        int profundidad = 0;

        while (nodoActual != null && profundidad < MAX_PROFUNDIDAD_SEGURA) {
            nodoPadre = nodoActual;
            int comparacion = clave.compareTo(nodoActual.clave);

            if (comparacion == 0) {
                // Clave duplicada: actualizamos el valor y salimos exitosamente
                nodoActual.valor = valor;
                return true;
            } else if (comparacion < 0) {
                nodoActual = nodoActual.izquierdo;
            } else {
                nodoActual = nodoActual.derecho;
            }
            profundidad++;
        }

        if (profundidad >= MAX_PROFUNDIDAD_SEGURA) {
            // Protección contra ramas desbalanceadas extremas
            return false;
        }

        int compFinal = clave.compareTo(nodoPadre.clave);
        if (compFinal < 0) {
            nodoPadre.izquierdo = nodoNuevo;
        } else {
            nodoPadre.derecho = nodoNuevo;
        }
        this.tamano++;
        return true;
    }

    /**
     * Búsqueda Iterativa O(log n): Totalmente segura contra recursión profunda.
     */
    public V buscar(K clave) {
        if (clave == null || this.raiz == null) return null;

        NodoArbol<K, V> nodoActual = this.raiz;
        int iteraciones = 0;

        while (nodoActual != null && iteraciones < MAX_PROFUNDIDAD_SEGURA) {
            int comp = clave.compareTo(nodoActual.clave);
            if (comp == 0) {
                return nodoActual.valor;
            } else if (comp < 0) {
                nodoActual = nodoActual.izquierdo;
            } else {
                nodoActual = nodoActual.derecho;
            }
            iteraciones++;
        }
        return null;
    }

    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    /**
     * Recorrido In-Order Iterativo: Devuelve los elementos ordenados ascendentemente
     * sin utilizar llamadas recursivas (cero riesgo de desbordamiento de pila).
     * Utiliza la estructura nativa com.aeropuerto.estructuras.Pila.
     */
    public List<V> recorridoInOrden() {
        List<V> resultado = new ArrayList<>(Math.max(10, tamano));
        if (estaVacio()) return resultado;

        Pila pilaAuxiliar = new Pila();
        NodoArbol<K, V> nodoActual = this.raiz;
        int contadorSeguridad = 0;
        int maxIter = tamano * 2 + 100;

        while ((nodoActual != null || !pilaAuxiliar.estaVacia()) && contadorSeguridad < maxIter) {
            while (nodoActual != null) {
                pilaAuxiliar.apilar(nodoActual);
                nodoActual = nodoActual.izquierdo;
            }
            if (!pilaAuxiliar.estaVacia()) {
                @SuppressWarnings("unchecked")
                NodoArbol<K, V> nodoDesapilado = (NodoArbol<K, V>) pilaAuxiliar.desapilar();
                resultado.add(nodoDesapilado.valor);
                nodoActual = nodoDesapilado.derecho;
            }
            contadorSeguridad++;
        }

        return resultado;
    }

    /**
     * Recorrido Pre-Order Iterativo: Raíz -> Izquierdo -> Derecho.
     * Utiliza la estructura nativa com.aeropuerto.estructuras.Pila.
     */
    public List<V> recorridoPreOrden() {
        List<V> resultado = new ArrayList<>(Math.max(10, tamano));
        if (estaVacio()) return resultado;

        Pila pilaAuxiliar = new Pila();
        pilaAuxiliar.apilar(this.raiz);
        int contadorSeguridad = 0;
        int maxIter = tamano * 2 + 100;

        while (!pilaAuxiliar.estaVacia() && contadorSeguridad < maxIter) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) pilaAuxiliar.desapilar();
            resultado.add(nodoActual.valor);

            // Primero apilamos el derecho para que el izquierdo salga primero (disciplina LIFO)
            if (nodoActual.derecho != null) {
                pilaAuxiliar.apilar(nodoActual.derecho);
            }
            if (nodoActual.izquierdo != null) {
                pilaAuxiliar.apilar(nodoActual.izquierdo);
            }
            contadorSeguridad++;
        }

        return resultado;
    }

    /**
     * Recorrido Post-Order Iterativo: Izquierdo -> Derecho -> Raíz.
     * Implementado con dos pilas nativas para garantizar orden estricto sin recursión.
     */
    public List<V> recorridoPostOrden() {
        List<V> resultado = new ArrayList<>(Math.max(10, tamano));
        if (estaVacio()) return resultado;

        Pila pila1 = new Pila();
        Pila pila2 = new Pila();

        pila1.apilar(this.raiz);
        int contadorSeguridad = 0;
        int maxIter = tamano * 2 + 100;

        while (!pila1.estaVacia() && contadorSeguridad < maxIter) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) pila1.desapilar();
            pila2.apilar(nodoActual);

            if (nodoActual.izquierdo != null) {
                pila1.apilar(nodoActual.izquierdo);
            }
            if (nodoActual.derecho != null) {
                pila1.apilar(nodoActual.derecho);
            }
            contadorSeguridad++;
        }

        while (!pila2.estaVacia()) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoExtraido = (NodoArbol<K, V>) pila2.desapilar();
            resultado.add(nodoExtraido.valor);
        }

        return resultado;
    }

    /**
     * Recorrido por Niveles (Amplitud / BFS) utilizando la estructura com.aeropuerto.estructuras.Cola.
     */
    public List<V> recorridoPorNiveles() {
        List<V> resultado = new ArrayList<>(Math.max(10, tamano));
        if (estaVacio()) return resultado;

        Cola colaAuxiliar = new Cola();
        colaAuxiliar.encolar(this.raiz);
        int contadorSeguridad = 0;
        int maxIter = tamano * 2 + 100;

        while (!colaAuxiliar.estaVacia() && contadorSeguridad < maxIter) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) colaAuxiliar.desencolar();
            resultado.add(nodoActual.valor);

            if (nodoActual.izquierdo != null) {
                colaAuxiliar.encolar(nodoActual.izquierdo);
            }
            if (nodoActual.derecho != null) {
                colaAuxiliar.encolar(nodoActual.derecho);
            }
            contadorSeguridad++;
        }

        return resultado;
    }

    /**
     * Calcula la altura del árbol de forma iterativa utilizando Cola propia.
     */
    public int obtenerAltura() {
        if (estaVacio()) return 0;
        Cola colaNiveles = new Cola();
        colaNiveles.encolar(this.raiz);
        int altura = 0;

        while (!colaNiveles.estaVacia()) {
            int nodosNivel = colaNiveles.obtenerTamano();
            altura++;
            for (int i = 0; i < nodosNivel; i++) {
                @SuppressWarnings("unchecked")
                NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) colaNiveles.desencolar();
                if (nodoActual != null) {
                    if (nodoActual.izquierdo != null) colaNiveles.encolar(nodoActual.izquierdo);
                    if (nodoActual.derecho != null) colaNiveles.encolar(nodoActual.derecho);
                }
            }
        }
        return altura;
    }

    /**
     * Cuenta la cantidad de hojas (nodos terminales sin hijos).
     */
    public int contarHojas() {
        if (estaVacio()) return 0;
        Cola colaAuxiliar = new Cola();
        colaAuxiliar.encolar(this.raiz);
        int hojas = 0;

        while (!colaAuxiliar.estaVacia()) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) colaAuxiliar.desencolar();
            if (nodoActual != null) {
                if (nodoActual.esHoja()) hojas++;
                if (nodoActual.izquierdo != null) colaAuxiliar.encolar(nodoActual.izquierdo);
                if (nodoActual.derecho != null) colaAuxiliar.encolar(nodoActual.derecho);
            }
        }
        return hojas;
    }

    /**
     * Obtiene el valor con la menor clave del árbol de forma iterativa.
     */
    public V obtenerMinimo() {
        if (estaVacio()) return null;
        NodoArbol<K, V> nodoActual = this.raiz;
        while (nodoActual.izquierdo != null) {
            nodoActual = nodoActual.izquierdo;
        }
        return nodoActual.valor;
    }

    /**
     * Obtiene el valor con la mayor clave del árbol de forma iterativa.
     */
    public V obtenerMaximo() {
        if (estaVacio()) return null;
        NodoArbol<K, V> nodoActual = this.raiz;
        while (nodoActual.derecho != null) {
            nodoActual = nodoActual.derecho;
        }
        return nodoActual.valor;
    }

    public NodoArbol<K, V> obtenerRaiz() {
        return raiz;
    }
}