package com.aeropuerto.modelo;

// Especificacion formal del TAD Ruta
/**
 * DOMINIO CONCEPTUAL:
 * Modela un segmento o arco de navegacion aerea troncal en CORPAC S.A.
 * 
 * SINTAXIS:
 * - Constructores:
 *     crearRuta: String x String x String x double -> TADRuta
 * - Observadores:
 *     obtenerId: TADRuta -> String
 *     obtenerOrigen: TADRuta -> String
 *     obtenerDestino: TADRuta -> String
 *     obtenerDistanciaKm: TADRuta -> double
 * - Modificadores:
 *     establecerOrigen: TADRuta x String -> void
 *     establecerDestino: TADRuta x String -> void
 *     establecerDistanciaKm: TADRuta x double -> void
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(Ruta):
 *     id != null && !id.trim().isEmpty()
 *     && origen != null && !origen.trim().isEmpty()
 *     && destino != null && !destino.trim().isEmpty()
 *     && !origen.equalsIgnoreCase(destino)
 *     && distanciaKm > 0.0
 */
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
