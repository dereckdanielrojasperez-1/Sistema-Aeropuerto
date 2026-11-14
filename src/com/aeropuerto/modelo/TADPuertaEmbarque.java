package com.aeropuerto.modelo;

/**
 * =========================================================================
 * ESPECIFICACIÓN FORMAL DEL TIPO ABSTRACTO DE DATOS: TAD PUERTA DE EMBARQUE
 * =========================================================================
 * 
 * DOMINIO CONCEPTUAL:
 * Modela un punto físico de abordaje (Gate) en terminal aeroportuario, asignado
 * mediante rotación continua cíclica (Round-Robin) en CORPAC S.A.
 * 
 * SINTAXIS:
 * - Constructores:
 *     crearPuerta: String x String x String x String -> TADPuertaEmbarque
 * - Observadores:
 *     obtenerNumeroPuerta: TADPuertaEmbarque -> String
 *     obtenerTerminal: TADPuertaEmbarque -> String
 *     obtenerEstado: TADPuertaEmbarque -> String
 *     obtenerCodigoVuelo: TADPuertaEmbarque -> String
 * - Modificadores:
 *     establecerEstado: TADPuertaEmbarque x String -> void
 *     establecerCodigoVuelo: TADPuertaEmbarque x String -> void
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(PuertaEmbarque):
 *     numeroPuerta != null && !numeroPuerta.trim().isEmpty()
 *     && terminal != null && !terminal.trim().isEmpty()
 *     && estado != null && (estado.equalsIgnoreCase("Disponible") || estado.equalsIgnoreCase("Ocupada") || estado.equalsIgnoreCase("Mantenimiento"))
 */
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
