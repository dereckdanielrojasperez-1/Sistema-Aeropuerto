package com.aeropuerto.modelo;

import java.io.Serializable;

// Implementacion formal del TAD Puerta de Embarque para CORPAC S.A.
public class PuertaEmbarque implements Serializable, TADPuertaEmbarque {
    private static final long serialVersionUID = 1L;

    private String numeroPuerta;
    private String terminal;
    private String estado;
    private String codigoVuelo;

    public PuertaEmbarque(String numeroPuerta, String terminal, String estado, String codigoVuelo) {
        this.numeroPuerta = (numeroPuerta != null) ? numeroPuerta.trim() : "";
        this.terminal = (terminal != null) ? terminal.trim() : "T1";
        this.estado = (estado != null) ? estado.trim() : "Disponible";
        this.codigoVuelo = (codigoVuelo != null) ? codigoVuelo.trim() : "Sin asignar";
    }

    public String obtenerNumeroPuerta() {
        return numeroPuerta;
    }

    public void establecerNumeroPuerta(String numeroPuerta) {
        this.numeroPuerta = numeroPuerta;
    }

    public String obtenerTerminal() {
        return terminal;
    }

    public void establecerTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String obtenerEstado() {
        return estado;
    }

    public void establecerEstado(String estado) {
        this.estado = estado;
    }

    public String obtenerCodigoVuelo() {
        return codigoVuelo;
    }

    public void establecerCodigoVuelo(String codigoVuelo) {
        this.codigoVuelo = codigoVuelo;
    }

    @Override
    public String toString() {
        return numeroPuerta + " (" + terminal + ") | Estado: " + estado + " | Vuelo: " + codigoVuelo;
    }
}
