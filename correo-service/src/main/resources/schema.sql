-- bd_correo: un registro por correo entregado en el buzón de un destinatario
create table if not exists correo (
    id     bigint auto_increment primary key,
    para   varchar(200) not null,
    asunto varchar(500),
    cuerpo clob,
    fecha  timestamp    not null
);
