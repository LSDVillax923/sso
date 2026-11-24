-- PC2 · psql -U postgres -f 04-crear-bd_sen.sql  (misma instancia que bd_sso, base y usuario distintos)
CREATE USER sen WITH PASSWORD 'sen';
CREATE DATABASE bd_sen OWNER sen;
