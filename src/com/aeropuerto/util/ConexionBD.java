package com.aeropuerto.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Conexión a MySQL con JDBC ultra-rápida, no bloqueante y optimizada para cero cuello de botella
public class ConexionBD {

    // Parámetros de configuración
    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "aeropuerto_db";
    private static final String USUARIO = "root";

    // Contraseñas de prueba
    private static final String[] CLAVES = {"1234", "admin123", "", "root"};
    private static final String PARAMETROS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&connectTimeout=500&socketTimeout=1000";

    // Estado reactivo y caché en memoria atómica (0 ms en EDT)
    private static volatile boolean conectado = false;
    private static volatile long tiempoUltimoChequeo = 0L;
    private static final long TTL_CACHE_MS = 6000L;

    // Estado interno
    private static boolean driverCargado = false;
    private static String claveExitosa = "1234";
    private static String mensajeUltimoEstado = "Modo Local en Memoria RAM.";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverCargado = true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                driverCargado = true;
            } catch (ClassNotFoundException ex) {
                driverCargado = false;
                mensajeUltimoEstado = "Driver JDBC de MySQL no encontrado en el classpath.";
                System.err.println("[ConexionBD] " + mensajeUltimoEstado);
            }
        }

        // Hilo daemon en segundo plano: monitoriza conectividad cada 6s sin congelar la interfaz
        iniciarMonitorSegundoPlano();
    }

    private static void iniciarMonitorSegundoPlano() {
        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    actualizarEstadoConexionInterno();
                    Thread.sleep(TTL_CACHE_MS);
                } catch (InterruptedException ie) {
                    break;
                } catch (Exception ignored) {}
            }
        }, "CORPAC-MySQL-Heartbeat");
        monitor.setDaemon(true);
        monitor.setPriority(Thread.MIN_PRIORITY);
        monitor.start();
    }

    // Prueba rápida TCP para verificar si el puerto MySQL está respondiendo (< 2ms)
    private static boolean esPuertoAccesible(String host, int puerto, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), timeoutMs);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void invalidarCache() {
        tiempoUltimoChequeo = 0L;
        // Lanzar verificación en segundo plano inmediata
        Thread t = new Thread(ConexionBD::actualizarEstadoConexionInterno, "CORPAC-MySQL-Check");
        t.setDaemon(true);
        t.start();
    }

    private static synchronized void actualizarEstadoConexionInterno() {
        if (!driverCargado) {
            conectado = false;
            return;
        }

        int puertoInt = 3306;
        try { puertoInt = Integer.parseInt(PUERTO); } catch (Exception ignored) {}
        if (!esPuertoAccesible(HOST, puertoInt, 50)) {
            conectado = false;
            mensajeUltimoEstado = "Modo Local en Memoria RAM (MySQL no detectado en " + HOST + ":" + PUERTO + ").";
            tiempoUltimoChequeo = System.currentTimeMillis();
            return;
        }

        String url = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS + PARAMETROS;
        try (Connection con = DriverManager.getConnection(url, USUARIO, claveExitosa)) {
            conectado = true;
            mensajeUltimoEstado = "Conectado a MySQL (" + HOST + ":" + PUERTO + "/" + BASE_DATOS + ")";
            tiempoUltimoChequeo = System.currentTimeMillis();
            return;
        } catch (SQLException e) {
            // Si falló la clave conocida, probar las alternativas rápidamente
            for (int i = 0; i < CLAVES.length; i++) {
                String c = CLAVES[i];
                if (!c.equals(claveExitosa)) {
                    try (Connection con2 = DriverManager.getConnection(url, USUARIO, c)) {
                        claveExitosa = c;
                        conectado = true;
                        mensajeUltimoEstado = "Conectado a MySQL con clave alternativa.";
                        tiempoUltimoChequeo = System.currentTimeMillis();
                        return;
                    } catch (SQLException ignored) {}
                }
            }
        }

        conectado = false;
        mensajeUltimoEstado = "No se pudo conectar a MySQL en " + HOST + ":" + PUERTO + ".";
        tiempoUltimoChequeo = System.currentTimeMillis();
    }

    // Obtener conexión a MySQL optimizada para velocidad
    public static Connection obtenerConexion() {
        if (!driverCargado) {
            return null;
        }

        // Si sabemos que está desconectado, retornar null instantáneamente sin tocar la red
        if (!conectado) {
            return null;
        }

        String url = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS + PARAMETROS;
        try {
            Connection conexion = DriverManager.getConnection(url, USUARIO, claveExitosa);
            mensajeUltimoEstado = "Conectado a MySQL (" + HOST + ":" + PUERTO + "/" + BASE_DATOS + ")";
            return conexion;
        } catch (SQLException e) {
            conectado = false;
            mensajeUltimoEstado = "Conexión a MySQL interrumpida.";
            return null;
        }
    }

    // Comprobar conexión activa: CERO bloqueo, retorna en 0 ms
    public static boolean estaConectado() {
        return conectado;
    }

    // Método explícito para probar conexión manual
    public static boolean probarConexionManual() {
        actualizarEstadoConexionInterno();
        return conectado;
    }

    // Inicializar tablas en MySQL
    public static void inicializarTablas() {
        Connection conexion = obtenerConexion();
        if (conexion == null) {
            System.out.println("[ConexionBD] Modo local en memoria activo (MySQL no conectado).");
            return;
        }

        try {
            Statement stmt = conexion.createStatement();

            // 1. Tabla Usuarios
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  usuario VARCHAR(50) PRIMARY KEY," +
                "  clave VARCHAR(100) NOT NULL," +
                "  rol VARCHAR(20) NOT NULL," +
                "  dni VARCHAR(20) NOT NULL," +
                "  nombre VARCHAR(100) NOT NULL" +
                ")"
            );

            // 2. Tabla Vuelos (Arreglos)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS vuelos (" +
                "  codigo VARCHAR(20) PRIMARY KEY," +
                "  origen VARCHAR(50) NOT NULL," +
                "  destino VARCHAR(50) NOT NULL," +
                "  fecha VARCHAR(20) NOT NULL," +
                "  hora_salida VARCHAR(10) NOT NULL," +
                "  hora_llegada VARCHAR(10) NOT NULL," +
                "  duracion INT NOT NULL," +
                "  precio_base DOUBLE NOT NULL," +
                "  clase_disponible VARCHAR(20) NOT NULL" +
                ")"
            );

            // 3. Tabla Asientos (Matriz 10x5 con identificador de vuelo y FK)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS asientos (" +
                "  codigo_vuelo VARCHAR(20) NOT NULL DEFAULT 'CP-2023'," +
                "  fila INT NOT NULL," +
                "  columna INT NOT NULL," +
                "  estado INT NOT NULL DEFAULT 0," +
                "  dni_pasajero VARCHAR(20)," +
                "  PRIMARY KEY (codigo_vuelo, fila, columna)," +
                "  CONSTRAINT fk_asientos_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE" +
                ")"
            );

            // 4. Tabla Equipaje (Lista Simple)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS equipaje (" +
                "  id VARCHAR(20) PRIMARY KEY," +
                "  dni_pasajero VARCHAR(20) NOT NULL," +
                "  codigo_vuelo VARCHAR(20) NOT NULL DEFAULT 'CP-2023'," +
                "  peso DOUBLE NOT NULL," +
                "  tipo VARCHAR(20) NOT NULL," +
                "  CONSTRAINT fk_equipaje_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE" +
                ")"
            );

            // 5. Tabla Rutas (Lista Doble)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS rutas (" +
                "  id VARCHAR(20) PRIMARY KEY," +
                "  origen VARCHAR(50) NOT NULL," +
                "  destino VARCHAR(50) NOT NULL," +
                "  distancia DOUBLE NOT NULL" +
                ")"
            );

            // 6. Tabla Puertas de Embarque (Lista Circular)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS puertas_embarque (" +
                "  numero_puerta VARCHAR(20) PRIMARY KEY," +
                "  terminal VARCHAR(20) NOT NULL," +
                "  estado VARCHAR(20) NOT NULL," +
                "  codigo_vuelo VARCHAR(20)" +
                ")"
            );

            // 7. Tabla Pasajes (Reservas y Boletas)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS pasajes (" +
                "  id_pasaje VARCHAR(30) PRIMARY KEY," +
                "  usuario VARCHAR(50) NOT NULL," +
                "  codigo_vuelo VARCHAR(20) NOT NULL," +
                "  dni_pasajero VARCHAR(20) NOT NULL," +
                "  nombre_pasajero VARCHAR(100) NOT NULL," +
                "  fila INT NOT NULL," +
                "  columna INT NOT NULL," +
                "  tarifa_total DOUBLE NOT NULL," +
                "  estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO'," +
                "  CONSTRAINT fk_pasajes_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE," +
                "  CONSTRAINT fk_pasajes_usuario FOREIGN KEY (usuario) REFERENCES usuarios(usuario) ON DELETE CASCADE" +
                ")"
            );

            // 8. Tabla Aeropuertos
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS aeropuertos (" +
                "  codigo_iata VARCHAR(10) PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  ciudad VARCHAR(50) NOT NULL," +
                "  total_pistas INT NOT NULL," +
                "  pistas_libres INT NOT NULL," +
                "  capacidad_stands INT NOT NULL," +
                "  aeronaves_actuales INT NOT NULL," +
                "  estado VARCHAR(20) NOT NULL," +
                "  clima VARCHAR(50)," +
                "  supervisor_cargo VARCHAR(100)" +
                ")"
            );

            // 9. Tabla Supervisores
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS supervisores (" +
                "  id VARCHAR(20) PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  dni VARCHAR(20) NOT NULL," +
                "  turno VARCHAR(20) NOT NULL," +
                "  rol VARCHAR(50) NOT NULL," +
                "  aeropuerto_asignado VARCHAR(10)," +
                "  estado VARCHAR(20) NOT NULL" +
                ")"
            );

            // Insertar credenciales por defecto si no existen
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) VALUES ('admin', 'admin123', 'ADMIN', '00000000', 'Administrador Principal CORPAC')");
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) VALUES ('operador', 'operador123', 'OPERADOR', '11111111', 'Operador de Vuelos CORPAC')");
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) VALUES ('supervisor', 'sup123', 'SUPERVISOR', '22222222', 'Supervisor de Operaciones CORPAC')");
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) VALUES ('pasajero', '123456', 'PASAJERO', '72345678', 'Pasajero Comercial')");

            stmt.close();
            System.out.println("[ConexionBD] Base de datos y tablas inicializadas con exito en MySQL.");
        } catch (SQLException e) {
            System.err.println("[ConexionBD] Error al inicializar tablas: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {}
        }
    }

    // Mensaje de estado de conexion
    public static String obtenerMensajeEstado() {
        return mensajeUltimoEstado;
    }
}
