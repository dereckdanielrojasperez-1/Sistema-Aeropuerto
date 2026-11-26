package com.aeropuerto.modelo;

// Especificacion formal del TAD Puerta de Embarque
public interface TADPuertaEmbarque {

    String obtenerNumeroPuerta();
    void establecerNumeroPuerta(String numeroPuerta);

    String obtenerTerminal();
    void establecerTerminal(String terminal);

    String obtenerEstado();
    void establecerEstado(String estado);

    String obtenerCodigoVuelo();
    void establecerCodigoVuelo(String codigoVuelo);
}
