package com.aeropuerto.dao;

import com.aeropuerto.modelo.Vuelo;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================================
 * PATRÓN DATA ACCESS OBJECT (DAO): PERSISTENCIA DE VUELOS (CRUD MYSQL)
 * ============================================================================
 * Seguridad y Robustez Clean Code:
 * - Parámetros bind (?) en PreparedStatement para 100% protección contra SQL Injection (OWASP).
 * - Manejo automático de recursos mediante try-with-resources (Cierre garantizado de Connection,
 *   PreparedStatement y ResultSet libre de connection leaks).
 * - Tolerancia a fallos: En ausencia de MySQL, retorna false/vacío y delega al estado en RAM.
 * ============================================================================
 */
public class VueloDAO {

    // Insertar vuelo
    public boolean insertar(Vuelo vuelo) {
        if (vuelo == null) {
            return false;
        }

        String sql = "INSERT INTO vuelos (codigo, origen, destino, fecha, hora_salida, hora_llegada, duracion, precio_base, clase_disponible) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE origen=VALUES(origen), destino=VALUES(destino), fecha=VALUES(fecha), " +
                     "hora_salida=VALUES(hora_salida), hora_llegada=VALUES(hora_llegada), duracion=VALUES(duracion), " +
                     "precio_base=VALUES(precio_base), clase_disponible=VALUES(clase_disponible)";

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, vuelo.obtenerCodigo());
                ps.setString(2, vuelo.obtenerOrigen());
                ps.setString(3, vuelo.obtenerDestino());
                ps.setString(4, vuelo.obtenerFecha());
                ps.setString(5, vuelo.obtenerHoraSalida());
                ps.setString(6, vuelo.obtenerHoraLlegada());
                ps.setInt(7, vuelo.obtenerDuracion());
                ps.setDouble(8, vuelo.obtenerPrecioBase());
                ps.setString(9, vuelo.obtenerClaseDisponible());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[VueloDAO] Error al insertar vuelo: " + e.getMessage());
            return false;
        }
    }

    // Actualizar datos de vuelo
    public boolean actualizar(Vuelo vuelo) {
        if (vuelo == null) {
            return false;
        }

        String sql = "UPDATE vuelos SET origen = ?, destino = ?, fecha = ?, hora_salida = ?, hora_llegada = ?, " +
                     "duracion = ?, precio_base = ?, clase_disponible = ? WHERE codigo = ?";

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, vuelo.obtenerOrigen());
                ps.setString(2, vuelo.obtenerDestino());
                ps.setString(3, vuelo.obtenerFecha());
                ps.setString(4, vuelo.obtenerHoraSalida());
                ps.setString(5, vuelo.obtenerHoraLlegada());
                ps.setInt(6, vuelo.obtenerDuracion());
                ps.setDouble(7, vuelo.obtenerPrecioBase());
                ps.setString(8, vuelo.obtenerClaseDisponible());
                ps.setString(9, vuelo.obtenerCodigo());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[VueloDAO] Error al actualizar vuelo: " + e.getMessage());
            return false;
        }
    }

    // Eliminar vuelo por codigo
    public boolean eliminar(String codigo) {
        if (codigo == null) {
            return false;
        }

        String sql = "DELETE FROM vuelos WHERE codigo = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, codigo);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[VueloDAO] Error al eliminar vuelo: " + e.getMessage());
            return false;
        }
    }

    // Buscar vuelo por codigo
    public Vuelo buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }

        String sql = "SELECT * FROM vuelos WHERE codigo = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return null;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Vuelo(
                            rs.getString("codigo"),
                            rs.getString("origen"),
                            rs.getString("destino"),
                            rs.getString("fecha"),
                            rs.getString("hora_salida"),
                            rs.getString("hora_llegada"),
                            rs.getInt("duracion"),
                            rs.getDouble("precio_base"),
                            rs.getString("clase_disponible")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[VueloDAO] Error al buscar vuelo: " + e.getMessage());
        }
        return null;
    }

    // Contar vuelos en la tabla
    public int contarVuelos() {
        String sql = "SELECT COUNT(*) FROM vuelos";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return 0;
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    // Listar todos los vuelos (arreglo Vuelo[])
    public Vuelo[] listarTodos() {
        int total = contarVuelos();
        Vuelo[] arregloVuelos = new Vuelo[total];

        String sql = "SELECT * FROM vuelos ORDER BY codigo ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return new Vuelo[0];
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                int indice = 0;
                while (rs.next() && indice < total) {
                    Vuelo vuelo = new Vuelo(
                        rs.getString("codigo"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getString("fecha"),
                        rs.getString("hora_salida"),
                        rs.getString("hora_llegada"),
                        rs.getInt("duracion"),
                        rs.getDouble("precio_base"),
                        rs.getString("clase_disponible")
                    );
                    arregloVuelos[indice] = vuelo;
                    indice = indice + 1;
                }
                return arregloVuelos;
            }
        } catch (SQLException e) {
            System.err.println("[VueloDAO] Error al listar vuelos: " + e.getMessage());
            return new Vuelo[0];
        }
    }

    public boolean vaciarTabla() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DELETE FROM vuelos");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
