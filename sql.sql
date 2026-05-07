- =====================================
-- 0. SEGURIDAD: ROLES Y USUARIOS
-- =====================================
CREATE TABLE rol (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(25) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
        ON DELETE CASCADE
);

-- =====================================
-- 1. CLIENTES Y USUARIOS
-- =====================================
CREATE TABLE CLIENTE (
    Id_Cliente SERIAL PRIMARY KEY,
    Nombre_Empresa VARCHAR(100) NOT NULL,
    Contacto VARCHAR(100)
);

-- =====================================
-- 2. PERSONAL
-- =====================================
CREATE TABLE CONDUCTORES (
    id_conductor SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    licencia VARCHAR(50) UNIQUE,
    telefono VARCHAR(15),
    estado VARCHAR(20) DEFAULT 'Activo'
);

CREATE TABLE AUXILIAR (
    Id_Auxiliar SERIAL PRIMARY KEY,
    Id_Usuario INT NOT NULL,
    Estado VARCHAR(25),
    Especialidad VARCHAR(25),
    CONSTRAINT fk_usuario_aux FOREIGN KEY (Id_Usuario) REFERENCES usuario(id_usuario)
);

-- =====================================
-- 3. FLOTA
-- =====================================
CREATE TABLE CAMIONES (
    id_camion SERIAL PRIMARY KEY,
    modelo_camion VARCHAR(100) NOT NULL,
    capacidad_m3 DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    id_conductor INT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conductor_camion FOREIGN KEY (id_conductor) REFERENCES CONDUCTORES(id_conductor) ON DELETE SET NULL
);

-- =====================================
-- 4. PRODUCTOS Y PAQUETES (Estructura UML)
-- =====================================
CREATE TABLE Productos (
    Id_Producto SERIAL PRIMARY KEY,
    Nombre VARCHAR(150) NOT NULL,
    Descripcion VARCHAR(255)
);

CREATE TABLE PAQUETE (
    Id_Paquete SERIAL PRIMARY KEY,
    Id_Cliente INT NOT NULL,
    Nro_Paquete VARCHAR(50) UNIQUE NOT NULL,
    Volumen_m3 DECIMAL(10,2) NOT NULL,
    Descripcion VARCHAR(255),
    CONSTRAINT fk_cliente_paquete FOREIGN KEY (Id_Cliente) REFERENCES CLIENTE(Id_Cliente)
);

-- Tabla intermedia (asociación de muchos a muchos) que conecta paquetes y productos
CREATE TABLE Paquete_Producto (
    Id_Paquete_Producto SERIAL PRIMARY KEY,
    Id_Paquete INT NOT NULL,
    Id_Producto INT NOT NULL,
    Cantidad INT NOT NULL,
    CONSTRAINT fk_pq_paquete FOREIGN KEY (Id_Paquete) REFERENCES PAQUETE(Id_Paquete),
    CONSTRAINT fk_pq_producto FOREIGN KEY (Id_Producto) REFERENCES Productos(Id_Producto)
);

-- =====================================
-- 5. ASIGNACIÓN
-- =====================================
CREATE TABLE ASIGNACION (
    Id_Asignacion SERIAL PRIMARY KEY,
    Id_Camion INT NOT NULL,
    Fecha_Asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_camion_asig FOREIGN KEY (Id_Camion) REFERENCES CAMIONES(id_camion)
);

CREATE TABLE ASIGNACION_PAQUETE (
    Id_Asignacion_Paquete SERIAL PRIMARY KEY,
    Id_Asignacion INT NOT NULL,
    Id_Paquete INT NOT NULL,
    Dir_Entrega VARCHAR(255),
    Cantidad INT NOT NULL,
    CONSTRAINT fk_asignacion_base FOREIGN KEY (Id_Asignacion) REFERENCES ASIGNACION(Id_Asignacion),
    CONSTRAINT fk_paquete_base FOREIGN KEY (Id_Paquete) REFERENCES PAQUETE(Id_Paquete)
);

-- =====================================
-- 6. HISTORIAL DE ESTADOS
-- =====================================
CREATE TABLE HISTORIAL_ESTADOS (
    Id_Historial SERIAL PRIMARY KEY,
    Id_Asig_Paq INT NOT NULL,
    Estado VARCHAR(50) NOT NULL,
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Observacion VARCHAR(255),
    CONSTRAINT fk_asig_paq_historial FOREIGN KEY (Id_Asig_Paq) REFERENCES ASIGNACION_PAQUETE(Id_Asignacion_Paquete)
);




INSERT INTO rol (nombre_rol) VALUES 
('Administrador'),
('Operador'),
('Supervisor');


INSERT INTO usuario (nombre, usuario, contrasena, id_rol) VALUES 
('Jean Paul', 'JeanPaulRojas', 'Jean123', 2),
('Daniel Sundar', 'DanielSundarBonilla', 'Daniel123', 1),
('Michel Stiven', 'MichelStivenDowglas', 'Michel123', 3);

INSERT INTO CLIENTE (Nombre_Empresa, Contacto) VALUES  
('Haceb', 'Juan Valdez'),
('Ajover Darnel', 'Maria Lopez'),
('Postobon', 'Carlos Vives'),
('Tecnoquímicas', 'Ana María Rojas'),
('Alquería', 'Pedro Páramo');

INSERT INTO CONDUCTORES (nombre_completo, licencia, telefono, estado) VALUES  
('Stiven Ramirez', 'LIC-001', '3001234567', 'Activo'),
('Edward Gomez', 'LIC-002', '3007654321', 'Activo'),
('Carlos Mendoza', 'LIC-003', '3109876543', 'Activo');

-- Se asume el id de usuario correspondiente creado previamente
INSERT INTO AUXILIAR (Id_Usuario, Estado, Especialidad) VALUES 
(1, 'Activo', 'Carga Pesada'),
(2, 'Activo', 'Materiales Frágiles'),
(3, 'Activo', 'Logística Inversa');

INSERT INTO CAMIONES (modelo_camion, capacidad_m3, estado, id_conductor) VALUES  
('Chevrolet NPR', 20.25, 'Disponible', 1),
('NPR Turbo', 33.75, 'Disponible', 2),
('Camion Liviano', 16.94, 'Disponible', NULL),
('Fotón Aumark', 28.50, 'Disponible', 3);

INSERT INTO PAQUETE (Id_Cliente, Nro_Paquete, Volumen_m3, Descripcion) VALUES  
(1, 'PKT-001', 5.50, 'Nevera Industrial'),
(2, 'PKT-002', 2.00, 'Envases plásticos'),
(1, 'PKT-003', 1.50, 'Microondas'),
(3, 'PKT-004', 8.20, 'Cajas de gaseosa 1.5L'),
(4, 'PKT-005', 3.10, 'Cajas de medicamentos'),
(5, 'PKT-006', 4.50, 'Lácteos refrigerados'),
(2, 'PKT-007', 6.00, 'Rollos de polietileno'),
(1, 'PKT-008', 1.20, 'Repuestos eléctricos'),
(3, 'PKT-009', 9.50, 'Botellas de agua mineral'),
(5, 'PKT-010', 3.80, 'Yogurt en presentación familiar'),
(4, 'PKT-011', 2.50, 'Insumos médicos'),
(2, 'PKT-012', 4.00, 'Material de empaque corrugado'),
(1, 'PKT-013', 7.50, 'Estufas industriales a gas'),
(3, 'PKT-014', 10.10, 'Bebidas azucaradas no retornables'),
(5, 'PKT-015', 5.00, 'Queso campesino por mayor');

INSERT INTO ASIGNACION (Id_Camion) VALUES (1);

INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES
(1, 1, 'Cali Norte', 2),
(1, 2, 'Cali Sur', 1);

INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES
(1, 'En reparto', 'Salida'),
(1, 'Entregado', 'OK'),
(2, 'En reparto', 'En camino'),
(2, 'No entregado', 'Cliente ausente');