-- bd_sms: cada SMS que recibió el proveedor y si quedó entregado al celular
create table if not exists sms (
    id     bigint auto_increment primary key,
    numero varchar(20)   not null,
    texto  varchar(1000) not null,
    estado varchar(20)   not null,
    fecha  timestamp     not null
);
