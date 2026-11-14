package com.aeropuerto.estructuras.hash;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ESTRUCTURA DE DATOS AVANZADA: TABLA HASH (DISPERSIÓN CON ENCADENAMIENTO SEPARADO)
 * ============================================================================
 * Propósito en CORPAC:
 * - Auditoría, verificación y búsqueda en tiempo constante O(1) de información
 *   de pasajeros y pases de abordar utilizando el número de DNI como clave primaria.
 * - Resolución de colisiones mediante encadenamiento separado (Separate Chaining)
 *   utilizando nodos simplemente enlazados por cubeta.
 *
 * Análisis de Complejidad Temporal (Big-O):
 * - Inserción (put):           O(1) promedio - Inserción en cubeta con factor de carga controlado
 * - Búsqueda por clave (get):  O(1) promedio - Acceso directo por índice hash polinomial
 * - Eliminación (remove):      O(1) promedio - Reconexión nodal dentro de la cubeta
 * - Peor caso degenerado:      O(n) - Si todas las claves colisionaran en la misma cubeta
 * - Rehashing dinámico:        O(n) - Redistribución total al superar el factor de carga (alpha > 0.75)
 *
 * Mecanismo de Dispersión y Factor de Carga:
 * - Función de dispersión con difusión binaria: h ^= (h >>> 16)
 * - Mapeo por compresión a tamaño primo de tabla para dispersión uniforme.
 * - Factor de carga límite: FACTOR_CARGA_MAXIMO = 0.75. Al superarse, se duplica la
 *   capacidad al siguiente número primo y se rehashean todos los nodos activos.
 * ============================================================================
 *
 * @param <K> Tipo de la clave (ej. DNI del pasajero, código de reserva)
 * @param <V> Tipo del valor asociado (ej. Datos completos del pasajero, ticket)
 */
public class TablaHash<K, V> {

    public static final int CAPACIDAD_INICIAL_DEFECTO = 17;
    public static final double FACTOR_CARGA_MAXIMO = 0.75;
    private static final int PRIMO_MINIMO_PERMITIDO = 11;

    @SuppressWarnings("unchecked")
    private NodoHash<K, V>[] cubetas;
    private int capacidad;
    private int tamano;
    private int totalColisiones;

    public TablaHash() {
        this(CAPACIDAD_INICIAL_DEFECTO);
    }

    @SuppressWarnings("unchecked")
    public TablaHash(int capacidadInicial) {
        this.capacidad = siguientePrimo(Math.max(PRIMO_MINIMO_PERMITIDO, capacidadInicial));
        this.cubetas = new NodoHash[this.capacidad];
        this.tamano = 0;
        this.totalColisiones = 0;
    }

    public int obtenerTamano() {
        return tamano;
    }

    public int obtenerCapacidad() {
        return capacidad;
    }

    public double obtenerFactorCargaActual() {
        return (double) tamano / capacidad;
    }

    public int obtenerTotalColisiones() {
        return totalColisiones;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    @SuppressWarnings("unchecked")
    public void vaciar() {
        this.cubetas = new NodoHash[this.capacidad];
        this.tamano = 0;
        this.totalColisiones = 0;
    }

    /**
     * Función de dispersión Hash con difusión binaria y reducción por módulo primo.
     */
    private int calcularIndice(K clave) {
        if (clave == null) return 0;
        int hashCalculado = clave.hashCode();
        hashCalculado ^= (hashCalculado >>> 16); // Difusión de bits altos a bajos
        int indiceCubeta = hashCalculado % capacidad;
        return (indiceCubeta < 0) ? (indiceCubeta + capacidad) : indiceCubeta;
    }

    /**
     * Inserción en tiempo O(1) promedio.
     * Si la clave ya existe, actualiza el valor asociado.
     * Si el factor de carga supera 0.75, ejecuta rehashing dinámico.
     */
    public boolean insertar(K clave, V valor) {
        if (clave == null) return false;

        // Verificar umbral de factor de carga (alpha <= 0.75)
        if ((double) (tamano + 1) / capacidad > FACTOR_CARGA_MAXIMO) {
            redimensionarYRehashear();
        }

        int indiceCubeta = calcularIndice(clave);
        NodoHash<K, V> nodoActual = cubetas[indiceCubeta];

        if (nodoActual == null) {
            cubetas[indiceCubeta] = new NodoHash<>(clave, valor);
            tamano++;
            return true;
        }

        // Ya hay elementos en la cubeta (posible colisión o clave duplicada)
        NodoHash<K, V> nodoAnterior = null;
        while (nodoActual != null) {
            if (nodoActual.clave.equals(clave)) {
                // Clave existente: actualizamos el valor
                nodoActual.valor = valor;
                return true;
            }
            nodoAnterior = nodoActual;
            nodoActual = nodoActual.siguiente;
        }

        // Colisión resuelta por encadenamiento al final de la cubeta
        if (nodoAnterior != null) {
            nodoAnterior.siguiente = new NodoHash<>(clave, valor);
        }
        tamano++;
        totalColisiones++;
        return true;
    }

    /**
     * Búsqueda en tiempo O(1) promedio.
     */
    public V buscar(K clave) {
        if (clave == null || tamano == 0) return null;
        int indiceCubeta = calcularIndice(clave);
        NodoHash<K, V> nodoActual = cubetas[indiceCubeta];

        while (nodoActual != null) {
            if (nodoActual.clave.equals(clave)) {
                return nodoActual.valor;
            }
            nodoActual = nodoActual.siguiente;
        }
        return null;
    }

    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    /**
     * Eliminación en tiempo O(1) promedio.
     */
    public boolean eliminar(K clave) {
        if (clave == null || tamano == 0) return false;
        int indiceCubeta = calcularIndice(clave);
        NodoHash<K, V> nodoActual = cubetas[indiceCubeta];
        NodoHash<K, V> nodoAnterior = null;

        while (nodoActual != null) {
            if (nodoActual.clave.equals(clave)) {
                if (nodoAnterior == null) {
                    cubetas[indiceCubeta] = nodoActual.siguiente;
                } else {
                    nodoAnterior.siguiente = nodoActual.siguiente;
                }
                tamano--;
                return true;
            }
            nodoAnterior = nodoActual;
            nodoActual = nodoActual.siguiente;
        }
        return false;
    }

    /**
     * Rehashing dinámico: duplica la capacidad hacia un nuevo número primo y redistribuye las claves.
     */
    @SuppressWarnings("unchecked")
    private void redimensionarYRehashear() {
        int nuevaCapacidad = siguientePrimo(this.capacidad * 2);
        NodoHash<K, V>[] cubetasAnteriores = this.cubetas;

        this.cubetas = new NodoHash[nuevaCapacidad];
        this.capacidad = nuevaCapacidad;
        this.tamano = 0;
        this.totalColisiones = 0;

        for (NodoHash<K, V> nodoInicial : cubetasAnteriores) {
            NodoHash<K, V> nodoActual = nodoInicial;
            while (nodoActual != null) {
                insertar(nodoActual.clave, nodoActual.valor);
                nodoActual = nodoActual.siguiente;
            }
        }
    }

    public List<K> listarClaves() {
        List<K> claves = new ArrayList<>(tamano);
        for (NodoHash<K, V> cubeta : cubetas) {
            NodoHash<K, V> nodoActual = cubeta;
            while (nodoActual != null) {
                claves.add(nodoActual.clave);
                nodoActual = nodoActual.siguiente;
            }
        }
        return claves;
    }

    public List<V> listarValores() {
        List<V> valores = new ArrayList<>(tamano);
        for (NodoHash<K, V> cubeta : cubetas) {
            NodoHash<K, V> nodoActual = cubeta;
            while (nodoActual != null) {
                valores.add(nodoActual.valor);
                nodoActual = nodoActual.siguiente;
            }
        }
        return valores;
    }

    private static int siguientePrimo(int numero) {
        int candidato = (numero % 2 == 0) ? (numero + 1) : numero;
        while (!esPrimo(candidato)) {
            candidato += 2;
        }
        return candidato;
    }

    private static boolean esPrimo(int numero) {
        if (numero <= 1) return false;
        if (numero <= 3) return true;
        if (numero % 2 == 0 || numero % 3 == 0) return false;
        for (int divisor = 5; divisor * divisor <= numero; divisor += 6) {
            if (numero % divisor == 0 || numero % (divisor + 2) == 0) return false;
        }
        return true;
    }
}
