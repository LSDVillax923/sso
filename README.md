# SSO · Proyecto 2 · Dos monolitos, Java y .NET

Sistema de Subastas en Línea de bienes incautados. Grupo 2 · Arquitectura de Software · Javeriana 2026-30 · 2 integrantes.

El SSO es un **monolito Spring Boot 3.5 (Java 21)**, `sso-monolito`, con 2 réplicas detrás de NGINX (PC1), que sirve el SPA en Angular 17 y reparte `/api` entre ellas. YoPago es un **monolito .NET 8**, `YoPago.dll`. Los dos monolitos se comunican por el tópico RabbitMQ `integracion.pagos`. OFAC, SIN y SEN son sistemas aparte, también en Spring Boot. Los sistemas externos (Active Directory, servidor de correo y gateway SMS) vienen como simuladores con las mismas clases del diseño, para correr los 7 CU sin Gmail ni Twilio. Los nombres de clases, paquetes, componentes, máquinas y puertos son los mismos del diseño en Visual Paradigm.

## Qué hay en cada carpeta y dónde corre

| Carpeta | Artefacto | Máquina | Puerto | Cómo se arranca (fuera del IDE) |
|---|---|---|---|---|
| `sso-web-spa` | SPA Angular 17 (pantallas `Pan*`, controladores `Ctrl*`, `Proxy*` e `InterceptorJWT`); NGINX sirve su build (`dist/sso-web-spa/browser`) | se descarga de PC1 y corre en el navegador | — | `npm run build` |
| `infra/nginx` | NGINX en Docker: sirve el SPA y reenvía `/api` a las 2 réplicas (**presentación Java**) | PC1 · 192.168.1.101 · DMZ | 80 | `docker run ... nginx` |
| `yopago/YoPago` | `YoPago.dll` (**monolito .NET, negocio**) | PC2 · 192.168.1.102 | 5002 | `dotnet YoPago.dll` |
| `ofac-service` | `ofac-service.jar` | PC1 · 192.168.1.101 · Docker | 8091 | `docker run ... ofac-service` |
| `sso-monolito` | `sso-monolito.jar` réplica 1 (**monolito Java, negocio**) | PC2 · 192.168.1.102 | 8080 | `java -jar sso-monolito.jar` |
| `sso-monolito` | `sso-monolito.jar` réplica 2 | PC2 · 192.168.1.102 · Docker | 8081 | `docker run -p 8081:8080 ... sso-monolito` |
| `yopago/YoPago.Web` | `YoPago.Web.dll` (**presentación .NET**) | PC1 · 192.168.1.101 · DMZ | 5001 | `dotnet YoPago.Web.dll` |
| `sin-service` | `sin-service.jar` | PC2 · 192.168.1.102 · Docker (nodo propio) | 8092 | `docker run ... sin-service` |
| `sen-service` | `sen-service.jar` | PC2 · 192.168.1.102 · Docker (nodo propio) | 8093 | `docker run ... sen-service` |
| `notificar-service` | `notificar-service.jar` (correo y SMS) | PC2 · 192.168.1.102 | 8094 | `java -jar notificar-service.jar` |
| `ad-service` | `ad-service.jar`: **Active Directory** simulado (`ListenerLDAPS` → `SRBAutenticacionLDAP` → `IntegraDirectorio`) | PC2 · 192.168.1.102 · Docker | 389 · 636 | `docker run ... ad-service` |
| `correo-service` | `correo-service.jar`: **servidor de correo** externo simulado (`LisenerSMTP` → `SRBEntregarCorreo` → `IntegraBuzon`) | externo (cualquier PC) | SMTP 587 · buzón web 8095 | `java -jar correo-service.jar` |
| `gateway-sms` | `gateway-sms.jar`: **gateway SMS** externo simulado (`LisenerGatewaySMS` → `SRBDespacharSMS` → `IntegraOperadorMovil`) | externo (cualquier PC) | 8096 | `java -jar gateway-sms.jar` |
| `infra` | scripts de BD y usuarios del AD | PC2 | — | ver abajo |

Infraestructura:

| Máquina | Qué corre | Puerto |
|---|---|---|
| PC1 | PostgreSQL `bd_ofac` en Docker | 5433 |
| PC2 | SQL Server Express `bd_yopago` | 1433 |
| PC2 | PostgreSQL con `bd_sso` y `bd_notificaciones` (bases y usuarios distintos) | 5432 |
| PC2 | PostgreSQL `bd_sin` en Docker (contenedor propio) | 5434 |
| PC2 | PostgreSQL `bd_sen` en Docker (contenedor propio) | 5435 |
| PC2 | RabbitMQ en Docker: exchange `sso.eventos` y tópico `integracion.pagos` | 5672 · 15672 |
| PC2 | Active Directory: `ad-service` en Docker (o un contenedor OpenLDAP con el mismo LDIF; use uno de los dos) | 389 · 636 |
| externo | `bd_correo` y `bd_sms`: bases H2 en archivo de los simuladores (`./datos/`) | — |

PC1 (DMZ) tiene solo las presentaciones (NGINX y `YoPago.Web`) y PC2 (red interna) tiene los negocios (`sso-monolito` y `YoPago.dll`). Así, la presentación y el negocio quedan en máquinas distintas tanto en Java como en .NET, y desde Internet solo se entra a PC1.

Las IP son de ejemplo. Corra `ipconfig` en cada PC y cámbielas con variables de entorno al arrancar (`RABBIT_HOST`, `BD_SSO_URL`, `OFAC_URL`, `AD_URL`, `YOPAGO_URL_PAGO`) o editando cada `application.yml` / `appsettings.json`. En .NET las variables usan doble guion bajo: `Rabbit__Host`, `YoPagoApi__Url`.

## Firewall

| Desde | Hacia | Para |
|---|---|---|
| Navegador del cliente | PC1:80 | SSO (Angular y API) |
| Navegador del cliente | PC1:5001 | pantallas de YoPago |
| NGINX (PC1) | PC2:8080, PC2:8081 | réplicas del monolito |
| `YoPago.Web` (PC1) | PC2:5002 | su monolito .NET |
| `sso-monolito` (PC2) | PC1:8091 | OFAC |
| `notificar-service` (PC2) | correo:587, gateway:8096 | simuladores de correo y SMS, si corren en otra máquina |

En Windows (PowerShell como administrador):
```powershell
# PC1
New-NetFirewallRule -DisplayName SSO -Direction Inbound -Protocol TCP -LocalPort 80,5001,8091 -Action Allow
# PC2
New-NetFirewallRule -DisplayName SSO -Direction Inbound -Protocol TCP -LocalPort 8080,8081,5002,5672,15672 -Action Allow
```

## Requisitos

- JDK 21 y Maven (o IntelliJ, que trae Maven).
- Node.js 18 o 20 con npm, para compilar el SPA que sirve NGINX.
- .NET 8 SDK o Visual Studio 2022 con la carga de trabajo "Desarrollo de ASP.NET y web".
- Docker Desktop en las dos máquinas.
- PC2: SQL Server Express y PostgreSQL 16.

## Compilar

```bash
# Angular: compila el SPA para NGINX
cd sso-web-spa && npm install && npm run build && cd ..
```

```bash
# Java: desde esta carpeta compila los 8 módulos, corre sus pruebas y deja cada .jar en <módulo>/target/
mvn package
```

```bash
# .NET: o "Publicar" desde Visual Studio abriendo yopago/YoPago.sln
dotnet publish yopago/YoPago -c Release -o publicado/YoPago
dotnet publish yopago/YoPago.Web -c Release -o publicado/YoPago.Web
```

## Preparar cada máquina

**PC2 · 192.168.1.102**
```bash
psql -U postgres -f infra/postgres/01-crear-bd_sso.sql
psql -U postgres -f infra/postgres/05-crear-bd_notificaciones.sql
docker run -d --name bd-sin -p 5434:5432 -e POSTGRES_DB=bd_sin -e POSTGRES_USER=sin -e POSTGRES_PASSWORD=sin postgres:16
docker run -d --name bd-sen -p 5435:5432 -e POSTGRES_DB=bd_sen -e POSTGRES_USER=sen -e POSTGRES_PASSWORD=sen postgres:16
docker run -d --name rabbit -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=sso -e RABBITMQ_DEFAULT_PASS=sso rabbitmq:3.13-management
# Active Directory: el simulador del diseño (lee infra/ad/clientes.ldif como bd_directorio)
cd ad-service && docker build -t ad-service . && cd ..
docker run -d --name ad -p 389:389 -p 636:636 ad-service
```
En lugar de `ad-service` también sirve un OpenLDAP real con el mismo LDIF:
```bash
docker run -d --name ad -p 389:389 -p 636:636 -e LDAP_ORGANISATION=SSO -e LDAP_DOMAIN=sso.local -e LDAP_ADMIN_PASSWORD=admin osixia/openldap:1.5.0
docker cp infra/ad/clientes.ldif ad:/tmp/clientes.ldif
docker exec ad ldapadd -x -D "cn=admin,dc=sso,dc=local" -w admin -f /tmp/clientes.ldif
```
- RabbitMQ no deja entrar al usuario `guest` desde otras máquinas; por eso se crea `sso`. Consola: `http://192.168.1.102:15672`. Las colas y el tópico los declaran los monolitos al arrancar.
- La réplica 2 corre en un contenedor y entra a PostgreSQL por la IP de PC2. En `postgresql.conf` ponga `listen_addresses = '*'` y en `pg_hba.conf` agregue las líneas que vienen en `01-crear-bd_sso.sql`.

- SQL Server Express con autenticación de Windows: `YoPago.dll` crea `bd_yopago` y sus usuarios de prueba la primera vez que arranca.

**PC1 · 192.168.1.101**
```bash
docker run -d --name bd-ofac -p 5433:5432 -e POSTGRES_DB=bd_ofac -e POSTGRES_USER=ofac -e POSTGRES_PASSWORD=ofac postgres:16
```

Para preparar NGINX en PC1, ejecute desde la raíz del repositorio cuando corresponda en el orden de arranque:
```bash
docker run -d --name nginx -p 80:80 -v "${PWD}/sso-web-spa/dist/sso-web-spa/browser:/usr/share/nginx/html:ro" -v "${PWD}/infra/nginx/default.conf:/etc/nginx/conf.d/default.conf:ro" nginx:1.27
```
Si cambian las IP, edite el upstream de `infra/nginx/default.conf` y reinicie con `docker restart nginx`.

## Orden de arranque

0. **Externos (opcional):** `correo-service.jar` y `gateway-sms.jar`, antes de `notificar-service` (ver Datos de prueba).
1. **PC2:** RabbitMQ y AD (ya quedaron arriba) → `sso-monolito.jar` réplica 1 (crea las tablas de bd_sso) → correr `infra/postgres/02-semilla-bd_sso.sql` → réplica 2 en Docker (sección siguiente) → contenedores SIN y SEN (comandos de abajo) → `notificar-service.jar` → `YoPago.dll`.
2. **PC1:** contenedor OFAC (sección siguiente) → `YoPago.Web.dll` → contenedor NGINX.

En PC2, desde la raíz del repositorio:
```bash
cd sin-service && docker build -t sin-service . && cd ..
docker run -d --name sin -p 8092:8092 -e RABBIT_HOST=192.168.1.102 -e BD_SIN_URL=jdbc:postgresql://192.168.1.102:5434/bd_sin sin-service
cd sen-service && docker build -t sen-service . && cd ..
docker run -d --name sen -p 8093:8093 -e RABBIT_HOST=192.168.1.102 -e BD_SEN_URL=jdbc:postgresql://192.168.1.102:5435/bd_sen sen-service
```

Si arranca un monolito antes que el otro no pasa nada: los mensajes del tópico esperan en sus colas.

## Aporte grupal: Docker

Dos servicios en máquinas distintas que se comunican: la réplica 2 de `sso-monolito` (contenedor en PC2) llama por REST a `ofac-service` (contenedor en PC1).

```bash
# PC1
cd ofac-service && docker build -t ofac-service .
docker run -d --name ofac -p 8091:8091 -e BD_OFAC_URL=jdbc:postgresql://192.168.1.101:5433/bd_ofac ofac-service
```

```bash
# PC2
cd sso-monolito && docker build -t sso-monolito .
docker run -d --name sso2 -p 8081:8080 -e REPLICA=sso-monolito-2 -e OFAC_URL=http://192.168.1.101:8091/api/ofac/clientes/ -e RABBIT_HOST=192.168.1.102 -e BD_SSO_URL=jdbc:postgresql://192.168.1.102:5432/bd_sso -e AD_URL=ldap://192.168.1.102:389 sso-monolito
```

Prueba de comunicación entre los dos contenedores:
```bash
docker exec sso2 curl -i http://192.168.1.101:8091/api/ofac/clientes/1010
```
La cabecera `X-Atendido-Por: ofac-service` la pone `APIRestOFAC`, el boundary de entrada de OFAC.

## Cómo se hablan los dos monolitos

Tópico `integracion.pagos` (colas durables, mensajes JSON persistentes):

| Clave | Cola | Publica | Consume | Contenido |
|---|---|---|---|---|
| `pago.pendiente` | `yopago.pendientes` | `PublicadorPagos` (Java) | `LisenerComprasSSO` (.NET) | cédula, número de compra y valor |
| `pago.consulta` | `yopago.consultas` | `PublicadorPagos` (Java) | `LisenerConsultaPago` (.NET) | número de compra |
| `pago.confirmado` | `sso.pagos.confirmados` | `PublicadorPagoSSO` (.NET) | `LisenerPagoConfirmado` (Java) | cédula, número de compra y valor pagado |

Dentro del SSO, el exchange `sso.eventos` lleva `compras.ofac`, `notif.correo`, `notif.sms`, `sin.ventas`, `sen.envios` y `sso.ack`, cada una con su `.dlq`.

## Datos de prueba

| Quién | Usuario AD / clave | Cédula | Qué responde OFAC | YoPago (cédula / contraseña) |
|---|---|---|---|---|
| Ana | `ana` / `1234` | 1010 | APROBADO | 1010 / 1234 |
| Luis | `luis` / `1234` | 2020 | APROBADO | 2020 / 1234 |
| En revisión | `revision` / `1234` | 8888 | ENPROCESO (prueba los reintentos) | 8888 / 1234 |
| Reportado | `reportado` / `1234` | 9999 | RECHAZADO | — |

Correos y SMS, tres formas:

1. **Sin nada más** (por defecto): salen en el log de `notificar-service` como `[CORREO SIMULADO]` y `[SMS SIMULADO]`.
2. **Con los simuladores del diseño** (recomendado para la sustentación): arranque `correo-service` y `gateway-sms` y a `notificar-service` páselo así:
   ```bash
   java -jar correo-service.jar        # SMTP 587 (en Linux sin root: CORREO_PUERTO=2525) · buzón en :8095
   java -jar gateway-sms.jar           # API en :8096
   CORREO_SIMULADO=false SMTP_HOST=<ip del correo> SMTP_PORT=587 SMS_URL=http://<ip del gateway>:8096/api/sms java -jar notificar-service.jar
   ```
   El correo al ganador se ve en `http://<ip del correo>:8095/buzon/ana@correo.com`, con el enlace a Mis subastas. Cada SMS sale en el log de `gateway-sms` ("SMS entregado al celular …") y queda en `bd_sms` (consola en `:8096/h2-console`, URL `jdbc:h2:file:./datos/bd_sms`, usuario `sms`, clave `sms`).
3. **De verdad:** `CORREO_SIMULADO=false` con `SMTP_HOST=smtp.gmail.com`, `SMTP_USER` y `SMTP_PASS` (clave de aplicación de Gmail), y `SMS_URL` / `SMS_API_KEY` con el API del proveedor.

Sin el AD levantado se puede probar con `AD_HABILITADO=false` en `sso-monolito` (acepta cualquier clave de un cliente registrado). Úselo solo para desarrollo.

## Probar el flujo completo

**Desde el navegador** (así lo usa el cliente): abra `http://192.168.1.101`, ingrese con `ana` / `1234` (CU1), busque y agregue bienes al carrito (CU2), pulse Ofertar y escriba los valores (CU3). Cuando cierre la subasta (CU4) entre con el enlace del correo a Mis subastas y pulse Iniciar compra (CU5); la pantalla de la compra muestra el estado y el total con impuestos. Pague en YoPago (CU6) con el enlace del SMS a `http://192.168.1.101:5001` y pulse Actualizar en la compra para verla `TERMINADA` (CU7).

**Desde Git Bash**, llamando el mismo API que usa el SPA:

```bash
API=http://192.168.1.101/api
TOKEN=$(curl -s -X POST $API/auth/login -H "Content-Type: application/json" -d '{"usuario":"ana","clave":"1234"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')

curl -s "$API/catalogo?filtro=" -H "Authorization: Bearer $TOKEN"                       # CU2 buscar
curl -s -X POST $API/carrito/1 -H "Authorization: Bearer $TOKEN"                         # CU2 carrito
curl -s -X POST $API/ofertas -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
     -d '{"items":[{"idSubasta":1,"valor":125000000}]}'                                    # CU3 ofertar
```
CU4: cierre la subasta con `UPDATE subasta SET fecha_cierre = NOW() WHERE id_subasta = 1;` y espere un minuto. Llega el correo al ganador.
```bash
curl -s $API/ofertas/ganadoras -H "Authorization: Bearer $TOKEN"                        # idOferta ganadora
curl -s -X POST $API/compras -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"idOferta":1}'   # CU5 (202)
```
CU5 publica `pago.pendiente`; en la consola de RabbitMQ se ve el mensaje pasar por `yopago.pendientes`. CU6: abra `http://192.168.1.101:5001`, ingrese con 1010 / 1234 y pague. YoPago publica `pago.confirmado`. CU7: SIN y SEN confirman y la compra queda `TERMINADA`:
```bash
curl -s $API/compras/1 -H "Authorization: Bearer $TOKEN"
```

Para ver el balanceo, repita una petición con `curl -i`: la cabecera `X-Atendido-Por` dice qué réplica respondió (`sso-monolito-1` o `sso-monolito-2`), porque la pone `APIRestSSO`, el boundary de entrada del monolito. Si detiene una réplica (`docker stop sso2`), NGINX sigue respondiendo con la otra.

## HTTPS y LDAPS

El diseño usa HTTPS en la DMZ y LDAPS al AD. Por defecto el código arranca en HTTP/LDAP para que se pueda probar sin certificados.

- NGINX escucha en el puerto 80 como en clase (L10 s8); para HTTPS se agrega `listen 443 ssl` con su certificado en `infra/nginx/default.conf`.
- LDAPS: `ad-service` genera su certificado (`ad-ldaps.p12`, clave `AD_KEYSTORE_CLAVE`, por defecto `cambiar`) la primera vez que arranca. Expórtelo e impórtelo en el Java del monolito y arranque con `AD_URL=ldaps://192.168.1.102:636`:
  ```bash
  keytool -exportcert -rfc -alias ad -keystore ad-ldaps.p12 -storepass cambiar -file ad.crt
  keytool -importcert -noprompt -alias ad-sso -file ad.crt -cacerts -storepass changeit
  ```

## Cómo se cumplen las reglas del enunciado

- **Pantalla → controlador → proxy → WS:** en el SPA cada pantalla `Pan*` (componente Angular) le pasa el evento a su `Ctrl*`, que llama al `Proxy*`; el proxy va a NGINX → `APIRestSSO` → `WS*`, e `InterceptorJWT` le pone el token. El `Ctrl*` le devuelve el resultado a la pantalla (`mostrarBienes`, `mostrarItems`, `mostrarEstado`…).
- **Sin lógica de negocio en el front:** NGINX sirve el SPA y balancea las peticiones. El SPA muestra las pantallas. `YoPago.Web` muestra las pantallas y llama a `YoPago.dll`. En .NET, cada pantalla (vista Razor en `Views/Pan*`) tiene su controlador `Ctrl*` que usa `ProxyYoPagoAPI`, y el proxy llega a los `WS` de `YoPago.dll`. Fechas, valor mínimo, ganador, OFAC, impuestos y pago se validan en los `Srv*`.
- **Entrada a cada servidor:** la petición entra por un boundary (`APIRestSSO` en `sso-monolito`, `APIRestYP` en `YoPago.dll`, `APIRestOFAC` en OFAC) que la marca y la entrega al `WS`, que es un controlador.
- **Presentación y negocio en máquinas separadas:** Java en PC1 (NGINX) y PC2 (`sso-monolito`). .NET en PC1 (`YoPago.Web`) y PC2 (`YoPago.dll`).
- **Balanceo del servicio de compra:** NGINX reparte `/api/` con `least_conn` entre las 2 réplicas (upstream en `infra/nginx/default.conf`). Las réplicas no guardan sesión (JWT), compiten por las colas y los timers usan ShedLock (tabla `shedlock` en `schema.sql`), así que cada timer corre en una sola réplica.
- **Caídas de red y OFAC:** timeout de 5 s y 3 intentos (`IntegraOFAC`), `TimerReintentoOFAC` cada 5 min, aviso por SMS a los 6 intentos; `TimerVerificarPago` publica `pago.consulta` si no llega la confirmación; `TimerAckPendiente` si SIN o SEN no confirman; `TimerReintentoNotificacion` en YoPago si RabbitMQ estaba caído.
- **Nodos de SIN y SEN:** cada servicio corre en su propio contenedor (nodo) con su propia base de datos en otro contenedor.
- **BD separadas:** bd_sso, bd_yopago, bd_ofac, bd_sin, bd_sen y bd_notificaciones. Ningún sistema se conecta a la BD de otro.
- **A cada BD solo se llega por un integrador:** en `sso-monolito` los `Srv` usan `IntegraBDCliente`, `IntegraBDSubasta`, `IntegraBDCarrito`, `IntegraBDOferta`, `IntegraBDCompra`, `IntegraBDPago` e `IntegraBDAckCompra` (paquete `dominio.integra`); OFAC usa `IntegraBDOFAC`; YoPago usa `IntegraBDUsuarioYP`, `IntegraBDCompraPendiente` e `IntegraBDPagoYP` (EF Core). Los repositorios y entidades JPA quedan dentro de cada integrador y no salen en el despliegue.
- **SIN, SEN y notificar-service:** solo tienen `Lisener` → `SRB` → `Integra*`. A su base llegan por `IntegraBDInventario`, `IntegraBDEnvios` e `IntegraBDNotificaciones` y el ack a SSO sale por `IntegraAckSIN` / `IntegraAckSEN`.
- **Sistemas externos con su detalle:** AD, correo y gateway SMS siguen el mismo patrón entrada → SRB → integrador → BD: `ListenerLDAPS` → `SRBAutenticacionLDAP` → `IntegraDirectorio` → bd_directorio; `LisenerSMTP` → `SRBEntregarCorreo` → `IntegraBuzon` → bd_correo; `LisenerGatewaySMS` → `SRBDespacharSMS` (3 reintentos) → `IntegraOperadorMovil` → bd_sms.
- **Web aislada:** PC1 es la DMZ; solo NGINX y `YoPago.Web` reciben tráfico de Internet y todo el negocio queda en PC2.

## Relación con el diseño

El diseño (`SSO-Proyecto2-Grupo2.vpp`, 212 clases) y este código tienen las mismas clases y los mismos métodos: las 212 están programadas con los nombres del diagrama. `sso-monolito` va en 10 paquetes (`entrada`, `seguridad`, `catalogo`, `ofertas`, `subastas`, `compras`, `pagos`, `distribucion`, `notificacion`, `dominio`) y `YoPago.dll` en 5 (`Entrada`, `Autenticacion`, `Compras`, `Pagos`, `Datos`). Los `WS` son controladores y cada servidor recibe por su boundary `APIRest`. Detalles:

- Las tres pantallas de YoPago (`PanLoginYoPago`, `PanComprasPendientes`, `PanPagarYoPago`) son vistas Razor en `yopago/YoPago.Web/Views/<Pan…>/`, no archivos `.cs`.
- En el código hay, además, lo que cualquier aplicación necesita para arrancar y que el diagrama no dibuja: `AdApplication`, `CorreoApplication`, `GatewaySmsApplication` y el componente raíz `SSOWebSPA` (el nombre del componente del diagrama).
- `findById` y `save` de los repositorios los hereda Spring Data (`JpaRepository`).

En los diagramas los pasos de cada historia van 1, 2, 3 en el SSO y los demás sistemas, y a, b, c dentro de YoPago.

## Pruebas

`mvn package` corre las pruebas de los simuladores, que usan los mismos clientes que el SSO:

- `ad-service`: `LdapTemplate.authenticate` igual que `IntegraAD` (clave buena, clave mala y usuario inexistente) y bind por LDAPS.
- `correo-service`: envío con JavaMail como `IntegraSMTP` (AUTH, HTML en UTF-8) y lectura del buzón.
- `gateway-sms`: `POST {to, message}` como `IntegraGatewaySMS`, entrega, 3 reintentos con un celular inválido y API key equivocada.

## Lo que falta

- Correr los 7 CU de punta a punta en las dos máquinas con PostgreSQL, SQL Server y RabbitMQ reales. YoPago ya compila (0 errores, 0 advertencias con el SDK 8.0.427), `YoPago.Web` publicado sirve sus pantallas y `YoPago.dll` arranca hasta conectarse a la BD.
