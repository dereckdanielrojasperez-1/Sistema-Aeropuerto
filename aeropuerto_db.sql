-- ========================================================
-- SISTEMA DE GESTION AEROPORTUARIA - CORPAC S.A.
-- Script SQL para creacion de base de datos y 9 tablas
-- con integridad referencial (FOREIGN KEY + ON DELETE CASCADE)
-- Curso: Algoritmos y Estructuras de Datos (100000I53N)
-- ========================================================

CREATE DATABASE IF NOT EXISTS aeropuerto_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aeropuerto_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS equipaje;
DROP TABLE IF EXISTS pasajes;
DROP TABLE IF EXISTS asientos;
DROP TABLE IF EXISTS pasajeros;
DROP TABLE IF EXISTS puertas_embarque;
DROP TABLE IF EXISTS rutas;
DROP TABLE IF EXISTS supervisores;
DROP TABLE IF EXISTS aeropuertos;
DROP TABLE IF EXISTS vuelos;
DROP TABLE IF EXISTS usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Tabla de Usuarios y Roles (Credenciales de Acceso RBAC)
CREATE TABLE usuarios (
    usuario VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

INSERT INTO usuarios (usuario, clave, rol, dni, nombre) VALUES
('admin', 'admin123', 'ADMIN', '00000000', 'Administrador Principal CORPAC'),
('operador', 'operador123', 'OPERADOR', '11111111', 'Operador de Vuelos CORPAC'),
('supervisor', 'sup123', 'SUPERVISOR', '22222222', 'Supervisor de Operaciones CORPAC'),
('pasajero', '123456', 'PASAJERO', '72345678', 'Pasajero Comercial');

-- 2. Tabla de Vuelos (Arreglos Unidimensionales Dinamicos)
CREATE TABLE vuelos (
    codigo VARCHAR(20) PRIMARY KEY,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    fecha VARCHAR(20) NOT NULL,
    hora_salida VARCHAR(10) NOT NULL,
    hora_llegada VARCHAR(10) NOT NULL,
    duracion INT NOT NULL,
    precio_base DOUBLE NOT NULL,
    clase_disponible VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- 3. Tabla de Asientos de Cabina (Matriz 10x5 por Vuelo)
CREATE TABLE asientos (
    codigo_vuelo VARCHAR(20) NOT NULL,
    fila INT NOT NULL,
    columna INT NOT NULL,
    estado INT NOT NULL DEFAULT 0,
    dni_pasajero VARCHAR(20),
    PRIMARY KEY (codigo_vuelo, fila, columna),
    CONSTRAINT fk_asientos_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Tabla de Pasajes / Reservas (Emision y TAD Pila LIFO)
CREATE TABLE pasajes (
    id_pasaje VARCHAR(30) PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL,
    dni_pasajero VARCHAR(20) NOT NULL,
    nombre_pasajero VARCHAR(100) NOT NULL,
    fila INT NOT NULL,
    columna INT NOT NULL,
    tarifa_total DOUBLE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    CONSTRAINT fk_pasajes_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE,
    CONSTRAINT fk_pasajes_usuario FOREIGN KEY (usuario) REFERENCES usuarios(usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabla de Equipaje (TAD Lista Enlazada Simple)
CREATE TABLE equipaje (
    id VARCHAR(20) PRIMARY KEY,
    dni_pasajero VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL,
    peso DOUBLE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    CONSTRAINT fk_equipaje_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Tabla de Rutas Comerciales (TAD Lista Doblemente Enlazada)
CREATE TABLE rutas (
    id VARCHAR(20) PRIMARY KEY,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    distancia DOUBLE NOT NULL
) ENGINE=InnoDB;

-- 7. Tabla de Puertas de Embarque (TAD Lista Circular)
CREATE TABLE puertas_embarque (
    numero_puerta VARCHAR(20) PRIMARY KEY,
    terminal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20)
) ENGINE=InnoDB;

-- 8. Tabla de Aeropuertos y Terminales (Control de Disponibilidad y Pistas)
CREATE TABLE aeropuertos (
    codigo_iata VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    total_pistas INT NOT NULL,
    pistas_libres INT NOT NULL,
    capacidad_stands INT NOT NULL,
    aeronaves_actuales INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    clima VARCHAR(50),
    supervisor_cargo VARCHAR(100)
) ENGINE=InnoDB;

-- 9. Tabla de Supervisores de Operaciones y Torre de Control
CREATE TABLE supervisores (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    aeropuerto_asignado VARCHAR(10),
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_supervisores_aeropuerto FOREIGN KEY (aeropuerto_asignado) REFERENCES aeropuertos(codigo_iata) ON DELETE SET NULL
) ENGINE=InnoDB;
