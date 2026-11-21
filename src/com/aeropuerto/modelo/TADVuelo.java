package com.aeropuerto.modelo;

/**
 * =========================================================================
 * ESPECIFICACIÓN FORMAL DEL TIPO ABSTRACTO DE DATOS: TAD VUELO
 * =========================================================================
 * 
 * DOMINIO CONCEPTUAL:
 * Modela una frecuencia de transporte aerocomercial itinerada en CORPAC S.A.
 * Gestiona su código alfanumérico IATA/OACI, ruta, horarios, tarifa base y
 * el estado físico de los 50 asientos de su cabina Airbus A320neo.
 * 
 * SINTAXIS (Signatura de Operaciones):
 * - Constructores:
 *     crearVuelo: String x String x String x String x String x String x int x double x String -> TADVuelo
 * - Observadores:
 *     obtenerCodigo: TADVuelo -> String
 *     obtenerOrigen: TADVuelo -> String
 *     obtenerDestino: TADVuelo -> String
 *     obtenerHoraSalida: TADVuelo -> String
 *     obtenerHoraLlegada: TADVuelo -> String
 *     obtenerPrecioBase: TADVuelo -> double
 *     obtenerClaseDisponible: TADVuelo -> String
 *     contarAsientosOcupados: TADVuelo -> int
 *     contarAsientosLibres: TADVuelo -> int
 *     obtenerMatrizAsientos: TADVuelo -> int[][]
 * - Modificadores:
 *     establecerHoraSalida: TADVuelo x String -> void
 *     establecerPrecioBase: TADVuelo x double -> void
 *     ocuparAsiento: TADVuelo x int x int x String -> boolean
 *     liberarAsiento: TADVuelo x int x int -> boolean
 * 
 * SEMÁNTICA FORMAL (Precondiciones, Postcondiciones e Invariantes):
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(Vuelo):
 *     codigo != null && !codigo.trim().isEmpty()
 *     && origen != null && !origen.trim().isEmpty()
 *     && destino != null && !destino.trim().isEmpty()
 *     && !origen.equalsIgnoreCase(destino)
 *     && precioBase > 0.0
 *     && matrizAsientos != null && matrizAsientos.length == 10 && matrizAsientos[0].length == 5
 *     && (contarAsientosOcupados() + contarAsientosLibres() == 50)
 * 
 * PRECONDICIONES Y POSTCONDICIONES:
 *   Pre: ocuparAsiento(fila, col, dni)
 *        0 <= fila < 10 && 0 <= col < 5 && dni != null && !dni.trim().isEmpty()
 *   Post: matrizAsientos[fila][col] == 1 && matrizPasajeros[fila][col].equals(dni)
 * 
 *   Pre: liberarAsiento(fila, col)
 *        0 <= fila < 10 && 0 <= col < 5 && matrizAsientos[fila][col] == 1
 *   Post: matrizAsientos[fila][col] == 0 && matrizPasajeros[fila][col] == null
 */
public interface TADVuelo {

    String obtenerCodigo();
    void establecerCodigo(String codigo);

    String obtenerOrigen();
    void establecerOrigen(String origen);

    String obtenerDestino();
    void establecerDestino(String destino);

    String obtenerFecha();
    void establecerFecha(String fecha);

    String obtenerHoraSalida();
    void establecerHoraSalida(String horaSalida);

    String obtenerHoraLlegada();
    void establecerHoraLlegada(String horaLlegada);

    int obtenerDuracion();
    void establecerDuracion(int duracion);

    double obtenerPrecioBase();
    void establecerPrecioBase(double precioBase);

    String obtenerClaseDisponible();
    default String obtenerClase() { return obtenerClaseDisponible(); }
    void establecerClaseDisponible(String claseDisponible);

    boolean ocuparAsiento(int fila, int columna, String dni);
    boolean liberarAsiento(int fila, int columna);

    int contarAsientosOcupados();
    int contarAsientosLibres();

    int[][] obtenerMatrizAsientos();
    void establecerMatrizAsientos(int[][] matriz);

    String[][] obtenerMatrizPasajeros();
    void establecerMatrizPasajeros(String[][] matriz);
}
