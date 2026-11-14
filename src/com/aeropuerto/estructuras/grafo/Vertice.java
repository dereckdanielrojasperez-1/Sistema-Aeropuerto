package com.aeropuerto.estructuras.grafo;

import java.util.Objects;

/**
 * Representa un Vértice o Nodo en la red aeroportuaria (Aeropuerto o Ciudad).
 */
public class Vertice {

    private String codigoIATA;
    private String nombreAeropuerto;
    private String ciudad;
    private boolean activo;

    public Vertice(String codigoIATA, String nombreAeropuerto, String ciudad) {
        if (codigoIATA == null || codigoIATA.trim().isEmpty()) {
            throw new IllegalArgumentException("El código IATA del vértice no puede ser nulo ni vacío.");
        }
        this.codigoIATA = codigoIATA.trim().toUpperCase();
        this.nombreAeropuerto = (nombreAeropuerto != null) ? nombreAeropuerto.trim() : this.codigoIATA;
        this.ciudad = (ciudad != null) ? ciudad.trim() : this.codigoIATA;
        this.activo = true;
    }

    public String obtenerCodigoIATA() {
        return codigoIATA;
    }

    public String obtenerNombreAeropuerto() {
        return nombreAeropuerto;
    }

    public String obtenerCiudad() {
        return ciudad;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void establecerActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertice vertice = (Vertice) o;
        return Objects.equals(codigoIATA, vertice.codigoIATA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoIATA);
    }

    @Override
    public String toString() {
        return codigoIATA + " (" + ciudad + " - " + nombreAeropuerto + ")";
    }
}
