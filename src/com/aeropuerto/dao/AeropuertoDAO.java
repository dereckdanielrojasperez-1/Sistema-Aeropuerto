package com.aeropuerto.dao;

import com.aeropuerto.modelo.Aeropuerto;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para gestion y persistencia de aeropuertos y terminales en MySQL
public class AeropuertoDAO {

    public boolean insertar(Aeropuerto a) {
        if (a == null) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "INSERT INTO aeropuertos (codigo_iata, nombre, ciudad, total_pistas, pistas_libres, capacidad_stands, aeronaves_actuales, estado, clima, supervisor_cargo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), ciudad=VALUES(ciudad), total_pistas=VALUES(total_pistas), " +
                     "pistas_libres=VALUES(pistas_libres), capacidad_stands=VALUES(capacidad_stands), aeronaves_actuales=VALUES(aeronaves_actuales), " +
                     "estado=VALUES(estado), clima=VALUES(clima), supervisor_cargo=VALUES(supervisor_cargo)";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, a.obtenerCodigoIata());
            ps.setString(2, a.obtenerNombre());
            ps.setString(3, a.obtenerCiudad());
            ps.setInt(4, a.obtenerTotalPistas());
            ps.setInt(5, a.obtenerPistasLibres());
            ps.setInt(6, a.obtenerCapacidadStands());
            ps.setInt(7, a.obtenerAeronavesActuales());
            ps.setString(8, a.obtenerEstado());
            ps.setString(9, a.obtenerClima());
            ps.setString(10, a.obtenerSupervisorACargo());
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[AeropuertoDAO] Error al insertar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public boolean actualizar(Aeropuerto a) {
        if (a == null) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "UPDATE aeropuertos SET nombre=?, ciudad=?, total_pistas=?, pistas_libres=?, capacidad_stands=?, aeronaves_actuales=?, estado=?, clima=?, supervisor_cargo=? WHERE codigo_iata=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, a.obtenerNombre());
            ps.setString(2, a.obtenerCiudad());
            ps.setInt(3, a.obtenerTotalPistas());
            ps.setInt(4, a.obtenerPistasLibres());
            ps.setInt(5, a.obtenerCapacidadStands());
            ps.setInt(6, a.obtenerAeronavesActuales());
            ps.setString(7, a.obtenerEstado());
            ps.setString(8, a.obtenerClima());
            ps.setString(9, a.obtenerSupervisorACargo());
            ps.setString(10, a.obtenerCodigoIata());
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[AeropuertoDAO] Error al actualizar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public boolean eliminar(String codigoIata) {
        if (codigoIata == null || codigoIata.trim().isEmpty()) return false;
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;

        String sql = "DELETE FROM aeropuertos WHERE codigo_iata=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, codigoIata.trim());
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[AeropuertoDAO] Error al eliminar: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }

    public List<Aeropuerto> listarTodos() {
        List<Aeropuerto> lista = new ArrayList<>();
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return lista;

        String sql = "SELECT * FROM aeropuertos ORDER BY codigo_iata ASC";
        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Aeropuerto a = new Aeropuerto(
                        rs.getString("codigo_iata"),
                        rs.getString("nombre"),
                        rs.getString("ciudad"),
                        rs.getInt("total_pistas"),
                        rs.getInt("pistas_libres"),
                        rs.getInt("capacidad_stands"),
                        rs.getInt("aeronaves_actuales"),
                        rs.getString("estado"),
                        rs.getString("clima"),
                        rs.getString("supervisor_cargo")
                );
                lista.add(a);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("[AeropuertoDAO] Error al listar: " + e.getMessage());
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
        return lista;
    }

    public boolean vaciarTabla() {
        Connection conexion = ConexionBD.obtenerConexion();
        if (conexion == null) return false;
        String sql = "DELETE FROM aeropuertos";
        try {
            Statement st = conexion.createStatement();
            st.executeUpdate(sql);
            st.close();
            return true;
        } catch (SQLException e) {
            System.err.println("[AeropuertoDAO] Error al vaciar tabla: " + e.getMessage());
            return false;
        } finally {
            try { conexion.close(); } catch (SQLException ex) {}
        }
    }
}
