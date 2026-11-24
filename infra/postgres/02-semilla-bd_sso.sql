-- PC2 · ejecutar DESPUÉS de arrancar sso-monolito una vez (Hibernate crea las tablas)
-- psql -U sso -d bd_sso -f 02-semilla-bd_sso.sql
INSERT INTO cliente (cedula, usuario_ad, nombre, email, celular, direccion) VALUES
  ('1010', 'ana',       'Ana Gómez',                'ana@correo.com',       '3001112233', 'Calle 45 # 12-30, Bogotá'),
  ('2020', 'luis',      'Luis Pérez',               'luis@correo.com',      '3004445566', 'Carrera 7 # 72-10, Bogotá'),
  ('8888', 'revision',  'Cliente en revisión OFAC', 'revision@correo.com',  '3007778899', 'Calle 100 # 15-20, Bogotá'),
  ('9999', 'reportado', 'Cliente reportado OFAC',   'reportado@correo.com', '3009990000', 'Calle 1 # 1-10, Bogotá')
ON CONFLICT (cedula) DO NOTHING;

INSERT INTO bien (nombre, descripcion, categoria, valor_minimo, imagen_url) VALUES
  ('Camioneta Toyota Prado 2019', 'Vehículo incautado, 80.000 km',        'Vehículos', 120000000, NULL),
  ('Apartamento 70 m2 Chapinero', 'Inmueble incautado, 3 habitaciones',   'Inmuebles', 450000000, NULL),
  ('Reloj Rolex Submariner',      'Joya incautada con certificado',       'Joyas',      35000000, NULL);

-- Subastas abiertas por 30 minutos. Para cerrarlas ya:  UPDATE subasta SET fecha_cierre = NOW();
INSERT INTO subasta (id_bien, fecha_inicio, fecha_cierre, estado)
SELECT id_bien, NOW() - INTERVAL '1 hour', NOW() + INTERVAL '30 minutes', 'ABIERTA' FROM bien;
