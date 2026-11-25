package com.aeropuerto.dao;

import com.aeropuerto.modelo.Ruta;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para gestion de rutas en MySQL con try-with-resources integral
public class RutaDAO {

    public boolean insertar(Ruta ruta) {
        if (ruta == null) return false;

        String sql = "INSERT INTO rutas (id, origen, destino, distancia) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE origen=VALUES(origen), destino=VALUES(destino), distancia=VALUES(distancia)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, ruta.obtenerId());
                ps.setString(2, ruta.obtenerOrigen());
                ps.setString(3, ruta.obtenerDestino());
                ps.setDouble(4, ruta.obtenerDistanciaKm());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Ruta ruta) {
        if (ruta == null) return false;

        String sql = "UPDATE rutas SET origen=?, destino=?, distancia=? WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, ruta.obtenerOrigen());
                ps.setString(2, ruta.obtenerDestino());
                ps.setDouble(3, ruta.obtenerDistanciaKm());
                ps.setString(4, ruta.obtenerId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        if (id == null) return false;

        String sql = "DELETE FROM rutas WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public Ruta buscarPorId(String id) {
        if (id == null) return null;

        String sql = "SELECT * FROM rutas WHERE id=?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return null;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Ruta(rs.getString("id"), rs.getString("origen"), rs.getString("destino"), rs.getDouble("distancia"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Ruta> listarTodas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutas ORDER BY id ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return lista;
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    lista.add(new Ruta(rs.getString("id"), rs.getString("origen"), rs.getString("destino"), rs.getDouble("distancia")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean vaciarTabla() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DELETE FROM rutas");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
