package com.aeropuerto.dao;

import com.aeropuerto.modelo.Equipaje;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para gestion de equipajes en MySQL con try-with-resources integral (Leak-free)
public class EquipajeDAO {

    public boolean insertar(Equipaje equipaje) {
        if (equipaje == null) return false;

        String sql = "INSERT INTO equipaje (id, dni_pasajero, codigo_vuelo, peso, tipo) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE dni_pasajero=VALUES(dni_pasajero), codigo_vuelo=VALUES(codigo_vuelo), peso=VALUES(peso), tipo=VALUES(tipo)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, equipaje.obtenerId());
                ps.setString(2, equipaje.obtenerDniPasajero());
                ps.setString(3, equipaje.obtenerCodigoVuelo());
                ps.setDouble(4, equipaje.obtenerPeso());
                ps.setString(5, equipaje.obtenerTipo());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[EquipajeDAO] Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Equipaje equipaje) {
        if (equipaje == null) return false;

        String sql = "UPDATE equipaje SET dni_pasajero=?, codigo_vuelo=?, peso=?, tipo=? WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, equipaje.obtenerDniPasajero());
                ps.setString(2, equipaje.obtenerCodigoVuelo());
                ps.setDouble(3, equipaje.obtenerPeso());
                ps.setString(4, equipaje.obtenerTipo());
                ps.setString(5, equipaje.obtenerId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[EquipajeDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        if (id == null) return false;

        String sql = "DELETE FROM equipaje WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[EquipajeDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public Equipaje buscarPorId(String id) {
        if (id == null) return null;

        String sql = "SELECT * FROM equipaje WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return null;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String dni = "";
                        try { dni = rs.getString("dni_pasajero"); } catch (Exception ex) {}
                        String vuelo = "CP-2023";
                        try { vuelo = rs.getString("codigo_vuelo"); } catch (Exception ex) {}
                        return new Equipaje(rs.getString("id"), dni, vuelo, rs.getDouble("peso"), rs.getString("tipo"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[EquipajeDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Equipaje> listarTodos() {
        List<Equipaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipaje ORDER BY id ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return lista;
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String dni = "";
                    try { dni = rs.getString("dni_pasajero"); } catch (Exception ex) {}
                    String vuelo = "General";
                    try { vuelo = rs.getString("codigo_vuelo"); } catch (Exception ex) {}
                    lista.add(new Equipaje(rs.getString("id"), dni, vuelo, rs.getDouble("peso"), rs.getString("tipo")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[EquipajeDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean vaciarTabla() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DELETE FROM equipaje");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
