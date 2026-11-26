package com.aeropuerto.modelo;

// Especificacion formal del TAD Vuelo (cabina 10x5 y tarifas)
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
