package com.aeropuerto.estructuras.grafo;

import java.util.List;

/**
 * Encapsula la matriz de todos los pares de distancias mínimas calculadas
 * por el Algoritmo de Floyd-Warshall O(V^3). Permite consultas en O(1).
 */
public class ResultadoFloydWarshall {

    private final List<String> codigosVertices;
    private final double[][] matrizCostosMinimos;
    private final String[][] matrizPredecesores;

    public ResultadoFloydWarshall(List<String> codigosVertices, double[][] matrizCostosMinimos, String[][] matrizPredecesores) {
        this.codigosVertices = codigosVertices;
        this.matrizCostosMinimos = matrizCostosMinimos;
        this.matrizPredecesores = matrizPredecesores;
    }

    public List<String> obtenerCodigosVertices() {
        return codigosVertices;
    }

    public double[][] obtenerMatrizCostosMinimos() {
        return matrizCostosMinimos;
    }

    public double obtenerCostoMinimo(String origen, String destino) {
        if (origen == null || destino == null) return Double.POSITIVE_INFINITY;
        int i = codigosVertices.indexOf(origen.trim().toUpperCase());
        int j = codigosVertices.indexOf(destino.trim().toUpperCase());
        if (i < 0 || j < 0) return Double.POSITIVE_INFINITY;
        return matrizCostosMinimos[i][j];
    }

    public boolean hayConexion(String origen, String destino) {
        double costo = obtenerCostoMinimo(origen, destino);
        return !Double.isInfinite(costo) && costo >= 0;
    }

    public String generarReporteMatriz() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATRIZ DE DISTANCIAS M\u00cdNIMAS (TODOS LOS PARES - FLOYD-WARSHALL) ===\n\n");
        sb.append(String.format("%-6s", ""));
        for (String c : codigosVertices) {
            sb.append(String.format("%-10s", c));
        }
        sb.append("\n");
        for (int i = 0; i < codigosVertices.size(); i++) {
            sb.append(String.format("%-6s", codigosVertices.get(i)));
            for (int j = 0; j < codigosVertices.size(); j++) {
                double val = matrizCostosMinimos[i][j];
                if (Double.isInfinite(val)) {
                    sb.append(String.format("%-10s", "INF"));
                } else {
                    sb.append(String.format("%-10.0f", val));
                }
            }
            sb.append("\n");
        }
        sb.append("\nComplejidad Temporal: O(V^3) | Consultas directas entre cualquier par: O(1)\n");
        return sb.toString();
    }
}
