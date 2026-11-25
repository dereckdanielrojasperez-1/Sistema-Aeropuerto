package com.aeropuerto.dao;

import com.aeropuerto.modelo.Supervisor;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para gestion y persistencia de supervisores de operaciones en MySQL con try-with-resources integral
public class SupervisorDAO {

    public boolean insertar(Supervisor s) {
        if (s == null) return false;

        String sql = "INSERT INTO supervisores (id, nombre, dni, turno, rol, aeropuerto_asignado, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), dni=VALUES(dni), turno=VALUES(turno), " +
                     "rol=VALUES(rol), aeropuerto_asignado=VALUES(aeropuerto_asignado), estado=VALUES(estado)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, s.obtenerIdSupervisor());
                ps.setString(2, s.obtenerNombre());
                ps.setString(3, s.obtenerDni());
                ps.setString(4, s.obtenerTurno());
                ps.setString(5, s.obtenerRol());
                ps.setString(6, s.obtenerAeropuertoAsignado());
                ps.setString(7, s.obtenerEstado());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[SupervisorDAO] Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Supervisor s) {
        if (s == null) return false;

        String sql = "UPDATE supervisores SET nombre=?, dni=?, turno=?, rol=?, aeropuerto_asignado=?, estado=? WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, s.obtenerNombre());
                ps.setString(2, s.obtenerDni());
                ps.setString(3, s.obtenerTurno());
                ps.setString(4, s.obtenerRol());
                ps.setString(5, s.obtenerAeropuertoAsignado());
                ps.setString(6, s.obtenerEstado());
                ps.setString(7, s.obtenerIdSupervisor());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[SupervisorDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        if (id == null || id.trim().isEmpty()) return false;

        String sql = "DELETE FROM supervisores WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, id.trim());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[SupervisorDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public List<Supervisor> listarTodos() {
        List<Supervisor> lista = new ArrayList<>();
        String sql = "SELECT * FROM supervisores ORDER BY id ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return lista;
            try (Statement st = conexion.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Supervisor s = new Supervisor(
                            rs.getString("id"),
                            rs.getString("nombre"),
                            rs.getString("dni"),
                            rs.getString("turno"),
                            rs.getString("rol"),
                            rs.getString("aeropuerto_asignado"),
                            rs.getString("estado")
                    );
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SupervisorDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean vaciarTabla() {
        String sql = "DELETE FROM supervisores";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement st = conexion.createStatement()) {
                st.executeUpdate(sql);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[SupervisorDAO] Error al vaciar tabla: " + e.getMessage());
            return false;
        }
    }
}
