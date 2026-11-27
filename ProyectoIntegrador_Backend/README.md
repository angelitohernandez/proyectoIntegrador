# StockSmart – Backend (Spring Boot + Oracle Cloud)

Package: `mx.edu.utez.proyectointegrador`

La app Android **no se conecta directo a Oracle**. Se conecta a este backend por HTTP (Retrofit)
y el backend es quien habla con Oracle Autonomous Database por JDBC. Por eso la wallet solo se
usa aquí, nunca dentro del APK.

```
App Android  --HTTP/JSON-->  Spring Boot  --JDBC (wallet)-->  Oracle Autonomous DB
```

## 1. Crear la base en Oracle Cloud
1. Oracle Cloud > Oracle Database > Autonomous Database > **Create Autonomous Database**.
2. Workload: *Transaction Processing* (o *JSON*). Marca **Always Free** si tu cuenta lo permite.
3. Define la contraseña del usuario `ADMIN` (guárdala).
4. En "Network access" deja *Secure access from everywhere* (para pruebas).

## 2. ¿Wallet o no? (dos opciones)
### Opción A – Con wallet (la que viene por defecto, mTLS)
1. En la base: **Database connection** > **Download wallet** > escribe una contraseña para el zip > descarga.
2. Descomprime en una carpeta, por ejemplo `C:/wallets/Wallet_MIBASE` (no la subas a Git).
3. Abre `tnsnames.ora` dentro de esa carpeta: ahí están los alias, por ejemplo `mibase_high`, `mibase_medium`, `mibase_low`, `mibase_tp`.
4. Configura la URL (usa `/` aunque estés en Windows):

   `jdbc:oracle:thin:@mibase_high?TNS_ADMIN=C:/wallets/Wallet_MIBASE`

5. Si te marca "Could not find wallet", edita `sqlnet.ora` de la wallet y pon en `DIRECTORY` la ruta real de la carpeta.

### Opción B – Sin wallet (TLS)
1. En la base: **Database connection** > **Mutual TLS (mTLS) authentication** > *Edit* > cambia a **Not required** (o "Allow TLS authentication").
2. En el mismo diálogo, en *Connection strings*, elige **TLS** y copia la cadena `(description=...)`.
3. Úsala completa: `jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=...))(connect_data=(service_name=...))(security=(ssl_server_dn_match=yes)))`
4. Requiere que el acceso de red permita tu IP (o "desde cualquier lugar").

## 3. Variables de entorno (recomendado en vez de escribir la clave en el archivo)
| Variable | Ejemplo |
|---|---|
| `DB_URL` | `jdbc:oracle:thin:@mibase_high?TNS_ADMIN=C:/wallets/Wallet_MIBASE` |
| `DB_USER` | `ADMIN` (o `stocksmart` si ejecutaste `oracle/01_crear_usuario.sql`) |
| `DB_PASSWORD` | tu contraseña |
| `TOKEN_SECRET` | una cadena larga aleatoria |

En IntelliJ: *Run > Edit Configurations > Environment variables*. También puedes editar `src/main/resources/application.properties`.

## 4. Ejecutar
```
mvn spring-boot:run
```
(o abre la carpeta en IntelliJ / Eclipse / VS Code y ejecuta `ProyectoIntegradorApplication`).
Al primer arranque Hibernate crea las tablas (`usuarios`, `productos`, `producto_fotos`, `mermas`) y
`DataInitializer` inserta usuarios y productos de ejemplo.

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | ADMIN |
| encargado | encargado123 | ENCARGADO |
| vendedor | vendedor123 | VENDEDOR |

## 5. Permisos por rol (se validan en el backend)
| Endpoint | ADMIN | ENCARGADO | VENDEDOR |
|---|---|---|---|
| `GET /api/productos`, `/{id}`, `/codigo/{codigo}` | sí | sí | sí |
| `POST /api/productos` | sí | sí | no |
| `PUT/DELETE /api/productos/{id}` | sí | no | no |
| `GET /api/mermas` | sí | sí | sí |
| `POST/PUT/DELETE /api/mermas` | sí | no | no |
| `/api/usuarios` (CRUD) | sí | no | no |
| `GET /api/productos/{id}/foto` | público (imagen) | | |

## 6. Conectar la app
- Emulador: `http://10.0.2.2:8080/` (valor por defecto).
- Teléfono físico: misma red Wi-Fi que tu PC, usa `http://IP_DE_TU_PC:8080/` (ícono de engrane en el login) y permite el puerto 8080 en el firewall.
- Para que funcione fuera de tu casa, despliega este JAR en una VM de OCI (Always Free) y usa `http://IP_PUBLICA:8080/` (abre el puerto 8080 en la Security List).
