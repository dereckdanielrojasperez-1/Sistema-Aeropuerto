package com.aeropuerto.modelo;

import java.io.Serializable;

// Implementacion formal del TAD Vuelo para CORPAC S.A.
public class Vuelo implements Serializable, TADVuelo {
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private String origen;
    private String destino;
    private String fecha;
    private String horaSalida;
    private String horaLlegada;
    private int duracion;
    private double precioBase;
    private String claseDisponible;

    // Matriz de ocupacion de cabina propia de este vuelo (10 filas x 5 columnas)
    private int[][] matrizAsientos;
    private String[][] matrizPasajeros;

    // Constructor basico
    public Vuelo(String codigo, String destino, String hora) {
        this(codigo, "Lima", destino, "2026-09-15", hora, hora, 75, 120.0, "Turista");
    }

    // Constructor de 7 parametros (conveniente para pruebas y simulaciones)
    public Vuelo(String codigo, String origen, String destino, String horaSalida, String horaLlegada, double precioBase, String claseDisponible) {
        this(codigo, origen, destino, "2026-09-15", horaSalida, horaLlegada, 60, precioBase, claseDisponible);
    }

    // Constructor completo
    public Vuelo(String codigo, String origen, String destino, String fecha, 
                 String horaSalida, String horaLlegada, int duracion, double precioBase, String claseDisponible) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.duracion = duracion;
        this.precioBase = precioBase;
        this.claseDisponible = claseDisponible;

        this.matrizAsientos = new int[10][5];
        this.matrizPasajeros = new String[10][5];
    }

    // Operaciones sobre la matriz de asientos de este vuelo
    public boolean ocuparAsiento(int fila, int columna, String dni) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
            return false;
        }
        if (matrizAsientos[fila][columna] == 0) {
            matrizAsientos[fila][columna] = 1;
            matrizPasajeros[fila][columna] = (dni != null && !dni.trim().isEmpty()) ? dni.trim() : "Reservado";
            return true;
        }
        return false;
    }

    public boolean liberarAsiento(int fila, int columna) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
            return false;
        }
        if (matrizAsientos[fila][columna] == 1) {
            matrizAsientos[fila][columna] = 0;
            matrizPasajeros[fila][columna] = null;
            return true;
        }
        return false;
    }

    public int contarAsientosOcupados() {
        int contador = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrizAsientos[i][j] == 1) {
                    contador++;
                }
            }
        }
        return contador;
    }

    public int contarAsientosLibres() {
        return 50 - contarAsientosOcupados();
    }

    public int[][] obtenerMatrizAsientos() {
        return matrizAsientos;
    }

    public void establecerMatrizAsientos(int[][] matriz) {
        this.matrizAsientos = matriz;
    }

    public String[][] obtenerMatrizPasajeros() {
        return matrizPasajeros;
    }

    public void establecerMatrizPasajeros(String[][] matriz) {
        this.matrizPasajeros = matriz;
    }

    // Getters y Setters
    public String obtenerCodigo() { return codigo; }
    public void establecerCodigo(String codigo) { this.codigo = codigo; }

    public String obtenerOrigen() { return origen; }
    public void establecerOrigen(String origen) { this.origen = origen; }

    public String obtenerDestino() { return destino; }
    public void establecerDestino(String destino) { this.destino = destino; }

    public String obtenerFecha() { return fecha; }
    public void establecerFecha(String fecha) { this.fecha = fecha; }

    public String obtenerHoraSalida() { return horaSalida; }
    public void establecerHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public String obtenerHoraLlegada() { return horaLlegada; }
    public void establecerHoraLlegada(String horaLlegada) { this.horaLlegada = horaLlegada; }

    public String obtenerHora() { return horaSalida; }
    public void establecerHora(String hora) { this.horaSalida = hora; }

    public int obtenerDuracion() { return duracion; }
    public int obtenerDuracionMinutos() { return duracion; }
    public void establecerDuracion(int duracion) { this.duracion = duracion; }

    public double obtenerPrecioBase() { return precioBase; }
    public void establecerPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public String obtenerClaseDisponible() { return claseDisponible; }
    public void establecerClaseDisponible(String claseDisponible) { this.claseDisponible = claseDisponible; }

    @Override
    public String toString() {
        return codigo + " | " + origen + " -> " + destino + " | Salida: " + horaSalida + " Llegada: " + horaLlegada + 
               " | " + duracion + " min | S/." + precioBase + " (" + claseDisponible + ") | Libres: " + contarAsientosLibres() + "/50";
    }
}