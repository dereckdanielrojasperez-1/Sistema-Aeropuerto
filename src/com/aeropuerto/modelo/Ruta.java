package com.aeropuerto.modelo;

import java.io.Serializable;

// Implementacion formal del TAD Ruta para CORPAC S.A.
public class Ruta implements Serializable, TADRuta {
    private static final long serialVersionUID = 1L;

    private String id;
    private String origen;
    private String destino;
    private double distanciaKm;

    public Ruta(String id, String origen, String destino, double distanciaKm) {
        this.id = (id != null) ? id.trim() : "";
        this.origen = (origen != null) ? origen.trim() : "";
        this.destino = (destino != null) ? destino.trim() : "";
        this.distanciaKm = distanciaKm;
    }

    public String obtenerId() {
        return id;
    }

    public void establecerId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String obtenerOrigen() {
        return origen;
    }

    public void establecerOrigen(String origen) {
        this.origen = (origen != null) ? origen.trim() : "";
    }

    public String obtenerDestino() {
        return destino;
    }

    public void establecerDestino(String destino) {
        this.destino = (destino != null) ? destino.trim() : "";
    }

    public double obtenerDistanciaKm() {
        return distanciaKm;
    }

    public void establecerDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = (origen != null) ? origen.trim() : "";
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = (destino != null) ? destino.trim() : "";
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Ruta: " + origen + " -> " + destino + " | Distancia: " + distanciaKm + " km";
    }
}
