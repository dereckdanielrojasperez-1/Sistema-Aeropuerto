package com.aeropuerto.estructuras;

import com.aeropuerto.modelo.Equipaje;

// Lista enlazada simple para gestion de equipajes
public class ListaSimple implements TADLista {

    public static final double LIMITE_SOBREPESO_IATA_KG = 23.0;
    private static final int FACTOR_SEGURIDAD_ITERACIONES = 20;

    private NodoSimple cabeza;
    private int tamano;

    public ListaSimple() {
        this.cabeza = null;
        this.tamano = 0;
    }

    // Obtener cantidad de elementos
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

    // Obtener el nodo cabeza
    public NodoSimple getCabeza() {
        return this.cabeza;
    }

    public NodoSimple obtenerCabeza() {
        return this.cabeza;
    }

    // Buscar maleta por ID
    public Equipaje buscarMaleta(String id) {
        if (id == null || estaVacia()) {
            return null;
        }
        String idBuscado = id.trim();
        if (idBuscado.isEmpty()) {
            return null;
        }
        NodoSimple nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerId() != null 
                    && nodoActual.dato.obtenerId().equalsIgnoreCase(idBuscado)) {
                return nodoActual.dato;
            }
            nodoActual = nodoActual.siguiente;
        }
        return null;
    }

    // Alias para busqueda
    public Equipaje buscar(String id) {
        return buscarMaleta(id);
    }

    // Verificar si existe una maleta por ID (usa buscar internamente)
    public boolean existeMaleta(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return buscarMaleta(id) != null;
    }

    // Agregar maleta al final de la lista
    public boolean agregarMaleta(Equipaje equipaje) {
        if (equipaje == null) {
            return false;
        }
        String id = equipaje.obtenerId();
        String dni = equipaje.obtenerDniPasajero();
        String tipo = equipaje.obtenerTipo();
        double peso = equipaje.obtenerPeso();

        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        if (dni == null || dni.trim().isEmpty()) {
            return false;
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        if (peso <= 0.0) {
            return false;
        }
        if (existeMaleta(id)) {
            return false;
        }

        NodoSimple nodoNuevo = new NodoSimple(equipaje);
        // Caso 1: Inserción en lista vacía
        if (estaVacia()) {
            this.cabeza = nodoNuevo;
        } else {
            // Caso 2: Recorrido hasta el último nodo
            NodoSimple nodoActual = this.cabeza;
            while (nodoActual.siguiente != null) {
                nodoActual = nodoActual.siguiente;
            }
            nodoActual.siguiente = nodoNuevo;
        }
        this.tamano++;
        return true;
    }

    // Alias para insercion
    public void insertar(Equipaje equipaje) {
        agregarMaleta(equipaje);
    }

    // Eliminar maleta por ID
    public boolean eliminarMaleta(String id) {
        if (id == null || estaVacia()) {
            return false;
        }
        String idBuscado = id.trim();
        if (idBuscado.isEmpty()) {
            return false;
        }

        // Caso Borde 1: Eliminación en la cabeza
        if (this.cabeza != null && this.cabeza.dato != null && this.cabeza.dato.obtenerId() != null
                && this.cabeza.dato.obtenerId().equalsIgnoreCase(idBuscado)) {
            this.cabeza = this.cabeza.siguiente;
            this.tamano--;
            if (this.tamano <= 0) this.cabeza = null;
            return true;
        }

        // Caso Borde 2: Eliminación intermedia o final con reconexión de punteros
        NodoSimple nodoAnterior = this.cabeza;
        NodoSimple nodoActual = (this.cabeza != null) ? this.cabeza.siguiente : null;
        int iteraciones = 0;
        int maxIteraciones = Math.max(this.tamano + 5, FACTOR_SEGURIDAD_ITERACIONES);
        while (nodoActual != null && iteraciones < maxIteraciones) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerId() != null
                    && nodoActual.dato.obtenerId().equalsIgnoreCase(idBuscado)) {
                if (nodoAnterior != null) {
                    nodoAnterior.siguiente = nodoActual.siguiente;
                }
                this.tamano--;
                if (this.tamano <= 0) this.cabeza = null;
                return true;
            }
            nodoAnterior = nodoActual;
            nodoActual = nodoActual.siguiente;
            iteraciones++;
        }
        return false;
    }

    // Alias para eliminacion
    public boolean eliminar(String id) {
        return eliminarMaleta(id);
    }

    // Actualizar maleta por ID (valida peso > 0 y tipo no vacio)
    public boolean actualizarMaleta(String id, double nuevoPeso, String nuevoTipo) {
        if (id == null || nuevoTipo == null || nuevoPeso <= 0.0 || nuevoTipo.trim().isEmpty()) {
            return false;
        }
        Equipaje encontrada = buscarMaleta(id);
        if (encontrada != null) {
            encontrada.establecerPeso(nuevoPeso);
            encontrada.establecerTipo(nuevoTipo.trim());
            return true;
        }
        return false;
    }

    // Ordenar maletas por peso de menor a mayor (ascendente) con metodo Burbuja
    public void ordenarPorPeso() {
        if (this.cabeza == null || this.cabeza.siguiente == null) {
            return;
        }
        boolean huboIntercambio;
        do {
            huboIntercambio = false;
            NodoSimple nodoActual = this.cabeza;
            while (nodoActual.siguiente != null) {
                if (nodoActual.dato != null && nodoActual.siguiente.dato != null) {
                    if (nodoActual.dato.obtenerPeso() > nodoActual.siguiente.dato.obtenerPeso()) {
                        Equipaje equipajeAuxiliar = nodoActual.dato;
                        nodoActual.dato = nodoActual.siguiente.dato;
                        nodoActual.siguiente.dato = equipajeAuxiliar;
                        huboIntercambio = true;
                    }
                }
                nodoActual = nodoActual.siguiente;
            }
        } while (huboIntercambio);
    }

    // Listar maletas registradas usando StringBuilder
    @Override
    public String listar() {
        return listarMaletas();
    }

    public String listarMaletas() {
        if (estaVacia()) {
            return "No hay equipaje registrado.";
        }
        StringBuilder sb = new StringBuilder();
        NodoSimple nodoActual = this.cabeza;
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

    // Calculo de peso total acumulado
    public double calcularPesoTotal() {
        double pesoTotalAcumulado = 0.0;
        NodoSimple nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                pesoTotalAcumulado += nodoActual.dato.obtenerPeso();
            }
            nodoActual = nodoActual.siguiente;
        }
        return pesoTotalAcumulado;
    }

    // Contar equipaje con sobrepeso (mayor a limite estándar de aerolínea)
    public int contarSobrepeso(double limiteKg) {
        int contador = 0;
        NodoSimple nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null && nodoActual.dato.obtenerPeso() > limiteKg) {
                contador++;
            }
            nodoActual = nodoActual.siguiente;
        }
        return contador;
    }

    // Representacion grafica de enlaces simples en memoria
    public String obtenerRepresentacionNodal() {
        if (estaVacia()) return "[ Cabeza ] -> [ null ]";
        StringBuilder sb = new StringBuilder();
        sb.append("[ Cabeza ] -> ");
        NodoSimple nodoActual = this.cabeza;
        while (nodoActual != null) {
            if (nodoActual.dato != null) {
                sb.append("[ ").append(nodoActual.dato.obtenerId())
                  .append(" | ").append(nodoActual.dato.obtenerPeso()).append("kg ] -> ");
            }
            nodoActual = nodoActual.siguiente;
        }
        sb.append("[ null ]");
        return sb.toString();
    }

    public void vaciar() {
        this.cabeza = null;
        this.tamano = 0;
    }
}