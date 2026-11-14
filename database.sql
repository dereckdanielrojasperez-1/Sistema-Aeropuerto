-- =============================================================================
-- SCRIPT DE BASE DE DATOS: SistemaGestionAeropuerto (CORPAC)
-- Motor: MySQL 8.0+ / MariaDB
-- Base de datos: aeropuerto_db
-- =============================================================================

CREATE DATABASE IF NOT EXISTS aeropuerto_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aeropuerto_db;

-- -----------------------------------------------------------------------------
-- 1. TABLA: usuarios (Módulo 1: Control de Acceso y Roles)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    usuario VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. TABLA: vuelos (Módulo 2: CRUD con Arreglos Unidimensionales - Avance 1)
-- -----------------------------------------------------------------------------
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
);

-- -----------------------------------------------------------------------------
-- 3. TABLA: asientos (Módulo 3: Matriz Bidimensional 10x5 de Cabina - Avance 1)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS asientos (
    fila INT NOT NULL,
    columna INT NOT NULL,
    estado INT NOT NULL DEFAULT 0, -- 0 = Libre, 1 = Ocupado
    dni_pasajero VARCHAR(20),
    PRIMARY KEY (fila, columna)
);

-- -----------------------------------------------------------------------------
-- 4. TABLA: equipaje (Módulo 4: Lista Enlazada Simple)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS equipaje (
    id VARCHAR(20) PRIMARY KEY,
    peso DOUBLE NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 5. TABLA: rutas (Módulo 5: Lista Doblemente Enlazada)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rutas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    distancia INT NOT NULL
);

-- -----------------------------------------------------------------------------
-- 6. TABLA: pasajeros (Módulo 8: Árbol AVL de Pasajeros por DNI)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pasajeros (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    clase VARCHAR(20) NOT NULL,
    codigo_vuelo VARCHAR(20) NOT NULL,
    asiento_fila INT DEFAULT -1,
    asiento_columna INT DEFAULT -1
);

-- -----------------------------------------------------------------------------
-- DATOS INICIALES POR DEFECTO
-- -----------------------------------------------------------------------------

-- Usuario Administrador Principal
INSERT IGNORE INTO usuarios (usuario, clave, rol, dni, nombre) 
VALUES ('admin', 'admin123', 'ADMIN', '00000000', 'Administrador Principal');

-- Vuelos Oficiales Iniciales
INSERT IGNORE INTO vuelos (codigo, origen, destino, fecha, hora_salida, hora_llegada, duracion, precio_base, clase_disponible) VALUES
('LA2023', 'Lima / Chosica', 'Cusco', '2026-09-15', '08:30', '09:45', 75, 150.0, 'Primera'),
('LA2102', 'Lima / Chosica', 'Arequipa', '2026-09-15', '10:15', '11:45', 90, 130.0, 'Ejecutiva'),
('LA2234', 'Lima / Chosica', 'Iquitos', '2026-09-15', '12:00', '13:50', 110, 180.0, 'Turista'),
('LA2340', 'Lima / Chosica', 'Piura', '2026-09-15', '14:45', '16:15', 90, 140.0, 'Turista'),
('LA2081', 'Lima / Chosica', 'Tarapoto', '2026-09-15', '16:30', '17:45', 75, 125.0, 'Ejecutiva');

-- Rutas Aéreas Iniciales
INSERT IGNORE INTO rutas (id, origen, destino, distancia) VALUES
(1, 'Lima / Chosica', 'Cusco', 600),
(2, 'Lima / Chosica', 'Arequipa', 770),
(3, 'Lima / Chosica', 'Iquitos', 1010),
(4, 'Lima / Chosica', 'Piura', 850);
