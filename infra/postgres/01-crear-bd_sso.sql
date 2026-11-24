-- PC2 · ejecutar como postgres:  psql -U postgres -f 01-crear-bd_sso.sql
CREATE USER sso WITH PASSWORD 'sso';
CREATE DATABASE bd_sso OWNER sso;
-- La réplica 2 de sso-monolito corre en Docker en PC2 y entra por la IP de la máquina.
-- En postgresql.conf ponga  listen_addresses = '*'  y en pg_hba.conf agregue:
--   host  bd_sso  sso  192.168.1.0/24  scram-sha-256
--   host  bd_sso  sso  172.16.0.0/12   scram-sha-256   (red interna de Docker)
-- y reinicie el servicio de PostgreSQL.
