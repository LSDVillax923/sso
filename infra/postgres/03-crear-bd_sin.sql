-- PC2 · psql -U postgres -f 03-crear-bd_sin.sql  (misma instancia que bd_sso, base y usuario distintos)
CREATE USER sin WITH PASSWORD 'sin';
CREATE DATABASE bd_sin OWNER sin;
