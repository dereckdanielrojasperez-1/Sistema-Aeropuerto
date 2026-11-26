package com.aeropuerto.modelo;

// Especificacion formal del TAD Ruta
public interface TADRuta {

    String obtenerId();
    void establecerId(String id);

    String obtenerOrigen();
    void establecerOrigen(String origen);

    String obtenerDestino();
    void establecerDestino(String destino);

    double obtenerDistanciaKm();
    void establecerDistanciaKm(double distanciaKm);
}
