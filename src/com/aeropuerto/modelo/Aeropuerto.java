package com.aeropuerto.modelo;

// Modelo representativo de un aeropuerto o terminal aereo nacional
public class Aeropuerto {
    private String codigoIata;
    private String nombre;
    private String ciudad;
    private int totalPistas;
    private int pistasLibres;
    private int capacidadStands;
    private int aeronavesActuales;
    private String estado; // "LIBRE", "OCUPADO", "EN ALERTA"
    private String clima;
    private String supervisorACargo;

    public Aeropuerto(String codigoIata, String nombre, String ciudad, int totalPistas,
                      int pistasLibres, int capacidadStands, int aeronavesActuales,
                      String estado, String clima, String supervisorACargo) {
        this.codigoIata = codigoIata;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.totalPistas = totalPistas;
        this.pistasLibres = pistasLibres;
        this.capacidadStands = capacidadStands;
        this.aeronavesActuales = aeronavesActuales;
        this.estado = estado;
        this.clima = clima;
        this.supervisorACargo = supervisorACargo;
    }

    public String obtenerCodigoIata() {
        return codigoIata;
    }

    public void establecerCodigoIata(String codigoIata) {
        this.codigoIata = codigoIata;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void establecerNombre(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerCiudad() {
        return ciudad;
    }

    public void establecerCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int obtenerTotalPistas() {
        return totalPistas;
    }

    public void establecerTotalPistas(int totalPistas) {
        this.totalPistas = totalPistas;
    }

    public int obtenerPistasLibres() {
        return pistasLibres;
    }

    public void establecerPistasLibres(int pistasLibres) {
        this.pistasLibres = pistasLibres;
    }

    public int obtenerCapacidadStands() {
        return capacidadStands;
    }

    public void establecerCapacidadStands(int capacidadStands) {
        this.capacidadStands = capacidadStands;
    }

    public int obtenerAeronavesActuales() {
        return aeronavesActuales;
    }

    public void establecerAeronavesActuales(int aeronavesActuales) {
        this.aeronavesActuales = aeronavesActuales;
    }

    public String obtenerEstado() {
        return estado;
    }

    public void establecerEstado(String estado) {
        this.estado = estado;
    }

    public String obtenerClima() {
        return clima;
    }

    public void establecerClima(String clima) {
        this.clima = clima;
    }

    public String obtenerSupervisorACargo() {
        return supervisorACargo;
    }

    public void establecerSupervisorACargo(String supervisorACargo) {
        this.supervisorACargo = supervisorACargo;
    }

    // Comprueba si el aeropuerto se encuentra completamente libre para operaciones
    public boolean esLibre() {
        return "LIBRE".equalsIgnoreCase(estado) || (pistasLibres > 0 && aeronavesActuales < capacidadStands);
    }

    public double calcularPorcentajeOcupacion() {
        if (capacidadStands <= 0) return 0.0;
        return ((double) aeronavesActuales / capacidadStands) * 100.0;
    }

    public boolean liberarPista() {
        if (pistasLibres < totalPistas) {
            pistasLibres++;
            if (aeronavesActuales > 0) aeronavesActuales--;
            actualizarEstadoAutomatico();
            return true;
        }
        return false;
    }

    public boolean ocuparPista() {
        if (pistasLibres > 0) {
            pistasLibres--;
            aeronavesActuales++;
            actualizarEstadoAutomatico();
            return true;
        }
        return false;
    }

    private void actualizarEstadoAutomatico() {
        if (pistasLibres == 0 || aeronavesActuales >= capacidadStands) {
            this.estado = "OCUPADO";
        } else if (pistasLibres == totalPistas && aeronavesActuales == 0) {
            this.estado = "LIBRE";
        } else {
            this.estado = "OPERATIVO";
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Pistas: %d/%d Libres | Capacidad: %d/%d | Estado: %s | Sup: %s",
                codigoIata, nombre, ciudad, pistasLibres, totalPistas, aeronavesActuales, capacidadStands, estado, supervisorACargo);
    }
}
