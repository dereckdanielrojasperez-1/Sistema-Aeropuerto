package com.aeropuerto.estructuras.grafo;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado del Árbol de Expansión Mínima (MST - Minimum Spanning Tree).
 * Utilizado por los algoritmos de Prim y Kruskal para la red troncal de CORPAC.
 */
public class ResultadoMST {

    private final String algoritmo;
    private final List<Arista> aristas;
    private final double costoTotal;
    private final double distanciaTotalKm;

    public ResultadoMST(String algoritmo, List<Arista> aristas, double costoTotal, double distanciaTotalKm) {
        this.algoritmo = algoritmo;
        this.aristas = (aristas != null) ? aristas : new ArrayList<>();
        this.costoTotal = costoTotal;
        this.distanciaTotalKm = distanciaTotalKm;
    }

    public String obtenerAlgoritmo() {
        return algoritmo;
    }

    public List<Arista> obtenerAristas() {
        return aristas;
    }

    public double obtenerCostoTotal() {
        return costoTotal;
    }

    public double obtenerDistanciaTotalKm() {
        return distanciaTotalKm;
    }

    public int obtenerCantidadAristas() {
        return aristas.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== ÁRBOL DE EXPANSIÓN MÍNIMA (MST) - %s ===\n", algoritmo.toUpperCase()));
        sb.append(String.format("Total Aristas: %d | Costo Base Total: S/. %.2f | Distancia Total: %.2f km\n",
                aristas.size(), costoTotal, distanciaTotalKm));
        sb.append("Tramos Troncales Seleccionados:\n");
        int idx = 1;
        for (Arista a : aristas) {
            sb.append(String.format("  [%d] %s -> %s (%.1f km, S/. %.2f, %d min)\n",
                    idx++,
                    a.obtenerOrigen().obtenerCodigoIATA(),
                    a.obtenerDestino().obtenerCodigoIATA(),
                    a.obtenerDistanciaKm(),
                    a.obtenerCostoBase(),
                    a.obtenerDuracionMinutos()));
        }
        return sb.toString();
    }
}
