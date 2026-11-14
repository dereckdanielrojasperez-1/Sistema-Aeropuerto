package com.aeropuerto.modelo;

/**
 * =========================================================================
 * ESPECIFICACIÓN FORMAL DEL TIPO ABSTRACTO DE DATOS: TAD EQUIPAJE
 * =========================================================================
 * 
 * DOMINIO CONCEPTUAL:
 * Modela una pieza de carga de un pasajero comercial procesada en los mostradores
 * de check-in de CORPAC S.A.
 * 
 * SINTAXIS:
 * - Constructores:
 *     crearEquipaje: String x String x String x double x String -> TADEquipaje
 * - Observadores:
 *     obtenerId: TADEquipaje -> String
 *     obtenerDniPasajero: TADEquipaje -> String
 *     obtenerCodigoVuelo: TADEquipaje -> String
 *     obtenerPeso: TADEquipaje -> double
 *     obtenerTipo: TADEquipaje -> String
 * - Modificadores:
 *     establecerPeso: TADEquipaje x double -> void
 *     establecerTipo: TADEquipaje x String -> void
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(Equipaje):
 *     id != null && !id.trim().isEmpty()
 *     && dniPasajero != null && !dniPasajero.trim().isEmpty()
 *     && peso > 0.0 && peso <= 45.0 (límite operativo IATA de seguridad)
 *     && tipo != null && (tipo.contains("Bodega") || tipo.contains("Cabina") || tipo.contains("Mochila"))
 * 
 * PRECONDICIONES / POSTCONDICIONES:
 *   Pre: establecerPeso(nuevoPeso) => nuevoPeso > 0.0
 *   Post: obtenerPeso() == nuevoPeso
 */
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
