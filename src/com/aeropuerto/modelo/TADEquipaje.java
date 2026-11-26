package com.aeropuerto.modelo;

// Especificacion formal del TAD Equipaje
public interface TADEquipaje {

    String obtenerId();
    void establecerId(String id);

    String obtenerDniPasajero();
    void establecerDniPasajero(String dniPasajero);

    String obtenerCodigoVuelo();
    void establecerCodigoVuelo(String codigoVuelo);

    double obtenerPeso();
    void establecerPeso(double peso);

    String obtenerTipo();
    void establecerTipo(String tipo);
}
