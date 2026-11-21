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
    private double altura = 11.76;
    private double pesoMaximoDespegue = 79000.0;
    private String rutaFotoHD = "airbus_a320neo_hd.jpg";
    private String rutaPlano = "airbus_a320neo_blueprint.jpg";
    private String resena = "El Airbus A320neo es la aeronave insignia de CORPAC para el transporte interdepartamental en el Perú.";
    private String destinoAsignado = "Cusco";
    private String codigoVueloAsignado = "CP-2023";

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
    public double obtenerAltura() { return altura; }
    public double obtenerPesoMaximoDespegue() { return pesoMaximoDespegue; }
    public String obtenerRutaFotoHD() { return (rutaFotoHD != null && !rutaFotoHD.isEmpty()) ? rutaFotoHD : "airbus_a320neo_hd.jpg"; }
    public String obtenerRutaPlano() { return (rutaPlano != null && !rutaPlano.isEmpty()) ? rutaPlano : "airbus_a320neo_blueprint.jpg"; }
    public String obtenerResena() { return (resena != null) ? resena : ""; }
    public String obtenerDestinoAsignado() { return (destinoAsignado != null) ? destinoAsignado : "Cusco"; }
    public String obtenerCodigoVueloAsignado() { return (codigoVueloAsignado != null) ? codigoVueloAsignado : "CP-2023"; }

    public void establecerModelo(String modelo) { this.modelo = modelo; }
    public void establecerFabricante(String fab) { this.fabricante = fab; }
    public void establecerMatricula(String mat) { this.matricula = mat; }
    public void establecerCapacidadReal(int cap) { this.capacidadReal = cap; }
    public void establecerVelocidadCrucero(double vel) { this.velocidadCrucero = vel; }
    public void establecerAlcanceMaximo(double alc) { this.alcanceMaximo = alc; }
    public void establecerTechoServicio(int techo) { this.techoServicio = techo; }
    public void establecerMotores(String mot) { this.motores = mot; }
    public void establecerEnvergadura(double env) { this.envergadura = env; }
    public void establecerLongitud(double lon) { this.longitud = lon; }
    public void establecerAltura(double alt) { this.altura = alt; }
    public void establecerPesoMaximoDespegue(double peso) { this.pesoMaximoDespegue = peso; }
    public void establecerRutaFotoHD(String foto) { this.rutaFotoHD = foto; }
    public void establecerRutaPlano(String plano) { this.rutaPlano = plano; }
    public void establecerResena(String resena) { this.resena = resena; }
    public void establecerDestinoAsignado(String dest) { this.destinoAsignado = dest; }
    public void establecerCodigoVueloAsignado(String cod) { this.codigoVueloAsignado = cod; }

    // Configuracion dinamica de aeronave segun destino y vuelo asignado en la flota CORPAC S.A.
    public void configurarSegunDestino(String destino, String codigoVuelo) {
        this.destinoAsignado = (destino != null && !destino.trim().isEmpty()) ? destino.trim() : "Cusco";
        this.codigoVueloAsignado = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : "CP-2023";
        String d = this.destinoAsignado.toLowerCase();

        if (d.contains("arequipa") || d.contains("trujillo") || d.contains("tacna")) {
            this.modelo = "Boeing 737-800 MAX (B737-8MAX)";
            this.fabricante = "Boeing Commercial Airplanes";
            this.matricula = "OB-2024-B";
            this.capacidadReal = 189;
            this.capacidadSimulada = 50;
            this.velocidadCrucero = 842.0;
            this.alcanceMaximo = 6570.0;
            this.techoServicio = 12500;
            this.motores = "2x CFM LEAP-1B28 Turbofán (29,300 lbf c/u)";
            this.envergadura = 35.92;
            this.longitud = 39.52;
            this.altura = 12.30;
            this.pesoMaximoDespegue = 82190.0;
            this.rutaFotoHD = "boeing_737_hd.jpg";
            this.rutaPlano = "airbus_a320neo_blueprint.jpg";
            this.resena = "Aeronave de pasillo único líder con winglets Split Scimitar, asignada a las rutas troncales de alta densidad del litoral y sur del Perú (Arequipa y Trujillo).";
        } else if (d.contains("piura") || d.contains("chiclayo") || d.contains("tumbes")) {
            this.modelo = "Airbus A321neo (A321-271NX)";
            this.fabricante = "Airbus SAS";
            this.matricula = "OB-2025-A";
            this.capacidadReal = 220;
            this.capacidadSimulada = 50;
            this.velocidadCrucero = 833.0;
            this.alcanceMaximo = 7400.0;
            this.techoServicio = 12100;
            this.motores = "2x CFM LEAP-1A32 Turbofán (32,160 lbf c/u)";
            this.envergadura = 35.80;
            this.longitud = 44.51;
            this.altura = 11.76;
            this.pesoMaximoDespegue = 97000.0;
            this.rutaFotoHD = "airbus_a321_hd.jpg";
            this.rutaPlano = "airbus_a320neo_blueprint.jpg";
            this.resena = "Versión de fuselaje extendido de máxima capacidad y bodega ampliada para el flujo comercial y turístico del norte peruano.";
        } else if (d.contains("iquitos") || d.contains("maldonado")) {
            this.modelo = "De Havilland Dash 8-Q400 (DHC-8)";
            this.fabricante = "De Havilland Canada";
            this.matricula = "OB-2026-D";
            this.capacidadReal = 78;
            this.capacidadSimulada = 50;
            this.velocidadCrucero = 667.0;
            this.alcanceMaximo = 2040.0;
            this.techoServicio = 8230;
            this.motores = "2x Pratt & Whitney Canada PW150A Turbopropela (5,071 shp c/u)";
            this.envergadura = 28.40;
            this.longitud = 32.81;
            this.altura = 8.30;
            this.pesoMaximoDespegue = 29250.0;
            this.rutaFotoHD = "dash8_q400_hd.jpg";
            this.rutaPlano = "airbus_a320neo_blueprint.jpg";
            this.resena = "Aeronave turbohélice de alta velocidad con capacidad STOL para pistas húmedas y clima tropical de la Amazonía peruana (Iquitos).";
        } else if (d.contains("tarapoto") || d.contains("ayacucho") || d.contains("jauja") || d.contains("cajamarca") || d.contains("pucallpa")) {
            this.modelo = "Embraer E190-E2 (ERJ-190-300)";
            this.fabricante = "Embraer";
            this.matricula = "OB-2027-E";
            this.capacidadReal = 114;
            this.capacidadSimulada = 50;
            this.velocidadCrucero = 870.0;
            this.alcanceMaximo = 5300.0;
            this.techoServicio = 12500;
            this.motores = "2x Pratt & Whitney PW1900G Geared Turbofan (23,000 lbf c/u)";
            this.envergadura = 33.72;
            this.longitud = 36.25;
            this.altura = 11.00;
            this.pesoMaximoDespegue = 56200.0;
            this.rutaFotoHD = "embraer_e190_hd.jpg";
            this.rutaPlano = "airbus_a320neo_blueprint.jpg";
            this.resena = "Jet regional moderno con ala de alta eficiencia y motores turbofán engranados ultra silenciosos para aproximaciones en valles interandinos y selva alta.";
        } else {
            // Cusco, Juliaca y rutas de altura estándar
            this.modelo = "Airbus A320neo (A320-271N)";
            this.fabricante = "Airbus SAS";
            this.matricula = "CC-BHB";
            this.capacidadReal = 180;
            this.capacidadSimulada = 50;
            this.velocidadCrucero = 828.0;
            this.alcanceMaximo = 6500.0;
            this.techoServicio = 12100;
            this.motores = "2x CFM International LEAP-1A26 Turbofán (27,120 lbf c/u)";
            this.envergadura = 35.80;
            this.longitud = 37.57;
            this.altura = 11.76;
            this.pesoMaximoDespegue = 79000.0;
            this.rutaFotoHD = "airbus_a320neo_hd.jpg";
            this.rutaPlano = "airbus_a320neo_blueprint.jpg";
            this.resena = "Aeronave insignia de CORPAC con motores LEAP-1A y dispositivos Sharklets, certificada para despegues y aterrizajes seguros en aeropuertos de altura como Cusco y Juliaca.";
        }
    }

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