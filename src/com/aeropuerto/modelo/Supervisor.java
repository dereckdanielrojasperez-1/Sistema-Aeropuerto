package com.aeropuerto.modelo;

// Modelo representativo de un supervisor de operaciones o torre de control
public class Supervisor {
    private String idSupervisor;
    private String nombre;
    private String dni;
    private String turno; // "Mañana", "Tarde", "Noche"
    private String rol;   // "Supervisor de Torre", "Supervisor de Plataforma", "Jefe de Despacho"
    private String aeropuertoAsignado; // IATA (e.g. LIM)
    private String estado; // "Activo", "En Turno", "Descanso"

    public Supervisor(String idSupervisor, String nombre, String dni, String turno,
                      String rol, String aeropuertoAsignado, String estado) {
        this.idSupervisor = idSupervisor;
        this.nombre = nombre;
        this.dni = dni;
        this.turno = turno;
        this.rol = rol;
        this.aeropuertoAsignado = aeropuertoAsignado;
        this.estado = estado;
    }

    public String obtenerIdSupervisor() {
        return idSupervisor;
    }

    public void establecerIdSupervisor(String idSupervisor) {
        this.idSupervisor = idSupervisor;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void establecerNombre(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerDni() {
        return dni;
    }

    public void establecerDni(String dni) {
        this.dni = dni;
    }

    public String obtenerTurno() {
        return turno;
    }

    public void establecerTurno(String turno) {
        this.turno = turno;
    }

    public String obtenerRol() {
        return rol;
    }

    public void establecerRol(String rol) {
        this.rol = rol;
    }

    public String obtenerAeropuertoAsignado() {
        return aeropuertoAsignado;
    }

    public void establecerAeropuertoAsignado(String aeropuertoAsignado) {
        this.aeropuertoAsignado = aeropuertoAsignado;
    }

    public String obtenerEstado() {
        return estado;
    }

    public void establecerEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | DNI: %s | Rol: %s | Turno: %s | Asignado: %s (%s)",
                idSupervisor, nombre, dni, rol, turno, aeropuertoAsignado, estado);
    }
}
