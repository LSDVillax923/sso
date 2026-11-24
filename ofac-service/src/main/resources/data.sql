-- Datos de prueba: 9999 sale RECHAZADO, 8888 sale ENPROCESO y cualquier otra cédula sale APROBADO
INSERT INTO persona_reportada (cedula, nombre, motivo, fecha_reporte)
VALUES ('9999', 'Persona reportada de prueba', 'Lista SDN', DATE '2024-01-15')
ON CONFLICT (cedula) DO NOTHING;

INSERT INTO solicitud_ofac (cedula, estado, fecha_apertura)
SELECT '8888', 'EN_REVISION', NOW()
WHERE NOT EXISTS (SELECT 1 FROM solicitud_ofac WHERE cedula = '8888');
