-- PC2 · psql -U postgres -f 05-crear-bd_notificaciones.sql  (misma instancia que bd_sso, base y usuario distintos)
CREATE USER notif WITH PASSWORD 'notif';
CREATE DATABASE bd_notificaciones OWNER notif;
