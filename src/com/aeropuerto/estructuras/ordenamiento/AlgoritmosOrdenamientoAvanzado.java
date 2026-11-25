package com.aeropuerto.estructuras.ordenamiento;

import com.aeropuerto.modelo.Vuelo;
import java.util.ArrayList;
import java.util.List;

/**
 * Módulo de Algoritmos de Ordenamiento Avanzado (Semana 12 - 15).
 * Implementa de forma pura (sin librerías java.util.Arrays.sort):
 * 1. Quicksort O(n log n)
 * 2. Mergesort O(n log n)
 * 3. Heapsort O(n log n)
 * 
 * Permite benchmarking comparativo con los algoritmos O(n^2) del avance anterior (Burbuja e Inserción).
 */
public class AlgoritmosOrdenamientoAvanzado {

    // 1. QUICKSORT (Divide y Vencerás In-Place) O(n log n)

    public static void quicksortPorPrecio(Vuelo[] arreglo, boolean ascendente) {
        if (arreglo != null && arreglo.length > 1) {
            quicksortPorPrecio(arreglo, 0, arreglo.length - 1, ascendente);
        }
    }

    public static void quicksortPorCodigo(Vuelo[] arreglo) {
        if (arreglo != null && arreglo.length > 1) {
            quicksortPorCodigo(arreglo, 0, arreglo.length - 1);
        }
    }

    public static void quicksortPorPrecio(Vuelo[] arreglo, int bajo, int alto, boolean ascendente) {
        if (arreglo == null || bajo >= alto || bajo < 0 || alto >= arreglo.length) return;

        int pivoteIdx = particionPrecio(arreglo, bajo, alto, ascendente);
        quicksortPorPrecio(arreglo, bajo, pivoteIdx - 1, ascendente);
        quicksortPorPrecio(arreglo, pivoteIdx + 1, alto, ascendente);
    }

    private static int particionPrecio(Vuelo[] arr, int bajo, int alto, boolean ascendente) {
        if (arr == null || arr[alto] == null) return bajo;
        double pivote = arr[alto].obtenerPrecioBase();
        int i = bajo - 1;

        for (int j = bajo; j < alto; j++) {
            if (arr[j] == null) continue;
            boolean cumple = ascendente ? (arr[j].obtenerPrecioBase() <= pivote)
                                        : (arr[j].obtenerPrecioBase() >= pivote);
            if (cumple) {
                i++;
                intercambiar(arr, i, j);
            }
        }
        intercambiar(arr, i + 1, alto);
        return i + 1;
    }

    public static void quicksortPorCodigo(Vuelo[] arreglo, int bajo, int alto) {
        if (arreglo == null || bajo >= alto || bajo < 0 || alto >= arreglo.length) return;

        int pivoteIdx = particionCodigo(arreglo, bajo, alto);
        quicksortPorCodigo(arreglo, bajo, pivoteIdx - 1);
        quicksortPorCodigo(arreglo, pivoteIdx + 1, alto);
    }

    private static int particionCodigo(Vuelo[] arr, int bajo, int alto) {
        if (arr == null || arr[alto] == null || arr[alto].obtenerCodigo() == null) return bajo;
        String pivote = arr[alto].obtenerCodigo();
        int i = bajo - 1;

        for (int j = bajo; j < alto; j++) {
            if (arr[j] == null || arr[j].obtenerCodigo() == null) continue;
            if (arr[j].obtenerCodigo().compareToIgnoreCase(pivote) <= 0) {
                i++;
                intercambiar(arr, i, j);
            }
        }
        intercambiar(arr, i + 1, alto);
        return i + 1;
    }

    // 2. MERGESORT (Ordenamiento Estable Divide y Vencerás) O(n log n)

    public static void mergesortPorPrecio(Vuelo[] arreglo, boolean ascendente) {
        if (arreglo != null && arreglo.length > 1) {
            mergesortPorPrecio(arreglo, 0, arreglo.length - 1, ascendente);
        }
    }

    public static void mergesortPorDestino(Vuelo[] arreglo, boolean ascendente) {
        if (arreglo != null && arreglo.length > 1) {
            mergesortPorDestino(arreglo, 0, arreglo.length - 1, ascendente);
        }
    }

    public static void mergesortPorDestino(Vuelo[] arreglo, int izquierda, int derecha, boolean ascendente) {
        if (arreglo == null || izquierda >= derecha) return;

        int medio = izquierda + (derecha - izquierda) / 2;
        mergesortPorDestino(arreglo, izquierda, medio, ascendente);
        mergesortPorDestino(arreglo, medio + 1, derecha, ascendente);
        mezclarDestino(arreglo, izquierda, medio, derecha, ascendente);
    }

    private static void mezclarDestino(Vuelo[] arr, int izq, int med, int der, boolean ascendente) {
        int n1 = med - izq + 1;
        int n2 = der - med;

        Vuelo[] L = new Vuelo[n1];
        Vuelo[] R = new Vuelo[n2];

        System.arraycopy(arr, izq, L, 0, n1);
        System.arraycopy(arr, med + 1, R, 0, n2);

        int i = 0, j = 0, k = izq;
        while (i < n1 && j < n2) {
            String destL = (L[i] != null && L[i].obtenerDestino() != null) ? L[i].obtenerDestino() : "";
            String destR = (R[j] != null && R[j].obtenerDestino() != null) ? R[j].obtenerDestino() : "";
            int comp = destL.compareToIgnoreCase(destR);
            boolean cumple = ascendente ? (comp <= 0) : (comp >= 0);
            if (cumple) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public static void mergesortPorPrecio(Vuelo[] arreglo, int izquierda, int derecha, boolean ascendente) {
        if (arreglo == null || izquierda >= derecha) return;

        int medio = izquierda + (derecha - izquierda) / 2;
        mergesortPorPrecio(arreglo, izquierda, medio, ascendente);
        mergesortPorPrecio(arreglo, medio + 1, derecha, ascendente);
        mezclarPrecio(arreglo, izquierda, medio, derecha, ascendente);
    }

    private static void mezclarPrecio(Vuelo[] arr, int izq, int med, int der, boolean ascendente) {
        int n1 = med - izq + 1;
        int n2 = der - med;

        Vuelo[] L = new Vuelo[n1];
        Vuelo[] R = new Vuelo[n2];

        System.arraycopy(arr, izq, L, 0, n1);
        System.arraycopy(arr, med + 1, R, 0, n2);

        int i = 0, j = 0, k = izq;
        while (i < n1 && j < n2) {
            double pL = (L[i] != null) ? L[i].obtenerPrecioBase() : 0.0;
            double pR = (R[j] != null) ? R[j].obtenerPrecioBase() : 0.0;
            boolean cumple = ascendente ? (pL <= pR) : (pL >= pR);
            if (cumple) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // 3. HEAPSORT (Ordenamiento por Montículo) O(n log n)

    public static void heapsortPorPrecio(Vuelo[] arreglo, boolean ascendente) {
        if (arreglo == null || arreglo.length <= 1) return;
        int n = arreglo.length;

        // Construir Max-Heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            amontonarPrecio(arreglo, n, i, ascendente);
        }

        // Extraer elementos uno a uno
        for (int i = n - 1; i > 0; i--) {
            intercambiar(arreglo, 0, i);
            amontonarPrecio(arreglo, i, 0, ascendente);
        }
    }

    public static void shellsortPorPrecio(Vuelo[] arreglo, boolean ascendente) {
        if (arreglo == null || arreglo.length <= 1) return;
        int n = arreglo.length;
        for (int brecha = n / 2; brecha > 0; brecha /= 2) {
            for (int i = brecha; i < n; i++) {
                Vuelo temp = arreglo[i];
                int j = i;
                while (j >= brecha && arreglo[j - brecha] != null && temp != null &&
                       (ascendente ? (arreglo[j - brecha].obtenerPrecioBase() > temp.obtenerPrecioBase())
                                   : (arreglo[j - brecha].obtenerPrecioBase() < temp.obtenerPrecioBase()))) {
                    arreglo[j] = arreglo[j - brecha];
                    j -= brecha;
                }
                arreglo[j] = temp;
            }
        }
    }

    private static void amontonarPrecio(Vuelo[] arr, int n, int i, boolean ascendente) {
        int mayor = i;
        int izq = 2 * i + 1;
        int der = 2 * i + 2;

        if (ascendente) {
            if (izq < n && arr[izq] != null && arr[mayor] != null && arr[izq].obtenerPrecioBase() > arr[mayor].obtenerPrecioBase()) {
                mayor = izq;
            }
            if (der < n && arr[der] != null && arr[mayor] != null && arr[der].obtenerPrecioBase() > arr[mayor].obtenerPrecioBase()) {
                mayor = der;
            }
        } else {
            if (izq < n && arr[izq] != null && arr[mayor] != null && arr[izq].obtenerPrecioBase() < arr[mayor].obtenerPrecioBase()) {
                mayor = izq;
            }
            if (der < n && arr[der] != null && arr[mayor] != null && arr[der].obtenerPrecioBase() < arr[mayor].obtenerPrecioBase()) {
                mayor = der;
            }
        }

        if (mayor != i) {
            intercambiar(arr, i, mayor);
            amontonarPrecio(arr, n, mayor, ascendente);
        }
    }

    private static void intercambiar(Vuelo[] arr, int i, int j) {
        Vuelo temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // BENCHMARKING COMPARATIVO DE RENDIMIENTO

    public static String ejecutarBenchmark(Vuelo[] vuelosBase) {
        if (vuelosBase == null || vuelosBase.length == 0) {
            return "No hay vuelos para ejecutar el benchmark.";
        }

        int n = vuelosBase.length;
        Vuelo[] copia1 = clonar(vuelosBase);
        Vuelo[] copia2 = clonar(vuelosBase);
        Vuelo[] copia3 = clonar(vuelosBase);
        Vuelo[] copia4 = clonar(vuelosBase);
        Vuelo[] copia5 = clonar(vuelosBase);
        Vuelo[] copia6 = clonar(vuelosBase);

        // 1. Burbuja O(n^2)
        long tInicio = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (copia1[j] != null && copia1[j + 1] != null && copia1[j].obtenerPrecioBase() > copia1[j + 1].obtenerPrecioBase()) {
                    intercambiar(copia1, j, j + 1);
                }
            }
        }
        long tBurbuja = System.nanoTime() - tInicio;

        // 2. Inserción O(n^2)
        tInicio = System.nanoTime();
        for (int i = 1; i < n; i++) {
            Vuelo actual = copia2[i];
            int j = i - 1;
            while (j >= 0 && copia2[j] != null && actual != null && copia2[j].obtenerPrecioBase() > actual.obtenerPrecioBase()) {
                copia2[j + 1] = copia2[j];
                j--;
            }
            copia2[j + 1] = actual;
        }
        long tInsercion = System.nanoTime() - tInicio;

        // 3. Quicksort O(n log n)
        tInicio = System.nanoTime();
        quicksortPorPrecio(copia3, 0, n - 1, true);
        long tQuicksort = System.nanoTime() - tInicio;

        // 4. Mergesort O(n log n)
        tInicio = System.nanoTime();
        mergesortPorPrecio(copia4, 0, n - 1, true);
        long tMergesort = System.nanoTime() - tInicio;

        // 5. Heapsort O(n log n)
        tInicio = System.nanoTime();
        heapsortPorPrecio(copia5, true);
        long tHeapsort = System.nanoTime() - tInicio;

        // 6. Shellsort O(n^1.5)
        tInicio = System.nanoTime();
        shellsortPorPrecio(copia6, true);
        long tShellsort = System.nanoTime() - tInicio;

        StringBuilder sb = new StringBuilder();
        sb.append("    BENCHMARKING DE ALGORITMOS DE ORDENAMIENTO (SEMANA 15 - CORPAC)\n");
        sb.append(String.format("Volumen de Datos Evaluado: %d registros de vuelos comerciales\n\n", n));
        sb.append(String.format("%-22s %-16s %-18s %s\n", "Algoritmo", "Complejidad", "Tiempo (ns)", "Tiempo (µs)"));
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("%-22s %-16s %-18d %.2f µs\n", "Burbuja (Bubble)", "O(n²)", tBurbuja, tBurbuja / 1000.0));
        sb.append(String.format("%-22s %-16s %-18d %.2f µs\n", "Inserción (Insertion)", "O(n²)", tInsercion, tInsercion / 1000.0));
        sb.append(String.format("%-22s %-16s %-18d %.2f µs (Brechas h)\n", "Shellsort (Incrementos)", "O(n^1.5)", tShellsort, tShellsort / 1000.0));
        sb.append(String.format("%-22s %-16s %-18d %.2f µs (Recomendado)\n", "Quicksort (Hoare)", "O(n log n)", tQuicksort, tQuicksort / 1000.0));
        sb.append(String.format("%-22s %-16s %-18d %.2f µs (Estable)\n", "Mergesort (Mezcla)", "O(n log n)", tMergesort, tMergesort / 1000.0));
        sb.append(String.format("%-22s %-16s %-18d %.2f µs (Heap in-place)\n", "Heapsort (Montículo)", "O(n log n)", tHeapsort, tHeapsort / 1000.0));

        return sb.toString();
    }

    public static class ResultadoBenchmark {
        public final String nombre;
        public final String complejidad;
        public final long tiempoNanos;
        public final boolean estable;

        public ResultadoBenchmark(String nombre, String complejidad, long tiempoNanos, boolean estable) {
            this.nombre = nombre;
            this.complejidad = complejidad;
            this.tiempoNanos = tiempoNanos;
            this.estable = estable;
        }
    }

    public static List<ResultadoBenchmark> ejecutarBenchmarkComparativo(Vuelo[] vuelosBase) {
        List<ResultadoBenchmark> lista = new ArrayList<>();
        if (vuelosBase == null || vuelosBase.length == 0) return lista;

        int n = vuelosBase.length;
        Vuelo[] c1 = clonar(vuelosBase);
        Vuelo[] c2 = clonar(vuelosBase);
        Vuelo[] c3 = clonar(vuelosBase);
        Vuelo[] c4 = clonar(vuelosBase);
        Vuelo[] c5 = clonar(vuelosBase);
        Vuelo[] c6 = clonar(vuelosBase);

        // 1. Burbuja
        long tIni = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (c1[j] != null && c1[j + 1] != null && c1[j].obtenerPrecioBase() > c1[j + 1].obtenerPrecioBase()) {
                    intercambiar(c1, j, j + 1);
                }
            }
        }
        lista.add(new ResultadoBenchmark("Burbuja (Bubble)", "O(n²)", System.nanoTime() - tIni, true));

        // 2. Inserción
        tIni = System.nanoTime();
        for (int i = 1; i < n; i++) {
            Vuelo act = c2[i];
            int j = i - 1;
            while (j >= 0 && c2[j] != null && act != null && c2[j].obtenerPrecioBase() > act.obtenerPrecioBase()) {
                c2[j + 1] = c2[j];
                j--;
            }
            c2[j + 1] = act;
        }
        lista.add(new ResultadoBenchmark("Inserción (Insertion)", "O(n²)", System.nanoTime() - tIni, true));

        // 3. Quicksort
        tIni = System.nanoTime();
        quicksortPorPrecio(c3, 0, n - 1, true);
        lista.add(new ResultadoBenchmark("Quicksort (Hoare)", "O(n log n)", System.nanoTime() - tIni, false));

        // 4. Mergesort
        tIni = System.nanoTime();
        mergesortPorPrecio(c4, 0, n - 1, true);
        lista.add(new ResultadoBenchmark("Mergesort (Mezcla)", "O(n log n)", System.nanoTime() - tIni, true));

        // 5. Heapsort
        tIni = System.nanoTime();
        heapsortPorPrecio(c5, true);
        lista.add(new ResultadoBenchmark("Heapsort (Montículo)", "O(n log n)", System.nanoTime() - tIni, false));

        return lista;
    }

    private static Vuelo[] clonar(Vuelo[] orig) {
        Vuelo[] copia = new Vuelo[orig.length];
        System.arraycopy(orig, 0, copia, 0, orig.length);
        return copia;
    }
}