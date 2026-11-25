package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.PuertaEmbarque;

// Lista circular para asignacion rotativa de puertas de embarque
public class ListaCircular implements TADLista {

    private static final int FACTOR_SEGURIDAD_ITERACIONES = 20;

    private NodoCircular cabeza;
    private NodoCircular ultimo;
    private NodoCircular cursorActual;
    private int tamano;

    public ListaCircular() {
        this.cabeza = null;
        this.ultimo = null;
        this.cursorActual = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return cabeza == null || tamano <= 0;
    }

    public int obtenerTamano() {
        return tamano;
    }

    public int getTamano() {
        return tamano;
    }

    // Insercion al final con enlace circular O(1)
    public boolean agregarPuerta(PuertaEmbarque puerta) {
        if (puerta == null || puerta.obtenerNumeroPuerta() == null) return false;
        if (buscarPuerta(puerta.obtenerNumeroPuerta()) != null) return false;

        NodoCircular nodoNuevo = new NodoCircular(puerta);
        if (estaVacia()) {
            cabeza = nodoNuevo;
            ultimo = nodoNuevo;
            nodoNuevo.siguiente = cabeza;
            cursorActual = cabeza;
        } else {
            ultimo.siguiente = nodoNuevo;
            nodoNuevo.siguiente = cabeza;
            ultimo = nodoNuevo;
        }
        tamano++;
        return true;
    }

    // Busqueda segura en estructura circular sin riesgo de bucle infinito O(n)
    public PuertaEmbarque buscarPuerta(String numeroPuerta) {
        if (estaVacia() || numeroPuerta == null) return null;
        String buscado = numeroPuerta.trim();
        if (buscado.isEmpty()) return null;

        NodoCircular nodoActual = cabeza;
        int iteraciones = 0;
        int maxIteraciones = Math.max(tamano + 5, FACTOR_SEGURIDAD_ITERACIONES);

        do {
            if (nodoActual != null && nodoActual.dato != null && nodoActual.dato.obtenerNumeroPuerta() != null) {
                if (nodoActual.dato.obtenerNumeroPuerta().equalsIgnoreCase(buscado)) {
                    return nodoActual.dato;
                }
            }
            if (nodoActual != null) {
                nodoActual = nodoActual.siguiente;
            }
            iteraciones++;
        } while (nodoActual != null && nodoActual != cabeza && iteraciones < maxIteraciones);

        if (iteraciones >= maxIteraciones) {
            System.err.println("[ADVERTENCIA TAD] ListaCircular: límite de seguridad maxIteraciones alcanzado en buscarPuerta().");
        }

        return null;
    }

    // Eliminacion en estructura circular con proteccion integral contra NPE
    public boolean eliminarPuerta(String numeroPuerta) {
        if (estaVacia() || numeroPuerta == null) return false;
        String buscado = numeroPuerta.trim();
        if (buscado.isEmpty()) return false;

        // Caso Borde 1: Único nodo en la lista circular
        if (cabeza == ultimo) {
            if (cabeza != null && cabeza.dato != null && cabeza.dato.obtenerNumeroPuerta() != null
                    && cabeza.dato.obtenerNumeroPuerta().equalsIgnoreCase(buscado)) {
                cabeza = null;
                ultimo = null;
                cursorActual = null;
                tamano = 0;
                return true;
            }
            return false;
        }

        // Caso Borde 2: El nodo a eliminar es la cabeza
        if (cabeza != null && cabeza.dato != null && cabeza.dato.obtenerNumeroPuerta() != null
                && cabeza.dato.obtenerNumeroPuerta().equalsIgnoreCase(buscado)) {
            if (cursorActual == cabeza) cursorActual = cabeza.siguiente;
            cabeza = cabeza.siguiente;
            if (ultimo != null) {
                ultimo.siguiente = cabeza;
            }
            tamano--;
            if (tamano <= 0) {
                cabeza = null;
                ultimo = null;
                cursorActual = null;
            }
            return true;
        }

        // Caso Borde 3: Nodo intermedio o ultimo con cierre del lazo circular
        NodoCircular nodoAnterior = cabeza;
        NodoCircular nodoActual = (cabeza != null) ? cabeza.siguiente : null;
        int iteraciones = 0;
        int maxIteraciones = Math.max(tamano + 5, FACTOR_SEGURIDAD_ITERACIONES);

        while (nodoActual != null && nodoActual != cabeza && iteraciones < maxIteraciones) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerNumeroPuerta() != null
                    && nodoActual.dato.obtenerNumeroPuerta().equalsIgnoreCase(buscado)) {
                if (cursorActual == nodoActual) cursorActual = nodoActual.siguiente;
                if (nodoAnterior != null) {
                    nodoAnterior.siguiente = nodoActual.siguiente;
                }
                if (nodoActual == ultimo) {
                    ultimo = nodoAnterior;
                }
                tamano--;
                return true;
            }
            nodoAnterior = nodoActual;
            nodoActual = nodoActual.siguiente;
            iteraciones++;
        }
        if (iteraciones >= maxIteraciones) {
            System.err.println("[ADVERTENCIA TAD] ListaCircular: límite de seguridad maxIteraciones alcanzado en eliminarPuerta().");
        }
        return false;
    }

    // Rotacion circular: avanzar al siguiente nodo O(1)
    public PuertaEmbarque avanzarSiguiente() {
        if (estaVacia()) return null;
        if (cursorActual == null) cursorActual = cabeza;
        if (cursorActual != null) {
            cursorActual = cursorActual.siguiente;
            return (cursorActual != null) ? cursorActual.dato : null;
        }
        return null;
    }

    public PuertaEmbarque obtenerPuertaActual() {
        if (cursorActual == null && cabeza != null) cursorActual = cabeza;
        return (cursorActual != null) ? cursorActual.dato : null;
    }

    // Asignar vuelo al turno actual
    public boolean asignarVueloACursor(String codigoVuelo) {
        if (cursorActual == null && cabeza != null) cursorActual = cabeza;
        if (cursorActual == null || cursorActual.dato == null) return false;
        cursorActual.dato.establecerCodigoVuelo(codigoVuelo);
        cursorActual.dato.establecerEstado("Ocupada");
        return true;
    }

    // Listado completo del ciclo sin riesgo de bucle
    public String listarPuertas() {
        if (estaVacia()) return "No hay puertas de embarque registradas.";
        StringBuilder sb = new StringBuilder();
        NodoCircular nodoActual = cabeza;
        int contador = 1;
        int iteraciones = 0;
        int maxIteraciones = Math.max(tamano + 5, FACTOR_SEGURIDAD_ITERACIONES);

        do {
            if (nodoActual != null && nodoActual.dato != null) {
                sb.append("[").append(contador).append("] ")
                  .append(nodoActual.dato.toString());
                if (nodoActual == cursorActual) {
                    sb.append(" <- [TURNO ACTIVO]");
                }
                sb.append("\n");
                contador++;
            }
            if (nodoActual != null) {
                nodoActual = nodoActual.siguiente;
            }
            iteraciones++;
        } while (nodoActual != null && nodoActual != cabeza && iteraciones < maxIteraciones);

        return sb.toString();
    }

    public NodoCircular obtenerCabeza() {
        return cabeza;
    }

    public NodoCircular obtenerCursor() {
        return cursorActual;
    }

    @Override
    public String listar() {
        return listarPuertas();
    }

    @Override
    public void vaciar() {
        this.cabeza = null;
        this.ultimo = null;
        this.cursorActual = null;
        this.tamano = 0;
    }
}