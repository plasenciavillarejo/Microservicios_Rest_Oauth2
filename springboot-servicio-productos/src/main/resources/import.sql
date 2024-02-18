INSERT INTO regiones (nombre) VALUES ('Sudamérica');
INSERT INTO regiones (nombre) VALUES ('Centroamérica');
INSERT INTO regiones (nombre) VALUES ('Norteamérica');
INSERT INTO regiones (nombre) VALUES ('Europa');
INSERT INTO regiones (nombre) VALUES ('Asia');
INSERT INTO regiones (nombre) VALUES ('Africa');
INSERT INTO regiones (nombre) VALUES ('Oceanía');
INSERT INTO regiones (nombre) VALUES ('Antártida');

INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Pantalla Panasonic',800, NOW() ,1);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Pantalla Sony',700, NOW() ,1);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Apple Iphone 8',800, NOW(), 2);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Sony Notebook',810, NOW(), 2);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Zapataillas Nike',830, NOW(), 3);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Zapatilla Adidas',3400, NOW(), 3);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Zapatillas Reebok',200, NOW(), 4);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Cinturón Guess',1500, NOW(), 5);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Panasonic LCD',800, NOW() ,1);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Sony LCD',700, NOW() ,1);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Ihpone 7 Apple',800, NOW(), 2);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Sony Notebook X9',810, NOW(), 2);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Nike 9911',830, NOW(), 3);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Adidas Producto Bueno',3400, NOW(), 3);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Reebok Mejor producto',200, NOW(), 6);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Guess mochila accesorio',1500, NOW(), 6);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Panasonic',800, NOW() ,8);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Sony',700, NOW() ,8);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Apple',800, NOW(), 8);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Sony Notebook',810, NOW(), 2);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Nike',830, NOW(), 7);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Adidas',3400, NOW(), 7);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Reebok',200, NOW(), 4);
INSERT INTO productos (nombre, precio, create_at, region_id) VALUES ('Guess',1500, NOW(), 5);


INSERT INTO facturas (descripcion, observacion, create_at) VALUES ('Factura equipos de oficina','Factura de cobro en equipos de oficina para la Region de Murcia',NOW());
INSERT INTO facturas_items (cantidad,producto_id,factura_id) VALUES (1,1,1);
INSERT INTO facturas_items (cantidad,producto_id,factura_id) VALUES (3,2,1);
INSERT INTO facturas_items (cantidad,producto_id,factura_id) VALUES (2,5,1);




