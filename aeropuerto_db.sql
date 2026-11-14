-- ========================================================
-- SISTEMA DE GESTION AEROPORTUARIA - CORPAC
-- Script SQL para creacion de base de datos y tablas
-- Curso: Algoritmos y Estructuras de Datos (Avance 2)
-- ========================================================

CREATE DATABASE IF NOT EXISTS aeropuerto_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aeropuerto_db;

-- 1. Tabla de Usuarios y Roles (Credenciales de Acceso)
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
    usuario VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- Credenciales predeterminadas para administracion y pasajeros
INSERT INTO usuarios (usuario, clave, rol, dni, nombre) VALUES
('admin', 'admin123', 'ADMIN', '00000000', 'Administrador Principal CORPAC'),
('pasajero', '123456', 'PASAJERO', '72345678', 'Pasajero Comercial');

-- 2. Tabla de Vuelos (Arreglos Unidimensionales Dinamicos)
DROP TABLE IF EXISTS vuelos;
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
DROP TABLE IF EXISTS asientos;
CREATE TABLE asientos (
    codigo_vuelo VARCHAR(20) NOT NULL,
    fila INT NOT NULL,
    columna INT NOT NULL,
    estado INT NOT NULL DEFAULT 0,
    dni_pasajero VARCHAR(20),
    PRIMARY KEY (codigo_vuelo, fila, columna)
) ENGINE=InnoDB;

-- 4. Tabla de Equipaje (TAD Lista Enlazada Simple)
DROP TABLE IF EXISTS equipaje;
CREATE TABLE equipaje (
    id VARCHAR(20) PRIMARY KEY,
    dni_pasajero VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL,
    peso DOUBLE NOT NULL,
    tipo VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- 5. Tabla de Rutas Comerciales (TAD Lista Doblemente Enlazada)
DROP TABLE IF EXISTS rutas;
CREATE TABLE rutas (
    id VARCHAR(20) PRIMARY KEY,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    distancia DOUBLE NOT NULL
) ENGINE=InnoDB;

-- 6. Tabla de Puertas de Embarque (TAD Lista Circular)
DROP TABLE IF EXISTS puertas_embarque;
CREATE TABLE puertas_embarque (
    numero_puerta VARCHAR(20) PRIMARY KEY,
    terminal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20)
) ENGINE=InnoDB;

-- 7. Tabla de Pasajeros
DROP TABLE IF EXISTS pasajeros;
CREATE TABLE pasajeros (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    clase VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL,
    asiento_fila INT DEFAULT -1,
    asiento_columna INT DEFAULT -1
) ENGINE=InnoDB;

-- 8. Tabla de Aeropuertos y Terminales (Control de Disponibilidad y Pistas)
DROP TABLE IF EXISTS aeropuertos;
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
DROP TABLE IF EXISTS supervisores;
CREATE TABLE supervisores (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    aeropuerto_asignado VARCHAR(10),
    estado VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- NOTA: Las tablas operativas inician completamente vacías (0 registros)
-- para registrar datos en tiempo real directamente desde la aplicación.

