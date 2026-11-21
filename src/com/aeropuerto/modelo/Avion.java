package com.aeropuerto.modelo;

import java.io.Serializable;

// Representacion y distribucion de cabina del avion
public class Avion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Matriz de asientos (10x5: 0=Libre, 1=Ocupado)
    private int[][] asientos;

    // Especificaciones tecnicas
    private String modelo = "Airbus A320neo (A320-271N)";
    private String fabricante = "Airbus SAS";
    private String matricula = "CC-BHB";
    private int capacidadReal = 180;
    private int capacidadSimulada = 50;
    private double velocidadCrucero = 828.0;
    private double alcanceMaximo = 6500.0;
    private int techoServicio = 12100;
    private String motores = "2x CFM International LEAP-1A26 Turbofán";
    private double envergadura = 35.80;
    private double longitud = 37.57;

    public Avion() {
        asientos = new int[10][5];
    }

    // Ocupar asiento en coordenadas
    public boolean ocuparAsiento(int fila, int columna) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
            return false;
        }
        if (asientos[fila][columna] == 0) {
            asientos[fila][columna] = 1;
            return true;
        }
        return false;
    }

    // Liberar asiento en coordenadas
    public boolean liberarAsiento(int fila, int columna) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
            return false;
        }
        if (asientos[fila][columna] == 1) {
            asientos[fila][columna] = 0;
            return true;
        }
        return false;
    }

    public int[][] obtenerAsientos() {
        return asientos;
    }

    public int contarOcupados() {
        int cantidad = 0;
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 5; c++) {
                if (asientos[f][c] == 1) cantidad++;
            }
        }
        return cantidad;
    }

    // Factor de costo segun zona fisica
    public double obtenerFactorZona(int fila, int columna) {
        if (columna == 0 || columna == 4) {
            return 1.3; // Ventana
        } else if (columna == 1 || columna == 3) {
            return 1.1; // Pasillo
        } else {
            return 1.0; // Centro
        }
    }

    // Calcular costo segun factor de zona fisica (Fuente unica de verdad)
    public double calcularCostoAsiento(int fila, int columna, double precioBase) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
            return 0.0;
        }
        return Math.round(precioBase * obtenerFactorZona(fila, columna) * 100.0) / 100.0;
    }

    // Matriz de precios calculados
    public double[][] obtenerMatrizPrecios(double precioBase) {
        double[][] precios = new double[10][5];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                precios[i][j] = calcularCostoAsiento(i, j, precioBase);
            }
        }
        return precios;
    }

    // Total recaudado
    public double calcularTotalRecaudado(double precioBase) {
        double total = 0.0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (asientos[i][j] == 1) {
                    total += calcularCostoAsiento(i, j, precioBase);
                }
            }
        }
        return total;
    }

    // Mostrar mapa de asientos en consola
    public String mostrarMapaAsientos() {
        StringBuilder mapaTexto = new StringBuilder("=== MAPA DE ASIENTOS (10x5) - PRECIOS POR ZONA ===\n");
        mapaTexto.append("      Col0(V) Col1(P) Col2(C) Col3(P) Col4(V)\n");
        for (int fila = 0; fila < 10; fila++) {
            mapaTexto.append("Fila ").append(fila).append(fila < 10 ? " : " : ": ");
            for (int col = 0; col < 5; col++) {
                mapaTexto.append(asientos[fila][col] == 0 ? "[ O ] " : "[ X ] ");
            }
            mapaTexto.append("\n");
        }
        mapaTexto.append("Leyenda: V = Ventana (+30%), P = Pasillo (+10%), C = Centro (Base)\n");
        return mapaTexto.toString();
    }

    // Getters de la informacion tecnica real
    public String obtenerModelo() { return modelo; }
    public String obtenerFabricante() { return fabricante; }
    public String obtenerMatricula() { return matricula; }
    public int obtenerCapacidadReal() { return capacidadReal; }
    public int obtenerCapacidadSimulada() { return capacidadSimulada; }
    public double obtenerVelocidadCrucero() { return velocidadCrucero; }
    public double obtenerAlcanceMaximo() { return alcanceMaximo; }
    public int obtenerTechoServicio() { return techoServicio; }
    public String obtenerMotores() { return motores; }
    public double obtenerEnvergadura() { return envergadura; }
    public double obtenerLongitud() { return longitud; }

    public String obtenerFichaTecnica() {
        return "======================================================\n" +
               "     FICHA TÉCNICA OFICIAL - AERONAVE COMERCIAL       \n" +
               "======================================================\n" +
               "- Modelo: " + modelo + "\n" +
               "- Fabricante: " + fabricante + "\n" +
               "- Matrícula Oficial: " + matricula + " (Flota Nacional)\n" +
               "- Capacidad de Pasajeros: " + capacidadReal + " plazas (50 en simulación)\n" +
               "- Velocidad Crucero: " + velocidadCrucero + " km/h (Mach 0.78)\n" +
               "- Alcance Máximo: " + alcanceMaximo + " km (Rutas sin escalas en Perú)\n" +
               "- Techo Máximo de Operación: " + techoServicio + " m (39,800 ft)\n" +
               "- Planta de Poder: " + motores + "\n" +
               "- Dimensiones: Envergadura " + envergadura + " m | Longitud " + longitud + " m\n" +
               "- Distribución de Cabina:\n" +
               "   - Filas 0-2: Primera Clase / Premium Economy (Ventana/Pasillo)\n" +
               "   - Filas 3-5: Clase Ejecutiva (Mayor espacio y prioridad)\n" +
               "   - Filas 6-9: Clase Turista Estándar (Tarifa base)\n" +
               "======================================================";
    }
}