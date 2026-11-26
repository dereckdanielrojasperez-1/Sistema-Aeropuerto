package com.aeropuerto.dao;

import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// DAO para autenticacion y control RBAC de usuarios en MySQL
public class UsuarioDAO {

    // Insertar usuario
    public boolean insertar(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        String sql = "INSERT INTO usuarios (usuario, clave, rol, dni, nombre) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return false;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, usuario.obtenerUsuario().trim());
                ps.setString(2, usuario.obtenerClave().trim());
                ps.setString(3, usuario.obtenerRol());
                ps.setString(4, usuario.obtenerDni());
                ps.setString(5, usuario.obtenerNombre());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // Autenticar usuario con credenciales
    public Usuario autenticar(String usuario, String clave) {
        if (usuario == null || clave == null) {
            return null;
        }

        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND clave = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return null;
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, usuario.trim());
                ps.setString(2, clave.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Usuario(
                            rs.getString("usuario"),
                            rs.getString("clave"),
                            rs.getString("rol"),
                            rs.getString("dni"),
                            rs.getString("nombre")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al autenticar: " + e.getMessage());
        }
        return null;
    }

    // Contar usuarios registrados
    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuarios";
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

    // Listar todos los usuarios
    public Usuario[] listarTodos() {
        int total = contarUsuarios();
        Usuario[] arreglo = new Usuario[total];

        String sql = "SELECT * FROM usuarios ORDER BY usuario ASC";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) return new Usuario[0];
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                int indice = 0;
                while (rs.next() && indice < total) {
                    Usuario u = new Usuario(
                        rs.getString("usuario"),
                        rs.getString("clave"),
                        rs.getString("rol"),
                        rs.getString("dni"),
                        rs.getString("nombre")
                    );
                    arreglo[indice] = u;
                    indice = indice + 1;
                }
                return arreglo;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al listar usuarios: " + e.getMessage());
            return new Usuario[0];
        }
    }
}
