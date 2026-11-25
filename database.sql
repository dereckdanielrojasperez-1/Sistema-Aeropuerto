-- =============================================================================
-- SCRIPT DE BASE DE DATOS: SistemaGestionAeropuerto (CORPAC S.A.)
-- Motor: MySQL 8.0+ / 9.x (InnoDB con Integridad Referencial)
-- Base de datos: aeropuerto_db
-- =============================================================================

CREATE DATABASE IF NOT EXISTS aeropuerto_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aeropuerto_db;

-- 1. TABLA: usuarios (Control de Acceso y Roles RBAC)
CREATE TABLE IF NOT EXISTS usuarios (
    usuario VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 2. TABLA: vuelos (Arreglos Unidimensionales)
CREATE TABLE IF NOT EXISTS vuelos (
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

-- 3. TABLA: asientos (Matriz Bidimensional 10x5 de Cabina)
CREATE TABLE IF NOT EXISTS asientos (
    codigo_vuelo VARCHAR(20) NOT NULL DEFAULT 'CP-2023',
    fila INT NOT NULL,
    columna INT NOT NULL,
    estado INT NOT NULL DEFAULT 0,
    dni_pasajero VARCHAR(20),
    PRIMARY KEY (codigo_vuelo, fila, columna),
    CONSTRAINT fk_asientos_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. TABLA: pasajes (Reservas y Boletas)
CREATE TABLE IF NOT EXISTS pasajes (
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

-- 5. TABLA: equipaje (Lista Enlazada Simple)
CREATE TABLE IF NOT EXISTS equipaje (
    id VARCHAR(20) PRIMARY KEY,
    dni_pasajero VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL DEFAULT 'CP-2023',
    peso DOUBLE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    CONSTRAINT fk_equipaje_vuelo FOREIGN KEY (codigo_vuelo) REFERENCES vuelos(codigo) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. TABLA: rutas (Lista Doblemente Enlazada)
CREATE TABLE IF NOT EXISTS rutas (
    id VARCHAR(20) PRIMARY KEY,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    distancia DOUBLE NOT NULL
) ENGINE=InnoDB;

-- 7. TABLA: puertas_embarque (Lista Circular)
CREATE TABLE IF NOT EXISTS puertas_embarque (
    numero_puerta VARCHAR(20) PRIMARY KEY,
    terminal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20)
) ENGINE=InnoDB;

-- 8. TABLA: aeropuertos (Red Nacional)
CREATE TABLE IF NOT EXISTS aeropuertos (
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

-- 9. TABLA: supervisores (Control de Operaciones)
CREATE TABLE IF NOT EXISTS supervisores (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    aeropuerto_asignado VARCHAR(10),
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_supervisores_aeropuerto FOREIGN KEY (aeropuerto_asignado) REFERENCES aeropuertos(codigo_iata) ON DELETE SET NULL
) ENGINE=InnoDB;

INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) VALUES
('admin', 'admin123', 'ADMIN', '00000000', 'Administrador Principal CORPAC'),
('operador', 'operador123', 'OPERADOR', '11111111', 'Operador de Vuelos CORPAC'),
('supervisor', 'sup123', 'SUPERVISOR', '22222222', 'Supervisor de Operaciones CORPAC'),
('pasajero', '123456', 'PASAJERO', '72345678', 'Pasajero Comercial');
