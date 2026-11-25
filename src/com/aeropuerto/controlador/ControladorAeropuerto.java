package com.aeropuerto.controlador;

import com.aeropuerto.dao.AeropuertoDAO;
import com.aeropuerto.dao.AsientoDAO;
import com.aeropuerto.dao.EquipajeDAO;
import com.aeropuerto.dao.PuertaDAO;
import com.aeropuerto.dao.RutaDAO;
import com.aeropuerto.dao.SupervisorDAO;
import com.aeropuerto.dao.UsuarioDAO;
import com.aeropuerto.dao.VueloDAO;
import com.aeropuerto.estructuras.Cola;
import com.aeropuerto.estructuras.ListaCircular;
import com.aeropuerto.estructuras.ListaDoble;
import com.aeropuerto.estructuras.ListaSimple;
import com.aeropuerto.estructuras.Pila;
import com.aeropuerto.modelo.AccionReserva;
import com.aeropuerto.modelo.Aeropuerto;
import com.aeropuerto.modelo.Avion;
import com.aeropuerto.modelo.Equipaje;
import com.aeropuerto.modelo.PuertaEmbarque;
import com.aeropuerto.modelo.Ruta;
import com.aeropuerto.modelo.Supervisor;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.modelo.Vuelo;
import com.aeropuerto.util.ConexionBD;
import com.aeropuerto.estructuras.grafo.Vertice;
import com.aeropuerto.estructuras.arbol.ArbolAVL;
import com.aeropuerto.estructuras.grafo.ResultadoMST;
import com.aeropuerto.estructuras.grafo.ResultadoFloydWarshall;
import com.aeropuerto.estructuras.hash.TablaHash;
import com.aeropuerto.estructuras.heap.MonticuloBinario;
import com.aeropuerto.estructuras.ordenamiento.AlgoritmosOrdenamientoAvanzado;
import com.aeropuerto.servicio.ServicioAeropuerto;

import java.util.List;

// Controlador principal del sistema aeroportuario
public class ControladorAeropuerto {

    // Catalogo de vuelos en arreglo unidimensional dinamico
    private Vuelo[] vuelos;
    private int contadorVuelos;
    private boolean estaOrdenadoPorCodigo = false;

    // Matriz de asientos (10x5) y aeronave
    private Avion avion;
    private String codigoVueloActivo;

    // Listas dinamicas (TADs)
    private ListaSimple listaEquipaje;
    private ListaDoble listaRutas;
    private ListaCircular listaPuertas;

    // Pilas y Colas (TADs LIFO y FIFO)
    private Pila pilaDeshacerReservas;
    private Cola colaPistaDespegue;

    // Control de Aeropuertos y Pistas (Arreglo dinamico)
    private Aeropuerto[] aeropuertos;
    private int contadorAeropuertos;

    // Supervisores de operaciones y torre de control (Arreglo dinamico)
    private Supervisor[] supervisores;
    private int contadorSupervisores;

    // Sesion actual
    private Usuario usuarioActual;

    // Capa de persistencia DAO
    private VueloDAO vueloDAO;
    private AsientoDAO asientoDAO;
    private UsuarioDAO usuarioDAO;
    private EquipajeDAO equipajeDAO;
    private RutaDAO rutaDAO;
    private PuertaDAO puertaDAO;
    private AeropuertoDAO aeropuertoDAO;
    private SupervisorDAO supervisorDAO;

    public ControladorAeropuerto() {
        this.vuelos = new Vuelo[20];
        this.contadorVuelos = 0;
        this.avion = new Avion();
        this.codigoVueloActivo = "";

        this.listaEquipaje = new ListaSimple();
        this.listaRutas = new ListaDoble();
        this.listaPuertas = new ListaCircular();
        this.pilaDeshacerReservas = new Pila();
        this.colaPistaDespegue = new Cola();

        this.aeropuertos = new Aeropuerto[20];
        this.contadorAeropuertos = 0;
        this.supervisores = new Supervisor[20];
        this.contadorSupervisores = 0;

        this.vueloDAO = new VueloDAO();
        this.asientoDAO = new AsientoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.equipajeDAO = new EquipajeDAO();
        this.rutaDAO = new RutaDAO();
        this.puertaDAO = new PuertaDAO();
        this.aeropuertoDAO = new AeropuertoDAO();
        this.supervisorDAO = new SupervisorDAO();

        inicializarDatosBase();
        cargarDesdeBD();
        sincronizarIndiceArbol();
    }

    // Inicializar datos base en memoria (inicio limpio y vacio para registro manual)
    private void inicializarDatosBase() {
        this.contadorVuelos = 0;
        this.codigoVueloActivo = "";
        this.listaEquipaje = new ListaSimple();
        this.listaRutas = new ListaDoble();
        this.listaPuertas = new ListaCircular();
        this.pilaDeshacerReservas = new Pila();
        this.colaPistaDespegue = new Cola();
        this.aeropuertos = new Aeropuerto[20];
        this.contadorAeropuertos = 0;
        this.supervisores = new Supervisor[20];
        this.contadorSupervisores = 0;
    }

    // Carga opcional de datos de demostracion
    public void cargarDatosDemostracion() {
        agregarVuelo(new Vuelo("CP-2023", "Lima", "Cusco", "2026-09-15", "06:00", "07:20", 80, 150.0, "Turista"));
        agregarVuelo(new Vuelo("CP-2024", "Lima", "Arequipa", "2026-09-15", "08:30", "10:00", 90, 140.0, "Ejecutiva"));
        agregarVuelo(new Vuelo("CP-2025", "Lima", "Piura", "2026-09-15", "11:15", "13:00", 105, 160.0, "Turista"));
        agregarVuelo(new Vuelo("CP-2026", "Lima", "Iquitos", "2026-09-15", "14:00", "15:55", 115, 180.0, "Primera"));
        agregarVuelo(new Vuelo("CP-2027", "Lima", "Tarapoto", "2026-09-15", "17:30", "18:45", 75, 130.0, "Turista"));

        seleccionarVueloActivo("CP-2023");
        ocuparAsiento(0, 0, "45892147");
        ocuparAsiento(1, 4, "72314589");
        ocuparAsiento(3, 2, "10234567");

        agregarEquipaje(new Equipaje("EQ101", "45892147", "CP-2023", 18.5, "Bodega"));
        agregarEquipaje(new Equipaje("EQ102", "72314589", "CP-2023", 8.0, "Cabina"));
        agregarEquipaje(new Equipaje("EQ103", "10234567", "CP-2024", 24.5, "Bodega"));

        agregarRuta(new Ruta("RT301", "Lima", "Cusco", 580.0));
        agregarRuta(new Ruta("RT302", "Lima", "Arequipa", 760.0));
        agregarRuta(new Ruta("RT303", "Lima", "Iquitos", 1010.0));
        agregarRuta(new Ruta("RT304", "Lima", "Piura", 850.0));

        cargarPuertasEstandar();
        cargarAeropuertosEstandar();
        cargarSupervisoresEstandar();

        if (colaPistaDespegue == null || colaPistaDespegue.estaVacia()) {
            colaPistaDespegue = new Cola();
            encolarVueloPista("CP-2023");
            encolarVueloPista("CP-2024");
        }
        sincronizarIndiceArbol();
    }

    // Inicializar 20 puertas estandar en el terminal (TAD Lista Circular)
    public void cargarPuertasEstandar() {
        this.listaPuertas = new ListaCircular();
        for (int i = 1; i <= 20; i++) {
            String terminal = (i <= 10) ? "Terminal 1" : "Terminal 2";
            String num = String.format("Puerta %02d", i);
            agregarPuerta(new PuertaEmbarque(num, terminal, "Disponible", "Sin asignar"));
        }
    }

    // Garantiza que existan vuelos comerciales con variaciones para el modo Pasajero
    public void asegurarVuelosComerciales() {
        if (contadorVuelos == 0) {
            // Cargar catalogo nacional con variaciones comerciales
            agregarVueloLocal(new Vuelo("CP-2024", "Lima", "Arequipa", "2026-10-06", "08:30", "10:00", 90, 139.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2034", "Lima", "Arequipa", "2026-10-06", "14:15", "15:45", 90, 165.0, "Ejecutiva"));
            agregarVueloLocal(new Vuelo("CP-2044", "Lima", "Arequipa", "2026-10-06", "20:00", "21:30", 90, 125.0, "Turista"));

            agregarVueloLocal(new Vuelo("CP-2023", "Lima", "Cusco", "2026-10-06", "06:00", "07:20", 80, 149.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2033", "Lima", "Cusco", "2026-10-06", "11:45", "13:05", 80, 175.0, "Ejecutiva"));
            agregarVueloLocal(new Vuelo("CP-2043", "Lima", "Cusco", "2026-10-06", "16:30", "17:50", 80, 139.0, "Turista"));

            agregarVueloLocal(new Vuelo("CP-2025", "Lima", "Piura", "2026-10-06", "08:10", "09:55", 105, 155.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2035", "Lima", "Piura", "2026-10-06", "17:30", "19:15", 105, 170.0, "Ejecutiva"));

            agregarVueloLocal(new Vuelo("CP-2026", "Lima", "Iquitos", "2026-10-06", "09:20", "11:15", 115, 180.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2036", "Lima", "Iquitos", "2026-10-06", "15:00", "16:55", 115, 195.0, "Ejecutiva"));

            agregarVueloLocal(new Vuelo("CP-2027", "Lima", "Tarapoto", "2026-10-06", "07:15", "08:30", 75, 130.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2037", "Lima", "Tarapoto", "2026-10-06", "18:20", "19:35", 75, 145.0, "Ejecutiva"));

            agregarVueloLocal(new Vuelo("CP-2028", "Lima", "Trujillo", "2026-10-06", "07:45", "08:55", 70, 119.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2038", "Lima", "Trujillo", "2026-10-06", "15:10", "16:20", 70, 135.0, "Ejecutiva"));

            agregarVueloLocal(new Vuelo("CP-2029", "Lima", "Chiclayo", "2026-10-06", "08:40", "10:00", 80, 125.0, "Turista"));
            agregarVueloLocal(new Vuelo("CP-2039", "Lima", "Chiclayo", "2026-10-06", "19:00", "20:20", 80, 140.0, "Ejecutiva"));

            seleccionarVueloActivo("CP-2024");
            ocuparAsiento(0, 0, "45892147");
            ocuparAsiento(0, 1, "72314589");
            ocuparAsiento(2, 3, "10234567");

            cargarPuertasEstandar();
            cargarAeropuertosEstandar();
            cargarSupervisoresEstandar();

            if (listaRutas == null || listaRutas.estaVacia()) {
                listaRutas = new ListaDoble();
                listaRutas.agregarRuta(new Ruta("RT301", "Lima", "Cusco", 580.0));
                listaRutas.agregarRuta(new Ruta("RT302", "Lima", "Arequipa", 760.0));
                listaRutas.agregarRuta(new Ruta("RT303", "Lima", "Iquitos", 1010.0));
                listaRutas.agregarRuta(new Ruta("RT304", "Lima", "Piura", 850.0));
                listaRutas.agregarRuta(new Ruta("RT305", "Lima", "Tarapoto", 620.0));
                listaRutas.agregarRuta(new Ruta("RT306", "Lima", "Trujillo", 490.0));
            }
            if (listaEquipaje == null || listaEquipaje.estaVacia()) {
                listaEquipaje = new ListaSimple();
                listaEquipaje.agregarMaleta(new Equipaje("EQ101", "45892147", "CP-2024", 18.5, "Bodega"));
                listaEquipaje.agregarMaleta(new Equipaje("EQ102", "72314589", "CP-2024", 8.0, "Cabina"));
                listaEquipaje.agregarMaleta(new Equipaje("EQ103", "10234567", "CP-2024", 24.5, "Bodega"));
            }
            if (colaPistaDespegue == null || colaPistaDespegue.estaVacia()) {
                colaPistaDespegue = new Cola();
                if (buscarVueloPorCodigo("CP-2024") != null) colaPistaDespegue.encolar(buscarVueloPorCodigo("CP-2024"));
                if (buscarVueloPorCodigo("CP-2023") != null) colaPistaDespegue.encolar(buscarVueloPorCodigo("CP-2023"));
            }
        } else if (codigoVueloActivo == null || codigoVueloActivo.isEmpty()) {
            seleccionarVueloActivo(vuelos[0].obtenerCodigo());
        }
    }

    // Asegura que un destino en particular tenga vuelos y variaciones
    public void asegurarVariacionesDestino(String destino) {
        if (destino == null || destino.trim().isEmpty() || "Todos".equalsIgnoreCase(destino)) return;
        int encontrados = 0;
        String destNorm = destino.trim().toLowerCase();
        for (int i = 0; i < contadorVuelos; i++) {
            if (vuelos[i] != null) {
                String vDest = vuelos[i].obtenerDestino().toLowerCase();
                if (vDest.contains(destNorm) || destNorm.contains(vDest)) {
                    encontrados++;
                }
            }
        }
        if (encontrados == 0) {
            String cod1 = "CP-" + (2050 + contadorVuelos);
            String cod2 = "CP-" + (2051 + contadorVuelos);
            agregarVueloLocal(new Vuelo(cod1, "Lima (Jorge Chavez)", destino, "2026-10-14", "08:00", "09:30", 90, 140.0, "Turista"));
            agregarVueloLocal(new Vuelo(cod2, "Lima (Jorge Chavez)", destino, "2026-10-14", "17:00", "18:30", 90, 160.0, "Ejecutiva"));
        }
    }

    // Insercion local en arreglo
    private void agregarVueloLocal(Vuelo vuelo) {
        if (vuelo == null) return;
        if (contadorVuelos >= vuelos.length) {
            redimensionarArregloVuelos();
        }
        vuelos[contadorVuelos] = vuelo;
        contadorVuelos++;
        estaOrdenadoPorCodigo = false;
        sincronizarIndiceArbol();
    }

    // Redimensionamiento dinamico de arreglos
    private void redimensionarArregloVuelos() {
        int nuevaCapacidad = vuelos.length * 2;
        Vuelo[] nuevoArreglo = new Vuelo[nuevaCapacidad];
        for (int i = 0; i < contadorVuelos; i++) {
            nuevoArreglo[i] = vuelos[i];
        }
        this.vuelos = nuevoArreglo;
    }

    // Agregar vuelo al arreglo y a MySQL sin limites artificiales
    public String agregarVuelo(Vuelo vuelo) {
        if (vuelo == null) {
            return "Error: Datos del vuelo inválidos.";
        }
        if (buscarVueloPorCodigo(vuelo.obtenerCodigo()) != null) {
            return "Error: Ya existe un vuelo con el código " + vuelo.obtenerCodigo();
        }

        if (contadorVuelos >= vuelos.length) {
            redimensionarArregloVuelos();
        }

        vuelos[contadorVuelos] = vuelo;
        contadorVuelos++;
        estaOrdenadoPorCodigo = false;
        sincronizarIndiceArbol();

        if (codigoVueloActivo == null || codigoVueloActivo.isEmpty()) {
            seleccionarVueloActivo(vuelo.obtenerCodigo());
        }

        if (ConexionBD.estaConectado()) {
            vueloDAO.insertar(vuelo);
            return "Vuelo " + vuelo.obtenerCodigo() + " registrado en Arreglo y MySQL.";
        }
        return "Vuelo " + vuelo.obtenerCodigo() + " registrado en Arreglo (Modo Local).";
    }

    // Actualizar datos de un vuelo
    public boolean actualizarVuelo(int indice, Vuelo nuevoVuelo) {
        if (indice < 0 || indice >= contadorVuelos || nuevoVuelo == null) {
            return false;
        }
        // Conservar matrices de asientos existentes si corresponden al mismo vuelo
        Vuelo previo = vuelos[indice];
        if (previo != null) {
            nuevoVuelo.establecerMatrizAsientos(previo.obtenerMatrizAsientos());
            nuevoVuelo.establecerMatrizPasajeros(previo.obtenerMatrizPasajeros());
        }
        vuelos[indice] = nuevoVuelo;
        estaOrdenadoPorCodigo = false;
        if (nuevoVuelo.obtenerCodigo() != null && nuevoVuelo.obtenerCodigo().equalsIgnoreCase(codigoVueloActivo)) {
            sincronizarAvionConVuelo(nuevoVuelo);
        }
        if (ConexionBD.estaConectado()) {
            vueloDAO.actualizar(nuevoVuelo);
        }
        return true;
    }

    // Eliminar vuelo con corrimiento fisico hacia la izquierda
    public boolean eliminarVuelo(int indice) {
        if (indice < 0 || indice >= contadorVuelos) {
            return false;
        }
        String codigoEliminado = vuelos[indice].obtenerCodigo();

        for (int i = indice; i < contadorVuelos - 1; i++) {
            vuelos[i] = vuelos[i + 1];
        }
        contadorVuelos--;
        vuelos[contadorVuelos] = null;
        estaOrdenadoPorCodigo = false;
        sincronizarIndiceArbol();

        if (codigoEliminado.equalsIgnoreCase(codigoVueloActivo)) {
            if (contadorVuelos > 0) {
                seleccionarVueloActivo(vuelos[0].obtenerCodigo());
            } else {
                this.codigoVueloActivo = "";
                this.avion = new Avion();
            }
        }

        if (ConexionBD.estaConectado()) {
            vueloDAO.eliminar(codigoEliminado);
        }
        return true;
    }

    // Busqueda secuencial lineal por codigo O(n)
    public Vuelo buscarVueloPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        String buscado = codigo.trim();
        for (int i = 0; i < contadorVuelos; i++) {
            if (vuelos[i] != null && vuelos[i].obtenerCodigo() != null && vuelos[i].obtenerCodigo().equalsIgnoreCase(buscado)) {
                return vuelos[i];
            }
        }
        return null;
    }

    // Ordenamiento del arreglo por precio (Algoritmo de insercion)
    public void ordenarVuelosPorPrecio(boolean ascendente) {
        for (int i = 1; i < contadorVuelos; i++) {
            Vuelo actual = vuelos[i];
            if (actual == null) continue;
            int j = i - 1;
            if (ascendente) {
                while (j >= 0 && vuelos[j] != null && vuelos[j].obtenerPrecioBase() > actual.obtenerPrecioBase()) {
                    vuelos[j + 1] = vuelos[j];
                    j--;
                }
            } else {
                while (j >= 0 && vuelos[j] != null && vuelos[j].obtenerPrecioBase() < actual.obtenerPrecioBase()) {
                    vuelos[j + 1] = vuelos[j];
                    j--;
                }
            }
            vuelos[j + 1] = actual;
        }
        estaOrdenadoPorCodigo = false;
    }

    // Ordenamiento del arreglo por codigo (Burbuja)
    public void ordenarVuelosPorCodigo() {
        for (int i = 0; i < contadorVuelos - 1; i++) {
            for (int j = 0; j < contadorVuelos - 1 - i; j++) {
                if (vuelos[j] != null && vuelos[j + 1] != null &&
                    vuelos[j].obtenerCodigo() != null && vuelos[j + 1].obtenerCodigo() != null) {
                    if (vuelos[j].obtenerCodigo().compareToIgnoreCase(vuelos[j + 1].obtenerCodigo()) > 0) {
                        Vuelo temporal = vuelos[j];
                        vuelos[j] = vuelos[j + 1];
                        vuelos[j + 1] = temporal;
                    }
                }
            }
        }
        estaOrdenadoPorCodigo = true;
    }

    // Clonacion de arreglo de vuelos
    public Vuelo[] clonarArregloVuelos() {
        Vuelo[] copia = new Vuelo[contadorVuelos];
        for (int i = 0; i < contadorVuelos; i++) {
            copia[i] = vuelos[i];
        }
        return copia;
    }

    // Comparacion de arreglos de vuelos (Unidad 1 - Silabo)
    public boolean compararArreglosVuelos(Vuelo[] arr1, Vuelo[] arr2) {
        if (arr1 == null || arr2 == null) return arr1 == arr2;
        if (arr1.length != arr2.length) return false;
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] == null && arr2[i] == null) continue;
            if (arr1[i] == null || arr2[i] == null) return false;
            if (!arr1[i].obtenerCodigo().equalsIgnoreCase(arr2[i].obtenerCodigo())) {
                return false;
            }
        }
        return true;
    }

    public String compararArreglosVuelos() {
        if (contadorVuelos == 0) return "El catálogo de vuelos se encuentra actualmente vacío (0 vuelos).";
        Vuelo[] copia = clonarArregloVuelos();
        boolean sonIguales = compararArreglosVuelos(vuelos, copia);
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPARACIÓN FORMAL DE ARREGLOS VUELO[] (UNIDAD 1 - SÍLABO UTP) ===\n\n");
        sb.append("Arreglo Principal (vuelos[]): ").append(contadorVuelos).append(" vuelos en memoria activa.\n");
        sb.append("Arreglo Respaldo (copia[]):   ").append(copia.length).append(" vuelos clonados de forma física independiente.\n\n");
        sb.append("Algoritmo Evaluado: Comparación secuencial posición por posición O(n).\n");
        sb.append("Resultado: ").append(sonIguales ? "COINCIDENCIA TOTAL (Arreglos idénticos en código y ordenación)" : "DISCREPANTES").append("\n\n");
        sb.append("Muestra de los primeros vuelos comparados:\n");
        int limite = Math.min(contadorVuelos, 8);
        for (int i = 0; i < limite; i++) {
            sb.append(String.format("  [%02d] Principal: %-8s | Respaldo: %-8s | Estado: COINCIDE\n",
                    i, vuelos[i].obtenerCodigo(), copia[i].obtenerCodigo()));
        }
        if (contadorVuelos > limite) {
            sb.append("  ... y ").append(contadorVuelos - limite).append(" vuelos adicionales verificados con éxito.\n");
        }
        return sb.toString();
    }

    // Fusion de dos arreglos de vuelos en uno nuevo (Unidad 1 - Silabo)
    public Vuelo[] fusionarArreglosVuelos(Vuelo[] arr1, Vuelo[] arr2) {
        int n1 = (arr1 != null) ? arr1.length : 0;
        int n2 = (arr2 != null) ? arr2.length : 0;
        Vuelo[] fusion = new Vuelo[n1 + n2];
        for (int i = 0; i < n1; i++) {
            fusion[i] = arr1[i];
        }
        for (int j = 0; j < n2; j++) {
            fusion[n1 + j] = arr2[j];
        }
        return fusion;
    }

    public String fusionarArreglosVuelos() {
        if (contadorVuelos == 0) return "El catálogo de vuelos se encuentra actualmente vacío (0 vuelos).";
        Vuelo[] itinerarioEspecial = new Vuelo[] {
            new Vuelo("CP-8001", "Lima", "Chachapoyas", "2026-12-01", "06:30", "08:00", 90, 240.0, "Turista"),
            new Vuelo("CP-8002", "Lima", "Tumbes", "2026-12-01", "07:15", "09:10", 115, 275.0, "Turista")
        };
        Vuelo[] activos = new Vuelo[contadorVuelos];
        System.arraycopy(vuelos, 0, activos, 0, contadorVuelos);
        Vuelo[] fusion = fusionarArreglosVuelos(activos, itinerarioEspecial);

        StringBuilder sb = new StringBuilder();
        sb.append("=== FUSIÓN DE ARREGLOS ESTÁTICOS VUELO[] (UNIDAD 1 - SÍLABO UTP) ===\n\n");
        sb.append("Arreglo Base A (Operaciones Troncales): ").append(contadorVuelos).append(" vuelos activos.\n");
        sb.append("Arreglo B (Rutas Especiales/Chachapoyas y Tumbes): ").append(itinerarioEspecial.length).append(" vuelos adicionales.\n");
        sb.append("Nuevo Arreglo Resultante (A + B): ").append(fusion.length).append(" vuelos en nuevo bloque continuo O(n1 + n2).\n\n");
        sb.append("Detalle del nuevo bloque de memoria fusionado:\n");
        for (int i = 0; i < fusion.length; i++) {
            Vuelo v = fusion[i];
            sb.append(String.format("  [%02d] %-8s | %-15s -> %-15s | Tarifa: S/. %6.2f | %s\n",
                    i + 1, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino(), v.obtenerPrecioBase(), v.obtenerClaseDisponible()));
        }
        return sb.toString();
    }

    // Recorrido secuencial del arreglo unidimensional Vuelo[]
    public String recorrerArregloVuelos() {
        if (contadorVuelos == 0) {
            return "El arreglo unidimensional de vuelos se encuentra actualmente vacío (0 elementos).";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append(" RECORRIDO SECUENCIAL DEL ARREGLO ESTÁTICO VUELO[] (Capacidad física: ").append(vuelos.length).append(")\n");
        sb.append("=======================================================================\n");
        for (int i = 0; i < contadorVuelos; i++) {
            Vuelo v = vuelos[i];
            sb.append(String.format("Índice [%02d] -> Código: %-8s | Origen: %-8s | Destino: %-10s | Salida: %-5s | Tarifa: S/.%6.2f | Libres: %02d/50\n",
                    i, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino(), v.obtenerHoraSalida(), v.obtenerPrecioBase(), v.contarAsientosLibres()));
        }
        sb.append("-----------------------------------------------------------------------\n");
        sb.append(String.format("- Ocupación lógica del arreglo: %d elementos.\n", contadorVuelos));
        sb.append(String.format("- Celdas contiguas disponibles (libres): %d celdas.\n", (vuelos.length - contadorVuelos)));
        sb.append(String.format("- Integridad contigua: 100%% sin celdas nulas intermedias.\n"));
        return sb.toString();
    }

    // Copia física independiente del arreglo de objetos Vuelo[]
    public String copiarArregloVuelosConReporte() {
        Vuelo[] copia = clonarArregloVuelos();
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append(" COPIA FÍSICA E INDEPENDIENTE DEL ARREGLO DE OBJETOS VUELO[]\n");
        sb.append("=======================================================================\n");
        sb.append(String.format("- Arreglo Original: Capacidad = %d | Ocupados = %d | Memoria Referencial = @%x\n",
                vuelos.length, contadorVuelos, System.identityHashCode(vuelos)));
        sb.append(String.format("- Arreglo Duplicado (Copia): Longitud = %d | Elementos = %d | Memoria Referencial = @%x\n",
                copia.length, copia.length, System.identityHashCode(copia)));
        sb.append(String.format("- Verificación de Independencia: (original != copia) -> %b\n", (vuelos != copia)));
        sb.append("-----------------------------------------------------------------------\n");
        sb.append("Detalle de elementos transferidos a la nueva dirección contigua de memoria:\n");
        for (int i = 0; i < copia.length; i++) {
            sb.append(String.format("  [Copia %02d]: %s (%s -> %s) - S/.%.2f\n",
                    i, copia[i].obtenerCodigo(), copia[i].obtenerOrigen(), copia[i].obtenerDestino(), copia[i].obtenerPrecioBase()));
        }
        return sb.toString();
    }

    // Recorrido exhaustivo por filas y columnas de la Matriz 10x5 de Cabina
    public String recorrerMatrizCabina() {
        Vuelo v = buscarVueloPorCodigo(codigoVueloActivo);
        if (v == null && contadorVuelos > 0) v = vuelos[0];
        if (v == null) {
            return "No hay vuelo activo seleccionado para recorrer la cabina.";
        }
        int[][] mat = v.obtenerMatrizAsientos();
        String[][] pas = v.obtenerMatrizPasajeros();
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append(" RECORRIDO BIDIMENSIONAL DE LA CABINA AIRBUS A320neo [10 Filas x 5 Columnas]\n");
        sb.append(" Vuelo Activo: ").append(v.obtenerCodigo()).append(" (").append(v.obtenerOrigen()).append(" -> ").append(v.obtenerDestino()).append(")\n");
        sb.append("=======================================================================\n");
        int ocupados = 0;
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 5; c++) {
                char letraCol = (char) ('A' + c);
                String tipoZona = (c == 0 || c == 4) ? "Ventana" : (c == 1 || c == 3) ? "Pasillo" : "Centro";
                boolean esVip = (f < 3);
                double factorZona = (c == 0 || c == 4) ? 1.30 : (c == 1 || c == 3) ? 1.10 : 1.00;
                double factorFila = esVip ? 1.20 : 1.00;
                double tarifaAsiento = v.obtenerPrecioBase() * factorZona * factorFila;
                boolean ocupado = (mat[f][c] == 1);
                if (ocupado) ocupados++;
                String dni = (pas != null && pas[f][c] != null) ? pas[f][c] : "N/A";
                sb.append(String.format("Asiento [%02d%c] (Fila %d, Col %d) | %-7s %s | Factor: %4.2fx | Tarifa: S/.%6.2f | Estado: %-7s | Pasajero: %s\n",
                        (f + 1), letraCol, f, c, tipoZona, (esVip ? "[VIP]" : "[EST]"), (factorZona * factorFila), tarifaAsiento,
                        (ocupado ? "OCUPADO" : "LIBRE"), dni));
            }
        }
        sb.append("-----------------------------------------------------------------------\n");
        sb.append(String.format("- Total Asientos Ocupados: %d / 50 (%.1f%%)\n", ocupados, (ocupados * 100.0 / 50)));
        sb.append(String.format("- Total Asientos Disponibles: %d / 50\n", (50 - ocupados)));
        sb.append(String.format("- Complejidad del Recorrido: O(Filas * Columnas) = O(10 * 5) = O(50) operaciones elementales.\n"));
        return sb.toString();
    }

    // Estadisticas del catalogo
    public String obtenerEstadisticasCatalogo() {
        if (contadorVuelos == 0) return "No hay vuelos registrados.";
        double sumaTarifas = 0.0;
        Vuelo minVuelo = vuelos[0];
        Vuelo maxVuelo = vuelos[0];

        for (int i = 0; i < contadorVuelos; i++) {
            Vuelo v = vuelos[i];
            sumaTarifas += v.obtenerPrecioBase();
            if (v.obtenerPrecioBase() < minVuelo.obtenerPrecioBase()) minVuelo = v;
            if (v.obtenerPrecioBase() > maxVuelo.obtenerPrecioBase()) maxVuelo = v;
        }

        double promedio = sumaTarifas / contadorVuelos;
        return String.format(java.util.Locale.US,
                "ESTADÍSTICAS DEL CATÁLOGO DE VUELOS:\n" +
                "- Total de vuelos programados: %d\n" +
                "- Tarifa promedio: S/. %.2f\n" +
                "- Vuelo más económico: %s (S/. %.2f)\n" +
                "- Vuelo de mayor tarifa: %s (S/. %.2f)",
                contadorVuelos, promedio, minVuelo.obtenerCodigo(), minVuelo.obtenerPrecioBase(),
                maxVuelo.obtenerCodigo(), maxVuelo.obtenerPrecioBase());
    }

    // Operaciones de Cabina por Vuelo Activo
    public void seleccionarVueloActivo(String codigoVuelo) {
        if (codigoVuelo == null || codigoVuelo.trim().isEmpty()) return;
        Vuelo v = buscarVueloPorCodigo(codigoVuelo);
        if (v != null) {
            this.codigoVueloActivo = v.obtenerCodigo();
            sincronizarAvionConVuelo(v);
        }
    }

    private void sincronizarAvionConVuelo(Vuelo v) {
        if (v == null) return;
        avion.configurarSegunDestino(v.obtenerDestino(), v.obtenerCodigo());
        int[][] asientosVuelo = v.obtenerMatrizAsientos();
        if (asientosVuelo == null || asientosVuelo.length < 10) return;
        for (int f = 0; f < 10; f++) {
            if (asientosVuelo[f] == null || asientosVuelo[f].length < 5) continue;
            for (int c = 0; c < 5; c++) {
                if (asientosVuelo[f][c] == 1) {
                    avion.ocuparAsiento(f, c);
                } else {
                    avion.liberarAsiento(f, c);
                }
            }
        }
    }

    public boolean ocuparAsiento(int fila, int columna, String dniPasajero) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) return false;
        Vuelo vActivo = buscarVueloPorCodigo(codigoVueloActivo);
        if (vActivo == null) return false;

        // Regla estricta de negocio: Un pasajero (DNI) solo puede tener un único asiento en el vuelo.
        // Si ya tenía un asiento previo en este vuelo, se libera automáticamente antes de asignar el nuevo.
        if (dniPasajero != null && !dniPasajero.trim().isEmpty() && !"Reservado".equalsIgnoreCase(dniPasajero.trim())) {
            String dniNorm = dniPasajero.trim();
            String[][] matPas = vActivo.obtenerMatrizPasajeros();
            if (matPas != null) {
                for (int f = 0; f < 10; f++) {
                    for (int c = 0; c < 5; c++) {
                        if ((f != fila || c != columna) && dniNorm.equals(matPas[f][c])) {
                            liberarAsiento(f, c);
                        }
                    }
                }
            }
        }

        boolean exito = vActivo.ocuparAsiento(fila, columna, dniPasajero);
        if (exito) {
            avion.ocuparAsiento(fila, columna);
            if (pilaDeshacerReservas != null) {
                pilaDeshacerReservas.apilar(new AccionReserva(codigoVueloActivo, fila, columna, dniPasajero));
            }
            if (ConexionBD.estaConectado()) {
                asientoDAO.ocuparAsiento(codigoVueloActivo, fila, columna, dniPasajero);
            }
            return true;
        }
        return false;
    }

    public boolean liberarAsiento(int fila, int columna) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) return false;
        Vuelo vActivo = buscarVueloPorCodigo(codigoVueloActivo);
        if (vActivo == null) return false;

        boolean exito = vActivo.liberarAsiento(fila, columna);
        if (exito) {
            avion.liberarAsiento(fila, columna);
            if (ConexionBD.estaConectado()) {
                asientoDAO.liberarAsiento(codigoVueloActivo, fila, columna);
            }
            return true;
        }
        return false;
    }

    // Operacion Deshacer mediante estructura Pila (LIFO)
    public AccionReserva deshacerUltimaReserva() {
        if (pilaDeshacerReservas == null || pilaDeshacerReservas.estaVacia()) {
            return null;
        }
        AccionReserva accion = (AccionReserva) pilaDeshacerReservas.desapilar();
        if (accion != null) {
            Vuelo v = buscarVueloPorCodigo(accion.obtenerCodigoVuelo());
            if (v != null) {
                v.liberarAsiento(accion.obtenerFila(), accion.obtenerColumna());
                if (accion.obtenerCodigoVuelo().equalsIgnoreCase(codigoVueloActivo)) {
                    avion.liberarAsiento(accion.obtenerFila(), accion.obtenerColumna());
                }
                if (ConexionBD.estaConectado()) {
                    asientoDAO.liberarAsiento(accion.obtenerCodigoVuelo(), accion.obtenerFila(), accion.obtenerColumna());
                }
            }
        }
        return accion;
    }

    public Pila obtenerPilaDeshacer() {
        return pilaDeshacerReservas;
    }

    // Inspección en cadena de la Pila LIFO de Deshacer
    public String obtenerHistorialPilaDeshacer() {
        if (pilaDeshacerReservas == null || pilaDeshacerReservas.estaVacia()) {
            return "La Pila LIFO de reservas se encuentra actualmente vacía (0 transacciones pendientes de revertir).";
        }
        return pilaDeshacerReservas.listar();
    }

    // Operaciones sobre Cola de Pista de Despegue (FIFO y Cola con Prioridad)
    public boolean encolarVueloPista(String codigoVuelo) {
        return encolarVueloPistaConPrioridad(codigoVuelo, 3); // Prioridad Regular por defecto
    }

    public boolean encolarVueloPistaConPrioridad(String codigoVuelo, int prioridad) {
        if (colaPistaDespegue == null || codigoVuelo == null) return false;
        Vuelo v = buscarVueloPorCodigo(codigoVuelo);
        if (v == null) return false;
        colaPistaDespegue.encolarConPrioridad(v, prioridad);
        return true;
    }

    public boolean encolarVueloPistaEmergencia(String codigoVuelo) {
        return encolarVueloPistaConPrioridad(codigoVuelo, 1); // Máxima prioridad (Emergencia)
    }

    public Vuelo despacharSiguienteVueloPista() {
        if (colaPistaDespegue == null || colaPistaDespegue.estaVacia()) {
            return null;
        }
        return (Vuelo) colaPistaDespegue.desencolar();
    }

    public Cola obtenerColaPista() {
        return colaPistaDespegue;
    }

    // Busqueda Binaria sobre el arreglo de vuelos ordenado por codigo (O(log n))
    public Vuelo buscarVueloBinario(String codigo) {
        if (codigo == null || contadorVuelos == 0) return null;
        String buscado = codigo.trim();

        // 1. Si no esta ordenado por codigo, se ordena una sola vez con Quicksort O(n log n)
        if (!estaOrdenadoPorCodigo) {
            if (contadorVuelos > 1) {
                AlgoritmosOrdenamientoAvanzado.quicksortPorCodigo(vuelos, 0, contadorVuelos - 1);
            }
            estaOrdenadoPorCodigo = true;
        }

        // 2. Busqueda binaria en O(log n) real sobre el arreglo ordenado
        int inicio = 0;
        int fin = contadorVuelos - 1;
        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            if (vuelos[medio] == null || vuelos[medio].obtenerCodigo() == null) {
                break;
            }
            int comparacion = vuelos[medio].obtenerCodigo().compareToIgnoreCase(buscado);
            if (comparacion == 0) {
                return vuelos[medio];
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return null;
    }

    public double calcularCostoAsiento(int fila, int columna, double precioBase) {
        return avion.calcularCostoAsiento(fila, columna, precioBase);
    }

    public int[][] obtenerMatrizAsientos() {
        Vuelo v = buscarVueloPorCodigo(codigoVueloActivo);
        if (v != null) return v.obtenerMatrizAsientos();
        return avion.obtenerAsientos();
    }

    public String[][] obtenerMatrizPasajeros() {
        Vuelo v = buscarVueloPorCodigo(codigoVueloActivo);
        if (v != null) return v.obtenerMatrizPasajeros();
        return new String[10][5];
    }

    public String obtenerCodigoVueloActivo() {
        return codigoVueloActivo;
    }

    public Vuelo obtenerVueloActivo() {
        return buscarVueloPorCodigo(codigoVueloActivo);
    }

    public Avion obtenerAvion() {
        return avion;
    }

    public Vuelo[] obtenerVuelos() {
        return vuelos;
    }

    public int obtenerContadorVuelos() {
        return contadorVuelos;
    }

    // Gestion de Listas Dinamicas
    public ListaSimple obtenerListaEquipaje() {
        return listaEquipaje;
    }

    public ListaDoble obtenerListaRutas() {
        return listaRutas;
    }

    public ListaCircular obtenerListaPuertas() {
        return listaPuertas;
    }

    // Delegacion de Equipaje con persistencia
    public boolean agregarEquipaje(Equipaje e) {
        if (listaEquipaje.agregarMaleta(e)) {
            if (ConexionBD.estaConectado()) {
                equipajeDAO.insertar(e);
            }
            return true;
        }
        return false;
    }

    public boolean eliminarEquipaje(String id) {
        if (listaEquipaje.eliminarMaleta(id)) {
            if (ConexionBD.estaConectado()) {
                equipajeDAO.eliminar(id);
            }
            return true;
        }
        return false;
    }

    public boolean actualizarEquipaje(String id, double peso, String tipo) {
        if (listaEquipaje.actualizarMaleta(id, peso, tipo)) {
            if (ConexionBD.estaConectado()) {
                Equipaje e = listaEquipaje.buscarMaleta(id);
                if (e != null) equipajeDAO.actualizar(e);
            }
            return true;
        }
        return false;
    }

    // Delegacion de Rutas con persistencia
    public boolean agregarRuta(Ruta r) {
        if (listaRutas.agregarRuta(r)) {
            if (ConexionBD.estaConectado()) {
                rutaDAO.insertar(r);
            }
            return true;
        }
        return false;
    }

    public boolean eliminarRuta(String id) {
        if (listaRutas.eliminarRuta(id)) {
            if (ConexionBD.estaConectado()) {
                rutaDAO.eliminar(id);
            }
            return true;
        }
        return false;
    }

    public boolean actualizarRuta(String id, Ruta nuevaRuta) {
        if (listaRutas.actualizarRuta(id, nuevaRuta)) {
            if (ConexionBD.estaConectado()) {
                rutaDAO.actualizar(nuevaRuta);
            }
            return true;
        }
        return false;
    }

    // Usuarios y autenticacion
    public Usuario autenticarUsuario(String usuario, String clave) {
        if (ConexionBD.estaConectado()) {
            Usuario u = usuarioDAO.autenticar(usuario, clave);
            if (u != null) {
                this.usuarioActual = u;
                return u;
            }
        }
        if ("admin".equalsIgnoreCase(usuario) && "admin123".equals(clave)) {
            this.usuarioActual = new Usuario("admin", "admin123", "ADMIN", "00000000", "Administrador CORPAC");
            return this.usuarioActual;
        }
        if ("operador".equalsIgnoreCase(usuario) && "operador123".equals(clave)) {
            this.usuarioActual = new Usuario("operador", "operador123", "OPERADOR", "11111111", "Operador de Vuelos CORPAC");
            return this.usuarioActual;
        }
        if ("supervisor".equalsIgnoreCase(usuario) && "sup123".equals(clave)) {
            this.usuarioActual = new Usuario("supervisor", "sup123", "SUPERVISOR", "22222222", "Supervisor de Equipajes CORPAC");
            return this.usuarioActual;
        }
        if ("pasajero".equalsIgnoreCase(usuario) && "123456".equals(clave)) {
            this.usuarioActual = new Usuario("pasajero", "123456", "PASAJERO", "", "");
            return this.usuarioActual;
        }
        return null;
    }

    public Usuario obtenerUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean esSesionAdmin() {
        return usuarioActual != null && usuarioActual.esAdministrador();
    }

    public void iniciarSesionPasajero() {
        this.usuarioActual = new Usuario("pasajero", "", "PASAJERO", "", "");
    }

    public void actualizarDatosPasajeroActual(String dni, String nombre) {
        if (this.usuarioActual != null && this.usuarioActual.esPasajero()) {
            this.usuarioActual.establecerDni(dni);
            this.usuarioActual.establecerNombre(nombre);
        } else {
            this.usuarioActual = new Usuario("pasajero", "", "PASAJERO", dni, nombre);
        }
    }

    public boolean esSesionPasajero() {
        return usuarioActual != null && "PASAJERO".equalsIgnoreCase(usuarioActual.obtenerRol());
    }

    // Gestion de Puertas con persistencia
    public boolean agregarPuerta(PuertaEmbarque p) {
        if (p == null) return false;
        if (listaPuertas.agregarPuerta(p)) {
            if (ConexionBD.estaConectado()) {
                puertaDAO.insertar(p);
            }
            return true;
        }
        return false;
    }

    public boolean actualizarPuerta(PuertaEmbarque p) {
        if (p == null) return false;
        if (ConexionBD.estaConectado()) {
            puertaDAO.actualizar(p);
        }
        return true;
    }

    public boolean asignarVueloAPuerta(String vuelo) {
        if (listaPuertas.asignarVueloACursor(vuelo)) {
            if (ConexionBD.estaConectado()) {
                PuertaEmbarque actual = listaPuertas.obtenerPuertaActual();
                if (actual != null) {
                    puertaDAO.actualizar(actual);
                }
            }
            return true;
        }
        return false;
    }

    public boolean asignarVueloAPuertaEspecifica(String numeroPuerta, String vuelo) {
        if (listaPuertas == null || numeroPuerta == null) return false;
        PuertaEmbarque pe = listaPuertas.buscarPuerta(numeroPuerta);
        if (pe != null) {
            pe.establecerCodigoVuelo(vuelo);
            pe.establecerEstado("Ocupada");
            if (ConexionBD.estaConectado()) {
                puertaDAO.actualizar(pe);
            }
            return true;
        }
        return false;
    }

    public boolean liberarPuerta(String numeroPuerta) {
        if (listaPuertas == null || numeroPuerta == null) return false;
        PuertaEmbarque pe = listaPuertas.buscarPuerta(numeroPuerta);
        if (pe != null) {
            pe.establecerCodigoVuelo("Sin asignar");
            pe.establecerEstado("Disponible");
            if (ConexionBD.estaConectado()) {
                puertaDAO.actualizar(pe);
            }
            return true;
        }
        return false;
    }

    // Vaciar todas las estructuras en memoria y opcionalmente en MySQL
    public boolean vaciarTodo(boolean vaciarMySQL) {
        for (int i = 0; i < contadorVuelos; i++) {
            vuelos[i] = null;
        }
        this.contadorVuelos = 0;
        this.codigoVueloActivo = "";
        this.avion = new Avion();
        this.listaEquipaje = new ListaSimple();
        this.listaRutas = new ListaDoble();
        this.listaPuertas = new ListaCircular();

        for (int i = 0; i < contadorAeropuertos; i++) {
            aeropuertos[i] = null;
        }
        this.contadorAeropuertos = 0;

        for (int i = 0; i < contadorSupervisores; i++) {
            supervisores[i] = null;
        }
        this.contadorSupervisores = 0;

        if (vaciarMySQL && ConexionBD.estaConectado()) {
            asientoDAO.vaciarTabla();
            equipajeDAO.vaciarTabla();
            rutaDAO.vaciarTabla();
            puertaDAO.vaciarTabla();
            vueloDAO.vaciarTabla();
            aeropuertoDAO.vaciarTabla();
            supervisorDAO.vaciarTabla();
        }
        return true;
    }

    // Sincronizacion total con MySQL
    public String sincronizarConBD() {
        if (!ConexionBD.estaConectado()) {
            return "MySQL no disponible. Se continúa operando en memoria RAM.";
        }
        try {
            // Sincronizar vuelos
            for (int i = 0; i < contadorVuelos; i++) {
                vueloDAO.insertar(vuelos[i]);
                asientoDAO.guardarMatrizCompleta(vuelos[i].obtenerCodigo(), vuelos[i].obtenerMatrizAsientos(), vuelos[i].obtenerMatrizPasajeros());
            }

            // Sincronizar equipaje
            com.aeropuerto.estructuras.NodoSimple nodoE = listaEquipaje.obtenerCabeza();
            while (nodoE != null) {
                if (nodoE.dato != null) equipajeDAO.insertar(nodoE.dato);
                nodoE = nodoE.siguiente;
            }

            // Sincronizar rutas
            com.aeropuerto.estructuras.NodoDoble nodoR = listaRutas.obtenerCabeza();
            while (nodoR != null) {
                if (nodoR.dato != null) rutaDAO.insertar(nodoR.dato);
                nodoR = nodoR.siguiente;
            }

            // Sincronizar puertas de embarque
            if (!listaPuertas.estaVacia()) {
                com.aeropuerto.estructuras.NodoCircular nodoP = listaPuertas.obtenerCabeza();
                com.aeropuerto.estructuras.NodoCircular inicioP = nodoP;
                if (nodoP != null) {
                    do {
                        if (nodoP.dato != null) puertaDAO.insertar(nodoP.dato);
                        nodoP = nodoP.siguiente;
                    } while (nodoP != null && nodoP != inicioP);
                }
            }

            // Sincronizar aeropuertos
            for (int i = 0; i < contadorAeropuertos; i++) {
                if (aeropuertos[i] != null) aeropuertoDAO.insertar(aeropuertos[i]);
            }

            // Sincronizar supervisores
            for (int i = 0; i < contadorSupervisores; i++) {
                if (supervisores[i] != null) supervisorDAO.insertar(supervisores[i]);
            }

            return "Sincronización completada con éxito: Vuelos, Asientos, Equipaje, Rutas, Puertas, Aeropuertos y Supervisores respaldados en MySQL.";
        } catch (Exception e) {
            return "Error al sincronizar con MySQL: " + e.getMessage();
        }
    }

    // Cargar datos desde MySQL
    public void cargarDesdeBD() {
        if (!ConexionBD.estaConectado()) {
            return;
        }
        try {
            // Cargar vuelos
            Vuelo[] vuelosBD = vueloDAO.listarTodos();
            if (vuelosBD != null && vuelosBD.length > 0) {
                this.contadorVuelos = 0;
                for (Vuelo v : vuelosBD) {
                    if (v != null) {
                        int[][] matBD = asientoDAO.cargarMatrizAsientos(v.obtenerCodigo());
                        String[][] pasBD = asientoDAO.cargarMatrizPasajeros(v.obtenerCodigo());
                        v.establecerMatrizAsientos(matBD);
                        v.establecerMatrizPasajeros(pasBD);
                        agregarVueloLocal(v);
                    }
                }
            }

            // Cargar equipaje
            List<Equipaje> maletasBD = equipajeDAO.listarTodos();
            if (maletasBD != null && !maletasBD.isEmpty()) {
                this.listaEquipaje = new ListaSimple();
                for (Equipaje eq : maletasBD) {
                    this.listaEquipaje.agregarMaleta(eq);
                }
            }

            // Cargar rutas
            List<Ruta> rutasBD = rutaDAO.listarTodas();
            if (rutasBD != null && !rutasBD.isEmpty()) {
                this.listaRutas = new ListaDoble();
                for (Ruta rt : rutasBD) {
                    this.listaRutas.agregarRuta(rt);
                }
            }

            // Cargar puertas de embarque
            List<PuertaEmbarque> puertasBD = puertaDAO.listarTodas();
            if (puertasBD != null && !puertasBD.isEmpty()) {
                this.listaPuertas = new ListaCircular();
                for (PuertaEmbarque p : puertasBD) {
                    this.listaPuertas.agregarPuerta(p);
                }
            }

            // Cargar aeropuertos
            List<Aeropuerto> aeropuertosBD = aeropuertoDAO.listarTodos();
            if (aeropuertosBD != null && !aeropuertosBD.isEmpty()) {
                this.aeropuertos = new Aeropuerto[Math.max(20, aeropuertosBD.size() * 2)];
                this.contadorAeropuertos = 0;
                for (Aeropuerto a : aeropuertosBD) {
                    this.aeropuertos[contadorAeropuertos++] = a;
                }
            }

            // Cargar supervisores
            List<Supervisor> supervisoresBD = supervisorDAO.listarTodos();
            if (supervisoresBD != null && !supervisoresBD.isEmpty()) {
                this.supervisores = new Supervisor[Math.max(20, supervisoresBD.size() * 2)];
                this.contadorSupervisores = 0;
                for (Supervisor s : supervisoresBD) {
                    this.supervisores[contadorSupervisores++] = s;
                }
            }

            // Sincronizar vuelo activo
            if (contadorVuelos > 0 && vuelos[0] != null) {
                seleccionarVueloActivo(vuelos[0].obtenerCodigo());
            } else {
                this.codigoVueloActivo = "";
                this.avion = new Avion();
            }
        } catch (Exception e) {
            System.err.println("[ControladorAeropuerto] Error cargando desde BD: " + e.getMessage());
        }
    }

    // Conteo de registros en tablas MySQL en tiempo real
    public int[] obtenerConteoRegistrosBD() {
        int[] conteos = new int[8]; // [vuelos, asientos, equipaje, rutas, puertas, usuarios, aeropuertos, supervisores]
        if (!ConexionBD.estaConectado()) return conteos;
        java.sql.Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) return conteos;

        String[] consultas = {
            "SELECT COUNT(*) FROM vuelos",
            "SELECT COUNT(*) FROM asientos WHERE estado = 1",
            "SELECT COUNT(*) FROM equipaje",
            "SELECT COUNT(*) FROM rutas",
            "SELECT COUNT(*) FROM puertas_embarque",
            "SELECT COUNT(*) FROM usuarios",
            "SELECT COUNT(*) FROM aeropuertos",
            "SELECT COUNT(*) FROM supervisores"
        };
        try {
            java.sql.Statement stmt = conn.createStatement();
            for (int i = 0; i < consultas.length; i++) {
                try {
                    java.sql.ResultSet rs = stmt.executeQuery(consultas[i]);
                    if (rs.next()) conteos[i] = rs.getInt(1);
                    rs.close();
                } catch (Exception ex) {
                    conteos[i] = 0;
                }
            }
            stmt.close();
        } catch (Exception e) {
        } finally {
            try { conn.close(); } catch (Exception ex) {}
        }
        return conteos;
    }

    // Operaciones sobre Aeropuertos y Pistas (Arreglo Dinamico)
    public boolean agregarAeropuerto(Aeropuerto a) {
        if (a == null) return false;
        if (buscarAeropuertoPorIata(a.obtenerCodigoIata()) != null) return false;
        if (contadorAeropuertos >= aeropuertos.length) {
            Aeropuerto[] nuevo = new Aeropuerto[aeropuertos.length * 2];
            for (int i = 0; i < contadorAeropuertos; i++) nuevo[i] = aeropuertos[i];
            aeropuertos = nuevo;
        }
        aeropuertos[contadorAeropuertos++] = a;
        if (ConexionBD.estaConectado()) {
            aeropuertoDAO.insertar(a);
        }
        return true;
    }

    public boolean actualizarAeropuerto(Aeropuerto a) {
        if (a == null) return false;
        for (int i = 0; i < contadorAeropuertos; i++) {
            if (aeropuertos[i] != null && aeropuertos[i].obtenerCodigoIata().equalsIgnoreCase(a.obtenerCodigoIata())) {
                aeropuertos[i] = a;
                if (ConexionBD.estaConectado()) {
                    aeropuertoDAO.actualizar(a);
                }
                return true;
            }
        }
        return false;
    }

    public boolean eliminarAeropuerto(String codigoIata) {
        if (codigoIata == null) return false;
        int idx = -1;
        for (int i = 0; i < contadorAeropuertos; i++) {
            if (aeropuertos[i] != null && aeropuertos[i].obtenerCodigoIata().equalsIgnoreCase(codigoIata)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return false;
        for (int i = idx; i < contadorAeropuertos - 1; i++) {
            aeropuertos[i] = aeropuertos[i + 1];
        }
        contadorAeropuertos--;
        aeropuertos[contadorAeropuertos] = null;
        if (ConexionBD.estaConectado()) {
            aeropuertoDAO.eliminar(codigoIata);
        }
        return true;
    }

    public Aeropuerto buscarAeropuertoPorIata(String codigoIata) {
        if (codigoIata == null) return null;
        for (int i = 0; i < contadorAeropuertos; i++) {
            if (aeropuertos[i] != null && aeropuertos[i].obtenerCodigoIata() != null
                    && aeropuertos[i].obtenerCodigoIata().equalsIgnoreCase(codigoIata)) {
                return aeropuertos[i];
            }
        }
        return null;
    }

    public void ordenarAeropuertosPorCapacidad(boolean ascendente) {
        for (int i = 1; i < contadorAeropuertos; i++) {
            Aeropuerto actual = aeropuertos[i];
            if (actual == null) continue;
            int j = i - 1;
            if (ascendente) {
                while (j >= 0 && aeropuertos[j] != null && aeropuertos[j].obtenerCapacidadStands() > actual.obtenerCapacidadStands()) {
                    aeropuertos[j + 1] = aeropuertos[j];
                    j--;
                }
            } else {
                while (j >= 0 && aeropuertos[j] != null && aeropuertos[j].obtenerCapacidadStands() < actual.obtenerCapacidadStands()) {
                    aeropuertos[j + 1] = aeropuertos[j];
                    j--;
                }
            }
            aeropuertos[j + 1] = actual;
        }
    }

    public boolean liberarPistaAeropuerto(String iata) {
        Aeropuerto a = buscarAeropuertoPorIata(iata);
        if (a != null && a.liberarPista()) {
            if (ConexionBD.estaConectado()) aeropuertoDAO.actualizar(a);
            return true;
        }
        return false;
    }

    public boolean ocuparPistaAeropuerto(String iata) {
        Aeropuerto a = buscarAeropuertoPorIata(iata);
        if (a != null && a.ocuparPista()) {
            if (ConexionBD.estaConectado()) aeropuertoDAO.actualizar(a);
            return true;
        }
        return false;
    }

    public Aeropuerto[] obtenerAeropuertos() {
        return aeropuertos;
    }

    public int obtenerContadorAeropuertos() {
        return contadorAeropuertos;
    }

    public void cargarAeropuertosEstandar() {
        this.aeropuertos = new Aeropuerto[20];
        this.contadorAeropuertos = 0;
        agregarAeropuerto(new Aeropuerto("LIM", "Aeropuerto Internacional Jorge Chávez", "Lima", 2, 2, 30, 0, "LIBRE", "21°C Despejado", "Ing. Carlos Mendoza (SUP-01)"));
        agregarAeropuerto(new Aeropuerto("CUZ", "Aeropuerto Alejandro Velasco Astete", "Cusco", 1, 1, 10, 0, "LIBRE", "16°C Parcial", "Lic. Sofia Delgado (SUP-02)"));
        agregarAeropuerto(new Aeropuerto("AQP", "Aeropuerto Rodríguez Ballón", "Arequipa", 1, 1, 8, 0, "LIBRE", "20°C Despejado", "Ing. Marco Ramos (SUP-03)"));
        agregarAeropuerto(new Aeropuerto("PIU", "Aeropuerto Cap. FAP Guillermo Concha", "Piura", 1, 1, 6, 0, "LIBRE", "28°C Soleado", "Tec. Elena Vega (SUP-04)"));
        agregarAeropuerto(new Aeropuerto("IQT", "Aeropuerto Coronel FAP Francisco Secada", "Iquitos", 1, 1, 6, 0, "LIBRE", "30°C Humedad Alta", "Ing. Carlos Mendoza (SUP-01)"));
        agregarAeropuerto(new Aeropuerto("TRU", "Aeropuerto Cap. FAP Carlos Martínez", "Trujillo", 1, 1, 5, 0, "LIBRE", "23°C Viento Moderado", "Lic. Sofia Delgado (SUP-02)"));
        agregarAeropuerto(new Aeropuerto("TPP", "Aeropuerto Cadete FAP Guillermo del Castillo", "Tarapoto", 1, 1, 4, 0, "LIBRE", "27°C Llovizna", "Ing. Marco Ramos (SUP-03)"));
        agregarAeropuerto(new Aeropuerto("CIX", "Aeropuerto Cap. FAP José Quiñones", "Chiclayo", 1, 1, 5, 0, "LIBRE", "24°C Despejado", "Tec. Elena Vega (SUP-04)"));
    }

    // Operaciones sobre Supervisores (Arreglo Dinamico)
    public boolean agregarSupervisor(Supervisor s) {
        if (s == null) return false;
        if (buscarSupervisorPorId(s.obtenerIdSupervisor()) != null) return false;
        if (contadorSupervisores >= supervisores.length) {
            Supervisor[] nuevo = new Supervisor[supervisores.length * 2];
            for (int i = 0; i < contadorSupervisores; i++) nuevo[i] = supervisores[i];
            supervisores = nuevo;
        }
        supervisores[contadorSupervisores++] = s;
        if (ConexionBD.estaConectado()) {
            supervisorDAO.insertar(s);
        }
        return true;
    }

    public boolean actualizarSupervisor(Supervisor s) {
        if (s == null) return false;
        for (int i = 0; i < contadorSupervisores; i++) {
            if (supervisores[i] != null && supervisores[i].obtenerIdSupervisor().equalsIgnoreCase(s.obtenerIdSupervisor())) {
                supervisores[i] = s;
                if (ConexionBD.estaConectado()) {
                    supervisorDAO.actualizar(s);
                }
                return true;
            }
        }
        return false;
    }

    public boolean eliminarSupervisor(String id) {
        if (id == null) return false;
        int idx = -1;
        for (int i = 0; i < contadorSupervisores; i++) {
            if (supervisores[i] != null && supervisores[i].obtenerIdSupervisor().equalsIgnoreCase(id)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return false;
        for (int i = idx; i < contadorSupervisores - 1; i++) {
            supervisores[i] = supervisores[i + 1];
        }
        contadorSupervisores--;
        supervisores[contadorSupervisores] = null;
        if (ConexionBD.estaConectado()) {
            supervisorDAO.eliminar(id);
        }
        return true;
    }

    public Supervisor buscarSupervisorPorId(String id) {
        if (id == null) return null;
        for (int i = 0; i < contadorSupervisores; i++) {
            if (supervisores[i] != null && supervisores[i].obtenerIdSupervisor() != null
                    && supervisores[i].obtenerIdSupervisor().equalsIgnoreCase(id)) {
                return supervisores[i];
            }
        }
        return null;
    }

    public Supervisor[] obtenerSupervisores() {
        return supervisores;
    }

    public int obtenerContadorSupervisores() {
        return contadorSupervisores;
    }

    public void cargarSupervisoresEstandar() {
        this.supervisores = new Supervisor[20];
        this.contadorSupervisores = 0;
        agregarSupervisor(new Supervisor("SUP-01", "Ing. Carlos Mendoza", "42338899", "Mañana", "Supervisor de Torre Principal", "LIM", "En Turno"));
        agregarSupervisor(new Supervisor("SUP-02", "Lic. Sofia Delgado", "45991122", "Tarde", "Supervisora de Operaciones Plataforma", "CUZ", "En Turno"));
        agregarSupervisor(new Supervisor("SUP-03", "Ing. Marco Ramos", "40117788", "Noche", "Jefe de Despacho y Tráfico Aéreo", "AQP", "En Turno"));
        agregarSupervisor(new Supervisor("SUP-04", "Tec. Elena Vega", "48223344", "Mañana", "Controladora Radar de Aproximación", "PIU", "En Turno"));
    }

    public boolean asignarSupervisorAAeropuerto(String codigoIata, String idSupervisor) {
        Aeropuerto a = buscarAeropuertoPorIata(codigoIata);
        Supervisor s = buscarSupervisorPorId(idSupervisor);
        if (a != null && s != null) {
            a.establecerSupervisorACargo(s.obtenerNombre() + " (" + s.obtenerIdSupervisor() + ")");
            s.establecerAeropuertoAsignado(a.obtenerCodigoIata());
            if (ConexionBD.estaConectado()) {
                aeropuertoDAO.actualizar(a);
                supervisorDAO.actualizar(s);
            }
            return true;
        }
        return false;
    }

    // Capa de servicio extensible (ABB, Grafos, Diagnostico)
    public ServicioAeropuerto obtenerServicioAeropuerto() {
        return ServicioAeropuerto.obtenerInstancia();
    }

    public void sincronizarIndiceArbol() {
        ServicioAeropuerto.obtenerInstancia().indexarVuelosEnArbol(this.vuelos, this.contadorVuelos);
    }

    public Vuelo buscarVueloPorArbol(String codigo) {
        if (codigo == null) return null;
        Vuelo v = ServicioAeropuerto.obtenerInstancia().buscarVueloEnAVL(codigo);
        if (v != null) return v;
        return ServicioAeropuerto.obtenerInstancia().buscarVueloEnArbol(codigo);
    }

    public List<Vertice> calcularRutaOptima(String codigoOrigen, String codigoDestino) {
        return ServicioAeropuerto.obtenerInstancia().calcularRutaOptima(codigoOrigen, codigoDestino);
    }

    public String ejecutarDiagnosticoTADs() {
        return ServicioAeropuerto.obtenerInstancia().ejecutarDiagnosticoIntegralTADs(this);
    }

    // Algoritmos de ordenamiento avanzado
    public void ordenarVuelosQuicksort(boolean ascendente) {
        if (contadorVuelos > 1) {
            AlgoritmosOrdenamientoAvanzado.quicksortPorPrecio(vuelos, 0, contadorVuelos - 1, ascendente);
            estaOrdenadoPorCodigo = false;
        }
    }

    public void ordenarVuelosMergesort(boolean ascendente) {
        if (contadorVuelos > 1) {
            AlgoritmosOrdenamientoAvanzado.mergesortPorDestino(vuelos, 0, contadorVuelos - 1, ascendente);
            estaOrdenadoPorCodigo = false;
        }
    }

    public void ordenarVuelosHeapsort(boolean ascendente) {
        if (contadorVuelos > 1) {
            Vuelo[] activos = new Vuelo[contadorVuelos];
            System.arraycopy(vuelos, 0, activos, 0, contadorVuelos);
            AlgoritmosOrdenamientoAvanzado.heapsortPorPrecio(activos, ascendente);
            System.arraycopy(activos, 0, vuelos, 0, contadorVuelos);
            estaOrdenadoPorCodigo = false;
        }
    }

    public String ejecutarBenchmarkOrdenamientos() {
        if (contadorVuelos == 0) return "No hay vuelos registrados para evaluar.";
        Vuelo[] activos = new Vuelo[contadorVuelos];
        System.arraycopy(vuelos, 0, activos, 0, contadorVuelos);
        return AlgoritmosOrdenamientoAvanzado.ejecutarBenchmark(activos);
    }

    // Estructuras avanzadas: AVL, Hash, Heap, MST y Floyd
    public ArbolAVL<String, Vuelo> obtenerArbolAVL() {
        return ServicioAeropuerto.obtenerInstancia().obtenerArbolAVL();
    }

    public TablaHash<String, String> obtenerTablaHashPasajeros() {
        return ServicioAeropuerto.obtenerInstancia().obtenerTablaHashPasajeros();
    }

    public MonticuloBinario<String> obtenerHeapATC() {
        return ServicioAeropuerto.obtenerInstancia().obtenerHeapDespachoATC();
    }

    public ResultadoMST calcularMSTPrim(String inicio) {
        return ServicioAeropuerto.obtenerInstancia().calcularMSTPrim(inicio);
    }

    public ResultadoMST calcularMSTKruskal() {
        return ServicioAeropuerto.obtenerInstancia().calcularMSTKruskal();
    }

    public ResultadoFloydWarshall calcularFloydWarshall() {
        return ServicioAeropuerto.obtenerInstancia().calcularFloydWarshall();
    }
}
