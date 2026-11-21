package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.Ruta;

// Lista doblemente enlazada para gestion bidireccional de rutas
public class ListaDoble {

    private static final int FACTOR_SEGURIDAD_ITERACIONES = 20;

    private NodoDoble cabeza;
    private NodoDoble cola;
    private int tamano;

    public ListaDoble() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }

    // Obtener cantidad de rutas
    public int obtenerTamano() {
        return this.tamano;
    }

    // Alias para compatibilidad
    public int getTamano() {
        return this.tamano;
    }

    // Comprobar si la lista esta vacia
    public boolean estaVacia() {
        return this.cabeza == null;
    }

    // Obtener nodo cabeza
    public NodoDoble getCabeza() {
        return this.cabeza;
    }

    public NodoDoble obtenerCabeza() {
        return this.cabeza;
    }

    // Obtener nodo cola
    public NodoDoble getCola() {
        return this.cola;
    }

    public NodoDoble obtenerCola() {
        return this.cola;
    }

    // Buscar ruta por ID
    public Ruta buscarRuta(String id) {
        if (id == null || estaVacia()) {
            return null;
        }
        String idBuscado = id.trim();
        if (idBuscado.isEmpty()) {
            return null;
        }
        NodoDoble nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerId().equalsIgnoreCase(idBuscado)) {
                return nodoActual.dato;
            }
            nodoActual = nodoActual.siguiente;
        }
        return null;
    }

    // Alias para busqueda
    public Ruta buscar(String id) {
        return buscarRuta(id);
    }

    // Verificar si existe una ruta por ID (usa buscar internamente)
    public boolean existeRuta(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return buscarRuta(id) != null;
    }

    // Agregar ruta al final de la lista doble O(1)
    public boolean agregarRuta(Ruta ruta) {
        if (ruta == null) {
            return false;
        }
        String id = ruta.obtenerId();
        String origen = ruta.obtenerOrigen();
        String destino = ruta.obtenerDestino();
        double distancia = ruta.obtenerDistanciaKm();

        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        if (origen == null || origen.trim().isEmpty()) {
            return false;
        }
        if (destino == null || destino.trim().isEmpty()) {
            return false;
        }
        if (distancia <= 0.0) {
            return false;
        }
        if (existeRuta(id)) {
            return false;
        }

        NodoDoble nodoNuevo = new NodoDoble(ruta);
        if (estaVacia()) {
            this.cabeza = nodoNuevo;
            this.cola = nodoNuevo;
        } else {
            this.cola.siguiente = nodoNuevo;
            nodoNuevo.anterior = this.cola;
            this.cola = nodoNuevo;
        }
        this.tamano++;
        return true;
    }

    // Alias para insercion al final
    public void insertarAlFinal(Ruta ruta) {
        agregarRuta(ruta);
    }

    // Eliminar ruta por ID con gestión integral de punteros dobles
    public boolean eliminarRuta(String id) {
        if (id == null || estaVacia()) {
            return false;
        }
        String idBuscado = id.trim();
        if (idBuscado.isEmpty()) {
            return false;
        }

        NodoDoble nodoActual = this.cabeza;
        int iteraciones = 0;
        int maxIteraciones = Math.max(this.tamano + 5, FACTOR_SEGURIDAD_ITERACIONES);
        while (nodoActual != null && iteraciones < maxIteraciones) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerId() != null
                    && nodoActual.dato.obtenerId().equalsIgnoreCase(idBuscado)) {
                // Caso A: Único nodo en la lista
                if (nodoActual == this.cabeza && nodoActual == this.cola) {
                    this.cabeza = null;
                    this.cola = null;
                }
                // Caso B: El nodo a eliminar es la cabeza
                else if (nodoActual == this.cabeza) {
                    this.cabeza = this.cabeza.siguiente;
                    if (this.cabeza != null) {
                        this.cabeza.anterior = null;
                    }
                }
                // Caso C: El nodo a eliminar es la cola
                else if (nodoActual == this.cola) {
                    this.cola = this.cola.anterior;
                    if (this.cola != null) {
                        this.cola.siguiente = null;
                    }
                }
                // Caso D: Nodo intermedio (Reconexión simétrica de punteros)
                else {
                    if (nodoActual.anterior != null) {
                        nodoActual.anterior.siguiente = nodoActual.siguiente;
                    }
                    if (nodoActual.siguiente != null) {
                        nodoActual.siguiente.anterior = nodoActual.anterior;
                    }
                }

                this.tamano--;
                if (this.tamano <= 0) {
                    this.cabeza = null;
                    this.cola = null;
                    this.tamano = 0;
                }
                return true;
            }
            nodoActual = nodoActual.siguiente;
            iteraciones++;
        }
        return false;
    }

    // Alias para eliminacion
    public boolean eliminar(String id) {
        return eliminarRuta(id);
    }

    // Actualizar datos de ruta por ID (modifica origen, destino y distanciaKm; nunca el id)
    public boolean actualizarRuta(String id, Ruta nueva) {
        if (id == null || nueva == null) {
            return false;
        }
        String nuevoOrigen = nueva.obtenerOrigen();
        String nuevoDestino = nueva.obtenerDestino();
        double nuevaDistancia = nueva.obtenerDistanciaKm();

        if (nuevoOrigen == null || nuevoOrigen.trim().isEmpty()) {
            return false;
        }
        if (nuevoDestino == null || nuevoDestino.trim().isEmpty()) {
            return false;
        }
        if (nuevaDistancia <= 0.0) {
            return false;
        }

        Ruta encontrada = buscarRuta(id);
        if (encontrada != null) {
            encontrada.establecerOrigen(nuevoOrigen.trim());
            encontrada.establecerDestino(nuevoDestino.trim());
            encontrada.establecerDistanciaKm(nuevaDistancia);
            return true;
        }
        return false;
    }

    // Recorrido de rutas hacia adelante usando StringBuilder (cabeza -> cola)
    public String recorrerAdelante() {
        if (estaVacia()) {
            return "No hay rutas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        NodoDoble nodoActual = this.cabeza;
        int contador = 1;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                sb.append("[").append(contador).append("] ")
                  .append(nodoActual.dato.toString())
                  .append("\n");
                contador++;
            }
            nodoActual = nodoActual.siguiente;
        }
        return sb.toString();
    }

    // Recorrido de rutas hacia atras usando StringBuilder (cola -> cabeza usando anterior)
    public String recorrerAtras() {
        if (estaVacia()) {
            return "No hay rutas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        NodoDoble nodoActual = this.cola;
        int contador = 1;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                sb.append("[").append(contador).append("] ")
                  .append(nodoActual.dato.toString())
                  .append("\n");
                contador++;
            }
            nodoActual = nodoActual.anterior;
        }
        return sb.toString();
    }

    // Representacion grafica de enlaces dobles adelante
    public String obtenerRepresentacionNodal() {
        if (estaVacia()) return "[ null ] <-> [ null ]";
        StringBuilder sb = new StringBuilder();
        sb.append("[ null ] <-> ");
        NodoDoble nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                sb.append("[ ").append(nodoActual.dato.obtenerId())
                  .append(" | ").append(nodoActual.dato.obtenerOrigen()).append("->")
                  .append(nodoActual.dato.obtenerDestino()).append(" ] <-> ");
            }
            nodoActual = nodoActual.siguiente;
        }
        sb.append("[ null ]");
        return sb.toString();
    }

    // Representacion grafica de enlaces dobles en reversa
    public String obtenerRepresentacionNodalReversa() {
        if (estaVacia()) return "[ null ] <-> [ null ]";
        StringBuilder sb = new StringBuilder();
        sb.append("[ null ] <-> ");
        NodoDoble nodoActual = this.cola;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                sb.append("[ ").append(nodoActual.dato.obtenerId())
                  .append(" | ").append(nodoActual.dato.obtenerDestino()).append("<-")
                  .append(nodoActual.dato.obtenerOrigen()).append(" ] <-> ");
            }
            nodoActual = nodoActual.anterior;
        }
        sb.append("[ null ]");
        return sb.toString();
    }

    public void vaciar() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }
}