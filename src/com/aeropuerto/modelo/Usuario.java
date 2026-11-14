package com.aeropuerto.modelo;

import java.io.Serializable;

// Entidad que representa a un usuario del sistema (Administrador o Pasajero)
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String usuario;
    private String clave;
    private String rol; // "ADMIN" o "PASAJERO"
    private String dni;
    private String nombre;

    public Usuario(String usuario, String clave, String rol, String dni, String nombre) {
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.dni = dni;
        this.nombre = nombre;
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public void establecerUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String obtenerClave() {
        return clave;
    }

    public void establecerClave(String clave) {
        this.clave = clave;
    }

    public String obtenerRol() {
        return rol;
    }

    public void establecerRol(String rol) {
        this.rol = rol;
    }

    public String obtenerDni() {
        return dni;
    }

    public void establecerDni(String dni) {
        this.dni = dni;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void establecerNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean esAdministrador() {
        return rol != null && rol.equalsIgnoreCase("ADMIN");
    }

    public boolean esPasajero() {
        return rol != null && rol.equalsIgnoreCase("PASAJERO");
    }

    @Override
    public String toString() {
        return "Usuario: " + usuario + " | Rol: " + rol + " | Nombre: " + nombre;
    }
}