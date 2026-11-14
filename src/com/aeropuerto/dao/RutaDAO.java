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

// DAO para gestion de rutas en MySQL
public class RutaDAO {

    public boolean insertar(Ruta ruta) {
        if (ruta == null) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "INSERT INTO rutas (id, origen, destino, distancia) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE origen=VALUES(origen), destino=VALUES(destino), distancia=VALUES(distancia)";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, ruta.obtenerId());
            ps.setString(2, ruta.obtenerOrigen());
            ps.setString(3, ruta.obtenerDestino());
            ps.setDouble(4, ruta.obtenerDistanciaKm());
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al insertar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public boolean actualizar(Ruta ruta) {
        if (ruta == null) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "UPDATE rutas SET origen=?, destino=?, distancia=? WHERE id=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, ruta.obtenerOrigen());
            ps.setString(2, ruta.obtenerDestino());
            ps.setDouble(3, ruta.obtenerDistanciaKm());
            ps.setString(4, ruta.obtenerId());
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al actualizar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public boolean eliminar(String id) {
        if (id == null) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "DELETE FROM rutas WHERE id=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, id);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al eliminar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public Ruta buscarPorId(String id) {
        if (id == null) return null;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return null;

        String sql = "SELECT * FROM rutas WHERE id=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            Ruta r = null;
            if (rs.next()) {
                r = new Ruta(rs.getString("id"), rs.getString("origen"), rs.getString("destino"), rs.getDouble("distancia"));
            }
            rs.close();
            ps.close();
            return r;
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al buscar: " + e.getMessage());
            return null;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public List<Ruta> listarTodas() {
        List<Ruta> lista = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return lista;

        String sql = "SELECT * FROM rutas ORDER BY id ASC";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Ruta(rs.getString("id"), rs.getString("origen"), rs.getString("destino"), rs.getDouble("distancia")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("[RutaDAO] Error al listar: " + e.getMessage());
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
        return lista;
    }

    public boolean vaciarTabla() {
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("DELETE FROM rutas");
            stmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }
}
