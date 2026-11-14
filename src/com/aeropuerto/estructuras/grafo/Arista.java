package com.aeropuerto.estructuras.grafo;

/**
 * Representa una Arista o Conexión aérea dirigida/ponderada entre dos aeropuertos.
 */
public class Arista {

    private Vertice origen;
    private Vertice destino;
    private double distanciaKm;
    private double costoBase;
    private int duracionMinutos;

    public Arista(Vertice origen, Vertice destino, double distanciaKm, double costoBase, int duracionMinutos) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los vértices de origen y destino no pueden ser nulos.");
        }
        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = Math.max(1.0, distanciaKm);
        this.costoBase = Math.max(0.0, costoBase);
        this.duracionMinutos = Math.max(1, duracionMinutos);
    }

    public Vertice obtenerOrigen() {
        return origen;
    }

    public Vertice obtenerDestino() {
        return destino;
    }

    public double obtenerDistanciaKm() {
        return distanciaKm;
    }

    public double obtenerCostoBase() {
        return costoBase;
    }

    public int obtenerDuracionMinutos() {
        return duracionMinutos;
    }

    @Override
    public String toString() {
        return origen.obtenerCodigoIATA() + " -> " + destino.obtenerCodigoIATA() +
               " [" + distanciaKm + " km, S/ " + String.format("%.2f", costoBase) + ", " + duracionMinutos + " min]";
    }
}
