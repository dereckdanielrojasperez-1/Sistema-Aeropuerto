package com.aeropuerto.modelo;

// Accion transaccional de reserva de asiento para historial de reversiones
public class AccionReserva {
    private String codigoVuelo;
    private int fila;
    private int columna;
    private String dniPasajero;
    private String horaAccion;

    public AccionReserva(String codigoVuelo, int fila, int columna, String dniPasajero) {
        this.codigoVuelo = codigoVuelo;
        this.fila = fila;
        this.columna = columna;
        this.dniPasajero = dniPasajero;
        this.horaAccion = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String obtenerCodigoVuelo() {
        return codigoVuelo;
    }

    public int obtenerFila() {
        return fila;
    }

    public int obtenerColumna() {
        return columna;
    }

    public String obtenerDniPasajero() {
        return dniPasajero;
    }

    public String obtenerHoraAccion() {
        return horaAccion;
    }

    public String obtenerEtiquetaAsiento() {
        char colLetra = (char) ('A' + columna);
        return (fila + 1) + "" + colLetra;
    }

    @Override
    public String toString() {
        return "Reserva Vuelo " + codigoVuelo + " | Asiento " + obtenerEtiquetaAsiento() +
               " [" + fila + "," + columna + "] | DNI: " + dniPasajero + " (" + horaAccion + ")";
    }
}
