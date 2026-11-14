package com.aeropuerto.modelo;

import java.io.Serializable;

// Implementacion formal del TAD Equipaje para CORPAC S.A.
public class Equipaje implements Serializable, TADEquipaje {
    private static final long serialVersionUID = 1L;

    private String id;
    private String dniPasajero;
    private String codigoVuelo;
    private double peso;
    private String tipo;

    public Equipaje(String id, String dniPasajero, double peso, String tipo) {
        this(id, dniPasajero, "CP-2023", peso, tipo);
    }

    public Equipaje(String id, String dniPasajero, String codigoVuelo, double peso, String tipo) {
        this.id = (id != null) ? id.trim() : "";
        this.dniPasajero = (dniPasajero != null) ? dniPasajero.trim() : "";
        this.codigoVuelo = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : "General";
        this.peso = peso;
        this.tipo = (tipo != null) ? tipo.trim() : "";
    }

    public String obtenerId() {
        return id;
    }

    public String obtenerCodigo() {
        return id;
    }

    public void establecerId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String obtenerDniPasajero() {
        return dniPasajero;
    }

    public void establecerDniPasajero(String dniPasajero) {
        this.dniPasajero = (dniPasajero != null) ? dniPasajero.trim() : "";
    }

    public String obtenerCodigoVuelo() {
        return codigoVuelo;
    }

    public void establecerCodigoVuelo(String codigoVuelo) {
        this.codigoVuelo = (codigoVuelo != null) ? codigoVuelo.trim() : "";
    }

    public double obtenerPeso() {
        return peso;
    }

    public void establecerPeso(double peso) {
        this.peso = peso;
    }

    public String obtenerTipo() {
        return tipo;
    }

    public void establecerTipo(String tipo) {
        this.tipo = (tipo != null) ? tipo.trim() : "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDniPasajero() { return dniPasajero; }
    public void setDniPasajero(String dniPasajero) { this.dniPasajero = dniPasajero; }
    public String getCodigoVuelo() { return codigoVuelo; }
    public void setCodigoVuelo(String codigoVuelo) { this.codigoVuelo = codigoVuelo; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return "ID: " + id + " | DNI: " + dniPasajero + " | Vuelo: " + codigoVuelo + " | Peso: " + peso + " kg | Tipo: " + tipo;
    }
}
