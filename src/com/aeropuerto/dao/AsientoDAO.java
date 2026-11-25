package com.aeropuerto.dao;

import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// DAO para matriz de asientos (persistencia por vuelo en MySQL) con try-with-resources integral
public class AsientoDAO {

    private static final String VUELO_DEFECTO = "CP-2023";

    public boolean ocuparAsiento(int fila, int columna, String dniPasajero) {
        return ocuparAsiento(VUELO_DEFECTO, fila, columna, dniPasajero);
    }

    public boolean ocuparAsiento(String codigoVuelo, int fila, int columna, String dniPasajero) {
        String cod = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : VUELO_DEFECTO;
        String dni = (dniPasajero != null && !dniPasajero.trim().isEmpty()) ? dniPasajero.trim() : "Reservado";

        String sql = "INSERT INTO asientos (codigo_vuelo, fila, columna, estado, dni_pasajero) VALUES (?, ?, ?, 1, ?) " +
                     "ON DUPLICATE KEY UPDATE estado = 1, dni_pasajero = VALUES(dni_pasajero)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, cod);
                ps.setInt(2, fila);
                ps.setInt(3, columna);
                ps.setString(4, dni);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                // Reintento de compatibilidad sin columna codigo_vuelo
                String sqlCompat = "INSERT INTO asientos (fila, columna, estado, dni_pasajero) VALUES (?, ?, 1, ?) " +
                                  "ON DUPLICATE KEY UPDATE estado = 1, dni_pasajero = VALUES(dni_pasajero)";
                try (PreparedStatement psCompat = conexion.prepareStatement(sqlCompat)) {
                    psCompat.setInt(1, fila);
                    psCompat.setInt(2, columna);
                    psCompat.setString(3, dni);
                    return psCompat.executeUpdate() > 0;
                } catch (SQLException ex) {
                    System.err.println("[AsientoDAO] Error al ocupar asiento: " + ex.getMessage());
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAO] Error de conexion al ocupar asiento: " + e.getMessage());
            return false;
        }
    }

    public boolean liberarAsiento(int fila, int columna) {
        return liberarAsiento(VUELO_DEFECTO, fila, columna);
    }

    public boolean liberarAsiento(String codigoVuelo, int fila, int columna) {
        String cod = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : VUELO_DEFECTO;
        String sql = "UPDATE asientos SET estado = 0, dni_pasajero = NULL WHERE codigo_vuelo = ? AND fila = ? AND columna = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, cod);
                ps.setInt(2, fila);
                ps.setInt(3, columna);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                String sqlCompat = "UPDATE asientos SET estado = 0, dni_pasajero = NULL WHERE fila = ? AND columna = ?";
                try (PreparedStatement psCompat = conexion.prepareStatement(sqlCompat)) {
                    psCompat.setInt(1, fila);
                    psCompat.setInt(2, columna);
                    return psCompat.executeUpdate() > 0;
                } catch (SQLException ex) {
                    System.err.println("[AsientoDAO] Error al liberar asiento: " + ex.getMessage());
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAO] Error de conexion al liberar asiento: " + e.getMessage());
            return false;
        }
    }

    public int[][] cargarMatrizAsientos() {
        return cargarMatrizAsientos(VUELO_DEFECTO);
    }

    public int[][] cargarMatrizAsientos(String codigoVuelo) {
        int[][] matriz = new int[10][5];
        String cod = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : VUELO_DEFECTO;
        String sql = "SELECT fila, columna, estado FROM asientos WHERE codigo_vuelo = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return matriz;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, cod);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int fila = rs.getInt("fila");
                        int columna = rs.getInt("columna");
                        int estado = rs.getInt("estado");
                        if (fila >= 0 && fila < 10 && columna >= 0 && columna < 5) {
                            matriz[fila][columna] = estado;
                        }
                    }
                    return matriz;
                }
            } catch (SQLException e) {
                String sqlCompat = "SELECT fila, columna, estado FROM asientos";
                try (PreparedStatement psCompat = conexion.prepareStatement(sqlCompat);
                     ResultSet rs = psCompat.executeQuery()) {
                    while (rs.next()) {
                        int fila = rs.getInt("fila");
                        int columna = rs.getInt("columna");
                        int estado = rs.getInt("estado");
                        if (fila >= 0 && fila < 10 && columna >= 0 && columna < 5) {
                            matriz[fila][columna] = estado;
                        }
                    }
                    return matriz;
                } catch (SQLException ex) {
                    System.err.println("[AsientoDAO] Error al cargar matriz de asientos: " + ex.getMessage());
                    return matriz;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAO] Error de conexion al cargar matriz: " + e.getMessage());
            return matriz;
        }
    }

    public String[][] cargarMatrizPasajeros() {
        return cargarMatrizPasajeros(VUELO_DEFECTO);
    }

    public String[][] cargarMatrizPasajeros(String codigoVuelo) {
        String[][] matrizPasajeros = new String[10][5];
        String cod = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : VUELO_DEFECTO;
        String sql = "SELECT fila, columna, dni_pasajero FROM asientos WHERE codigo_vuelo = ? AND estado = 1";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return matrizPasajeros;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, cod);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int fila = rs.getInt("fila");
                        int columna = rs.getInt("columna");
                        String dni = rs.getString("dni_pasajero");
                        if (fila >= 0 && fila < 10 && columna >= 0 && columna < 5) {
                            matrizPasajeros[fila][columna] = dni;
                        }
                    }
                    return matrizPasajeros;
                }
            } catch (SQLException e) {
                String sqlCompat = "SELECT fila, columna, dni_pasajero FROM asientos WHERE estado = 1";
                try (PreparedStatement psCompat = conexion.prepareStatement(sqlCompat);
                     ResultSet rs = psCompat.executeQuery()) {
                    while (rs.next()) {
                        int fila = rs.getInt("fila");
                        int columna = rs.getInt("columna");
                        String dni = rs.getString("dni_pasajero");
                        if (fila >= 0 && fila < 10 && columna >= 0 && columna < 5) {
                            matrizPasajeros[fila][columna] = dni;
                        }
                    }
                    return matrizPasajeros;
                } catch (SQLException ex) {
                    System.err.println("[AsientoDAO] Error al cargar matriz de pasajeros: " + ex.getMessage());
                    return matrizPasajeros;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAO] Error de conexion al cargar pasajeros: " + e.getMessage());
            return matrizPasajeros;
        }
    }

    public boolean guardarMatrizCompleta(int[][] matriz, String[][] matrizPasajeros) {
        return guardarMatrizCompleta(VUELO_DEFECTO, matriz, matrizPasajeros);
    }

    public boolean guardarMatrizCompleta(String codigoVuelo, int[][] matriz, String[][] matrizPasajeros) {
        if (matriz == null) return false;
        String cod = (codigoVuelo != null && !codigoVuelo.trim().isEmpty()) ? codigoVuelo.trim() : VUELO_DEFECTO;
        String sql = "INSERT INTO asientos (codigo_vuelo, fila, columna, estado, dni_pasajero) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE estado = VALUES(estado), dni_pasajero = VALUES(dni_pasajero)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (int fila = 0; fila < 10; fila++) {
                    for (int columna = 0; columna < 5; columna++) {
                        int estado = matriz[fila][columna];
                        String dni = (matrizPasajeros != null) ? matrizPasajeros[fila][columna] : null;
                        ps.setString(1, cod);
                        ps.setInt(2, fila);
                        ps.setInt(3, columna);
                        ps.setInt(4, estado);
                        ps.setString(5, dni);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAO] Error al guardar matriz: " + e.getMessage());
            return false;
        }
    }

    public boolean vaciarTabla() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DELETE FROM asientos");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
