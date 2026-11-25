package com.aeropuerto.dao;

import com.aeropuerto.modelo.PuertaEmbarque;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para gestion de puertas de embarque en MySQL (Lista Circular) con try-with-resources integral
public class PuertaDAO {

    public boolean insertar(PuertaEmbarque puerta) {
        if (puerta == null) return false;

        String sql = "INSERT INTO puertas_embarque (numero_puerta, terminal, estado, codigo_vuelo) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE terminal=VALUES(terminal), estado=VALUES(estado), codigo_vuelo=VALUES(codigo_vuelo)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, puerta.obtenerNumeroPuerta());
                ps.setString(2, puerta.obtenerTerminal());
                ps.setString(3, puerta.obtenerEstado());
                ps.setString(4, puerta.obtenerCodigoVuelo());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[PuertaDAO] Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(PuertaEmbarque puerta) {
        if (puerta == null) return false;

        String sql = "UPDATE puertas_embarque SET terminal=?, estado=?, codigo_vuelo=? WHERE numero_puerta=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, puerta.obtenerTerminal());
                ps.setString(2, puerta.obtenerEstado());
                ps.setString(3, puerta.obtenerCodigoVuelo());
                ps.setString(4, puerta.obtenerNumeroPuerta());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[PuertaDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String numeroPuerta) {
        if (numeroPuerta == null) return false;

        String sql = "DELETE FROM puertas_embarque WHERE numero_puerta=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, numeroPuerta);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[PuertaDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public List<PuertaEmbarque> listarTodas() {
        List<PuertaEmbarque> lista = new ArrayList<>();
        String sql = "SELECT * FROM puertas_embarque ORDER BY numero_puerta ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return lista;
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    PuertaEmbarque p = new PuertaEmbarque(
                        rs.getString("numero_puerta"),
                        rs.getString("terminal"),
                        rs.getString("estado"),
                        rs.getString("codigo_vuelo")
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PuertaDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean vaciarTabla() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DELETE FROM puertas_embarque");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
