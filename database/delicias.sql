IF DB_ID('restaurant') IS NULL
    CREATE DATABASE restaurant;
GO
USE restaurant;
GO

CREATE TABLE clientes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(255) NOT NULL,
    correo NVARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE platillos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(255) NOT NULL UNIQUE,
    precio FLOAT NOT NULL CHECK (precio > 0)
);

CREATE TABLE pedidos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_pedidos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

CREATE TABLE pedido_detalle (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    platillo_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    CONSTRAINT FK_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT FK_detalle_platillo FOREIGN KEY (platillo_id) REFERENCES platillos(id)
);

USE delicias;
GO

/* --- (Opcional) limpiar antes de poblar ---
-- DELETE FROM pedido_detalle;
-- DELETE FROM pedidos;
-- DELETE FROM platillos;
-- DELETE FROM clientes;
-- GO
*/

------------------------------
-- 1) Clientes (5 registros)
------------------------------
INSERT INTO clientes (nombre, correo) VALUES
('Chris Martinez',   'chris@delicias.test'),
('Megan Lopez', 'megan@delicias.test'),
('Juan Hernandez', 'juan@delicias.test'),
('Pepe Garza', 'pepe@delicias.test'),
('Hector Badillo','hector@delicias.test');
GO

------------------------------
-- 2) Platillos (5 registros)
------------------------------
INSERT INTO platillos (nombre, precio) VALUES
('Enchiladas Verdes', 120.00),
('Tacos al Pastor',    90.00),
('Sopa Azteca',        75.00),
('Chiles en Nogada',  180.00),
('Quesadillas',        60.00);
GO

------------------------------------
-- 3) Pedidos + Detalles (5 pedidos)
------------------------------------
DECLARE @c1 BIGINT = (SELECT id FROM clientes WHERE correo='chris@delicias.test');
DECLARE @c2 BIGINT = (SELECT id FROM clientes WHERE correo='megan@delicias.test');
DECLARE @c3 BIGINT = (SELECT id FROM clientes WHERE correo='juan@delicias.test');
DECLARE @c4 BIGINT = (SELECT id FROM clientes WHERE correo='pepe@delicias.test');
DECLARE @c5 BIGINT = (SELECT id FROM clientes WHERE correo='hector@delicias.test');

DECLARE @p_enchiladas BIGINT = (SELECT id FROM platillos WHERE nombre='Enchiladas Verdes');
DECLARE @p_pastor     BIGINT = (SELECT id FROM platillos WHERE nombre='Tacos al Pastor');
DECLARE @p_sopa       BIGINT = (SELECT id FROM platillos WHERE nombre='Sopa Azteca');
DECLARE @p_nogada     BIGINT = (SELECT id FROM platillos WHERE nombre='Chiles en Nogada');
DECLARE @p_quesa      BIGINT = (SELECT id FROM platillos WHERE nombre='Quesadillas');

-- Pedido 1: Ana - 2 enchiladas + 1 sopa
INSERT INTO pedidos (cliente_id) VALUES (@c1);
DECLARE @o1 BIGINT = SCOPE_IDENTITY();
INSERT INTO pedido_detalle (pedido_id, platillo_id, cantidad) VALUES
(@o1, @p_enchiladas, 2),
(@o1, @p_sopa,       1);

-- Pedido 2: Luis - 3 tacos + 1 quesadilla
INSERT INTO pedidos (cliente_id) VALUES (@c2);
DECLARE @o2 BIGINT = SCOPE_IDENTITY();
INSERT INTO pedido_detalle (pedido_id, platillo_id, cantidad) VALUES
(@o2, @p_pastor, 3),
(@o2, @p_quesa,  1);

-- Pedido 3: Ana - 1 nogada
INSERT INTO pedidos (cliente_id) VALUES (@c1);
DECLARE @o3 BIGINT = SCOPE_IDENTITY();
INSERT INTO pedido_detalle (pedido_id, platillo_id, cantidad) VALUES
(@o3, @p_nogada, 1);

-- Pedido 4: María - 2 quesadillas + 1 sopa
INSERT INTO pedidos (cliente_id) VALUES (@c3);
DECLARE @o4 BIGINT = SCOPE_IDENTITY();
INSERT INTO pedido_detalle (pedido_id, platillo_id, cantidad) VALUES
(@o4, @p_quesa, 2),
(@o4, @p_sopa,  1);

-- Pedido 5: Sofía - 1 enchiladas + 2 tacos
INSERT INTO pedidos (cliente_id) VALUES (@c5);
DECLARE @o5 BIGINT = SCOPE_IDENTITY();
INSERT INTO pedido_detalle (pedido_id, platillo_id, cantidad) VALUES
(@o5, @p_enchiladas, 1),
(@o5, @p_pastor,     2);
GO

------------------------------
-- 4) Comprobaciones rápidas
------------------------------
-- Pedidos por cliente
SELECT p.id AS pedido, c.nombre AS cliente, p.fecha
FROM pedidos p
JOIN clientes c ON c.id = p.cliente_id
ORDER BY p.id;

-- Detalles con subtotal
SELECT d.pedido_id, pl.nombre, d.cantidad, pl.precio,
       d.cantidad * pl.precio AS subtotal
FROM pedido_detalle d
JOIN platillos pl ON pl.id = d.platillo_id
ORDER BY d.pedido_id, pl.nombre;

-- Total a pagar por pedido
SELECT p.id AS pedido, SUM(d.cantidad * pl.precio) AS total
FROM pedidos p
JOIN pedido_detalle d ON d.pedido_id = p.id
JOIN platillos pl ON pl.id = d.platillo_id
GROUP BY p.id
ORDER BY p.id;