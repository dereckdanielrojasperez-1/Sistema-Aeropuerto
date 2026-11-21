package com.aeropuerto.estructuras.arbol;

import com.aeropuerto.estructuras.Cola;
import java.util.ArrayList;
import java.util.List;

// Arbol AVL balanceado con 4 rotaciones canonicas (RSD, RSI, RDID, RDDI)
public class ArbolAVL<K extends Comparable<K>, V> {

    public static final int FACTOR_BALANCE_MAXIMO = 1;
    public static final int FACTOR_BALANCE_MINIMO = -1;

    private NodoArbol<K, V> raiz;
    private int tamano;
    private final List<String> registroRotaciones;

    public ArbolAVL() {
        this.raiz = null;
        this.tamano = 0;
        this.registroRotaciones = new ArrayList<>();
    }

    public boolean estaVacio() {
        return raiz == null || tamano == 0;
    }

    public boolean esVacio() {
        return estaVacio();
    }

    public int obtenerTamano() {
        return tamano;
    }

    public int tamano() {
        return obtenerTamano();
    }

    public void vaciar() {
        this.raiz = null;
        this.tamano = 0;
        this.registroRotaciones.clear();
    }

    public NodoArbol<K, V> obtenerRaiz() {
        return raiz;
    }

    public List<String> obtenerRegistroRotaciones() {
        return new ArrayList<>(registroRotaciones);
    }

    public void limpiarRegistroRotaciones() {
        this.registroRotaciones.clear();
    }

    private int altura(NodoArbol<K, V> nodo) {
        return (nodo != null) ? nodo.altura : 0;
    }

    private int factorBalance(NodoArbol<K, V> nodo) {
        return (nodo != null) ? (altura(nodo.derecho) - altura(nodo.izquierdo)) : 0;
    }

    private void actualizarAltura(NodoArbol<K, V> nodo) {
        if (nodo != null) {
            nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        }
    }

    // ROTACIONES CANÓNICAS DEL ÁRBOL AVL

    /**
     * Rotación Simple a la Derecha (RSD / LL - Left-Left).
     * Explicación Pedagógica:
     * Ocurre cuando se inserta un elemento en el subárbol izquierdo del hijo izquierdo.
     * El subárbol izquierdo tiene mayor altura (FE < -1) y su hijo izquierdo está cargado (FE <= 0).
     * Solución: Se rota el 'nodoDesbalanceado' hacia la derecha; su 'hijoIzquierdo' asciende
     * como nueva raíz y el subárbol derecho de este pasa a ser el hijo izquierdo del nodo original.
     */
    private NodoArbol<K, V> rotacionDerecha(NodoArbol<K, V> nodoDesbalanceado) {
        NodoArbol<K, V> nuevaRaizSubarbol = nodoDesbalanceado.izquierdo;
        NodoArbol<K, V> subarbolIntermedio = nuevaRaizSubarbol.derecho;

        // Efectuar la rotación topológica de punteros
        nuevaRaizSubarbol.derecho = nodoDesbalanceado;
        nodoDesbalanceado.izquierdo = subarbolIntermedio;

        // Actualizar las alturas recalculadas
        actualizarAltura(nodoDesbalanceado);
        actualizarAltura(nuevaRaizSubarbol);

        String registro = String.format("[ROTACIÓN RSD / LL] Rotación Simple Derecha sobre nodo '%s' (Nueva raíz: '%s')",
                nodoDesbalanceado.clave, nuevaRaizSubarbol.clave);
        registroRotaciones.add(registro);

        return nuevaRaizSubarbol;
    }

    /**
     * Rotación Simple a la Izquierda (RSI / RR - Right-Right).
     * Explicación Pedagógica:
     * Ocurre cuando se inserta un elemento en el subárbol derecho del hijo derecho.
     * El subárbol derecho tiene mayor altura (FE > 1) y su hijo derecho está cargado (FE >= 0).
     * Solución: Se rota el 'nodoDesbalanceado' hacia la izquierda; su 'hijoDerecho' asciende
     * como nueva raíz y el subárbol izquierdo de este pasa a ser el hijo derecho del nodo original.
     */
    private NodoArbol<K, V> rotacionIzquierda(NodoArbol<K, V> nodoDesbalanceado) {
        NodoArbol<K, V> nuevaRaizSubarbol = nodoDesbalanceado.derecho;
        NodoArbol<K, V> subarbolIntermedio = nuevaRaizSubarbol.izquierdo;

        // Efectuar la rotación topológica de punteros
        nuevaRaizSubarbol.izquierdo = nodoDesbalanceado;
        nodoDesbalanceado.derecho = subarbolIntermedio;

        // Actualizar las alturas recalculadas
        actualizarAltura(nodoDesbalanceado);
        actualizarAltura(nuevaRaizSubarbol);

        String registro = String.format("[ROTACIÓN RSI / RR] Rotación Simple Izquierda sobre nodo '%s' (Nueva raíz: '%s')",
                nodoDesbalanceado.clave, nuevaRaizSubarbol.clave);
        registroRotaciones.add(registro);

        return nuevaRaizSubarbol;
    }

    /**
     * Rotación Doble Izquierda-Derecha (RDID / LR - Left-Right).
     * Explicación Pedagógica:
     * Ocurre cuando se inserta en el subárbol derecho del hijo izquierdo (forma de 'zigzag').
     * Genera un desbalance con FE(nodo) < -1 pero con FE(hijoIzquierdo) > 0.
     * Solución:
     *   Paso 1: Rotación simple izquierda sobre el 'hijoIzquierdo' (alinea el desbalance a LL).
     *   Paso 2: Rotación simple derecha sobre el 'nodoDesbalanceado' para restaurar el balance.
     */
    private NodoArbol<K, V> rotacionIzquierdaDerecha(NodoArbol<K, V> nodoDesbalanceado) {
        registroRotaciones.add(String.format("[ROTACIÓN RDID / LR - Paso 1] Pre-rotación izquierda en hijo '%s'", nodoDesbalanceado.izquierdo.clave));
        nodoDesbalanceado.izquierdo = rotacionIzquierda(nodoDesbalanceado.izquierdo);
        return rotacionDerecha(nodoDesbalanceado);
    }

    /**
     * Rotación Doble Derecha-Izquierda (RDDI / RL - Right-Left).
     * Explicación Pedagógica:
     * Ocurre cuando se inserta en el subárbol izquierdo del hijo derecho (forma de 'zigzag').
     * Genera un desbalance con FE(nodo) > 1 pero con FE(hijoDerecho) < 0.
     * Solución:
     *   Paso 1: Rotación simple derecha sobre el 'hijoDerecho' (alinea el desbalance a RR).
     *   Paso 2: Rotación simple izquierda sobre el 'nodoDesbalanceado' para restaurar el balance.
     */
    private NodoArbol<K, V> rotacionDerechaIzquierda(NodoArbol<K, V> nodoDesbalanceado) {
        registroRotaciones.add(String.format("[ROTACIÓN RDDI / RL - Paso 1] Pre-rotación derecha en hijo '%s'", nodoDesbalanceado.derecho.clave));
        nodoDesbalanceado.derecho = rotacionDerecha(nodoDesbalanceado.derecho);
        return rotacionIzquierda(nodoDesbalanceado);
    }

    /**
     * Balancea un nodo aplicando las rotaciones necesarias si |FE| > 1.
     */
    private NodoArbol<K, V> balancear(NodoArbol<K, V> nodo) {
        if (nodo == null) return null;

        actualizarAltura(nodo);
        int balance = factorBalance(nodo);

        // Caso 1: Desbalance Izquierda-Izquierda (LL)
        if (balance < FACTOR_BALANCE_MINIMO && factorBalance(nodo.izquierdo) <= 0) {
            return rotacionDerecha(nodo);
        }

        // Caso 2: Desbalance Derecha-Derecha (RR)
        if (balance > FACTOR_BALANCE_MAXIMO && factorBalance(nodo.derecho) >= 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso 3: Desbalance Izquierda-Derecha (LR)
        if (balance < FACTOR_BALANCE_MINIMO && factorBalance(nodo.izquierdo) > 0) {
            return rotacionIzquierdaDerecha(nodo);
        }

        // Caso 4: Desbalance Derecha-Izquierda (RL)
        if (balance > FACTOR_BALANCE_MAXIMO && factorBalance(nodo.derecho) < 0) {
            return rotacionDerechaIzquierda(nodo);
        }

        return nodo; // Ya se encuentra balanceado (-1, 0, +1)
    }

    // OPERACIONES DE INSERCIÓN, BÚSQUEDA Y ELIMINACIÓN

    public boolean insertar(K clave, V valor) {
        if (clave == null) return false;
        int tamInicial = this.tamano;
        this.raiz = insertarRecursivo(this.raiz, clave, valor);
        return this.tamano > tamInicial || this.tamano == tamInicial;
    }

    private NodoArbol<K, V> insertarRecursivo(NodoArbol<K, V> nodoActual, K clave, V valor) {
        if (nodoActual == null) {
            this.tamano++;
            return new NodoArbol<>(clave, valor);
        }

        int comp = clave.compareTo(nodoActual.clave);
        if (comp < 0) {
            nodoActual.izquierdo = insertarRecursivo(nodoActual.izquierdo, clave, valor);
        } else if (comp > 0) {
            nodoActual.derecho = insertarRecursivo(nodoActual.derecho, clave, valor);
        } else {
            // Clave duplicada: actualiza el valor asociado
            nodoActual.valor = valor;
            return nodoActual;
        }

        return balancear(nodoActual);
    }

    public V buscar(K clave) {
        if (clave == null || this.raiz == null) return null;
        NodoArbol<K, V> nodoActual = this.raiz;
        while (nodoActual != null) {
            int comp = clave.compareTo(nodoActual.clave);
            if (comp == 0) {
                return nodoActual.valor;
            } else if (comp < 0) {
                nodoActual = nodoActual.izquierdo;
            } else {
                nodoActual = nodoActual.derecho;
            }
        }
        return null;
    }

    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    public boolean eliminar(K clave) {
        if (clave == null || !contiene(clave)) return false;
        int tamInicial = this.tamano;
        this.raiz = eliminarRecursivo(this.raiz, clave);
        return this.tamano < tamInicial;
    }

    private NodoArbol<K, V> eliminarRecursivo(NodoArbol<K, V> nodoActual, K clave) {
        if (nodoActual == null) return null;

        int comp = clave.compareTo(nodoActual.clave);
        if (comp < 0) {
            nodoActual.izquierdo = eliminarRecursivo(nodoActual.izquierdo, clave);
        } else if (comp > 0) {
            nodoActual.derecho = eliminarRecursivo(nodoActual.derecho, clave);
        } else {
            // Nodo encontrado
            if (nodoActual.izquierdo == null || nodoActual.derecho == null) {
                NodoArbol<K, V> hijoUnico = (nodoActual.izquierdo != null) ? nodoActual.izquierdo : nodoActual.derecho;
                if (hijoUnico == null) {
                    nodoActual = null;
                } else {
                    nodoActual = hijoUnico;
                }
                this.tamano--;
            } else {
                // Nodo con dos hijos: obtener sucesor in-order (el menor del subárbol derecho)
                NodoArbol<K, V> sucesor = obtenerMinimoNodo(nodoActual.derecho);
                nodoActual.clave = sucesor.clave;
                nodoActual.valor = sucesor.valor;
                nodoActual.derecho = eliminarRecursivo(nodoActual.derecho, sucesor.clave);
            }
        }

        if (nodoActual == null) return null;
        return balancear(nodoActual);
    }

    private NodoArbol<K, V> obtenerMinimoNodo(NodoArbol<K, V> nodo) {
        NodoArbol<K, V> nodoActual = nodo;
        while (nodoActual.izquierdo != null) {
            nodoActual = nodoActual.izquierdo;
        }
        return nodoActual;
    }

    // RECORRIDOS FUNDAMENTALES (IN-ORDER, PRE-ORDER, POST-ORDER, POR NIVELES)

    public List<V> recorridoInOrden() {
        List<V> resultado = new ArrayList<>();
        inOrdenRecursivo(this.raiz, resultado);
        return resultado;
    }

    private void inOrdenRecursivo(NodoArbol<K, V> nodo, List<V> lista) {
        if (nodo != null) {
            inOrdenRecursivo(nodo.izquierdo, lista);
            lista.add(nodo.valor);
            inOrdenRecursivo(nodo.derecho, lista);
        }
    }

    public List<V> recorridoPreOrden() {
        List<V> resultado = new ArrayList<>();
        preOrdenRecursivo(this.raiz, resultado);
        return resultado;
    }

    private void preOrdenRecursivo(NodoArbol<K, V> nodo, List<V> lista) {
        if (nodo != null) {
            lista.add(nodo.valor);
            preOrdenRecursivo(nodo.izquierdo, lista);
            preOrdenRecursivo(nodo.derecho, lista);
        }
    }

    public List<V> recorridoPostOrden() {
        List<V> resultado = new ArrayList<>();
        postOrdenRecursivo(this.raiz, resultado);
        return resultado;
    }

    private void postOrdenRecursivo(NodoArbol<K, V> nodo, List<V> lista) {
        if (nodo != null) {
            postOrdenRecursivo(nodo.izquierdo, lista);
            postOrdenRecursivo(nodo.derecho, lista);
            lista.add(nodo.valor);
        }
    }

    /**
     * Recorrido por Niveles (BFS - Amplitud) utilizando la estructura propia com.aeropuerto.estructuras.Cola.
     * Complejidad O(n).
     */
    public List<V> recorridoPorNiveles() {
        List<V> resultado = new ArrayList<>();
        if (estaVacio()) return resultado;

        // Uso de nuestro propio TAD Cola en lugar de java.util.LinkedList (Pureza algorítmica)
        Cola colaAuxiliar = new Cola();
        colaAuxiliar.encolar(this.raiz);

        while (!colaAuxiliar.estaVacia()) {
            @SuppressWarnings("unchecked")
            NodoArbol<K, V> nodoActual = (NodoArbol<K, V>) colaAuxiliar.desencolar();
            resultado.add(nodoActual.valor);
            if (nodoActual.izquierdo != null) colaAuxiliar.encolar(nodoActual.izquierdo);
            if (nodoActual.derecho != null) colaAuxiliar.encolar(nodoActual.derecho);
        }
        return resultado;
    }

    public int obtenerAltura() {
        return altura(this.raiz);
    }

    /**
     * Valida si el árbol cumple estrictamente con el invariante de balance AVL: |FE| <= 1 en todos los nodos.
     */
    public boolean esAVLBalanceado() {
        return verificarBalanceAVL(this.raiz);
    }

    public boolean verificarBalance() {
        return esAVLBalanceado();
    }

    private boolean verificarBalanceAVL(NodoArbol<K, V> nodo) {
        if (nodo == null) return true;
        int balance = factorBalance(nodo);
        if (balance < FACTOR_BALANCE_MINIMO || balance > FACTOR_BALANCE_MAXIMO) return false;
        return verificarBalanceAVL(nodo.izquierdo) && verificarBalanceAVL(nodo.derecho);
    }
}