\# StockSmart



\## Matrículas

\- 20253ds082

\- 20253ds076



\## Descripción de la solución

StockSmart es una aplicación móvil para el control de inventario, caducidades y merma de productos. Tiene tres roles:



\- \*\*Administrador:\*\* acceso completo. Gestiona productos, merma, usuarios y consulta estadísticas.

\- \*\*Encargado:\*\* agrega y consulta productos.

\- \*\*Vendedor:\*\* consulta el catálogo disponible y la merma.



La solución se compone de:



\- \*\*App Android\*\* (Kotlin y Jetpack Compose), que consume la API REST con Retrofit.

\- \*\*Backend\*\* (Spring Boot 3, Java 17), que expone la API REST, valida el token y el rol, y aplica las reglas de negocio.

\- \*\*Base de datos\*\* Oracle Cloud (Autonomous Database), que guarda usuarios, productos, mermas y fotos.



\*\*Sensores del teléfono:\*\*

\- \*\*Cámara:\*\* toma la foto del producto y escanea códigos de barras.

\- \*\*Micrófono:\*\* dicta texto y sugiere palabras al escribir.

\- \*\*Acelerómetro:\*\* agitar el teléfono actualiza los datos.



\*\*CRUD completo:\*\* productos (con foto), mermas y usuarios.



\## Estructura del repositorio

\- `ProyectoIntegrador/`: app Android (package `mx.edu.utez.proyectointegrador`).

\- `ProyectoIntegrador\\\_Backend/`: API REST en Spring Boot.



\## Instrucciones de ejecución



\### 1. Backend

1\. Descomprime la wallet de Oracle Cloud en una carpeta, por ejemplo `C:/wallets/Wallet\\\_MIBASE`.

2\. Edita `src/main/resources/application.properties` (o usa variables de entorno):

&#x20;  - `spring.datasource.url=jdbc:oracle:thin:@ALIAS\\\_tp?TNS\\\_ADMIN=C:/wallets/Wallet\\\_MIBASE`

&#x20;  - `spring.datasource.username=ADMIN`

&#x20;  - `spring.datasource.password=TU\\\_CONTRASEÑA`

3\. Ejecuta `ProyectoIntegradorApplication` (o `mvn spring-boot:run`).

4\. Al iniciar se crean las tablas automáticamente, junto con usuarios y productos de ejemplo. El servidor queda en `http://localhost:8080`.



\### 2. App Android

1\. Abre la carpeta `ProyectoIntegrador` en Android Studio y espera a que termine la sincronización de Gradle.

2\. Si usas el emulador, la app se conecta por defecto a `http://10.0.2.2:8080/`.

3\. Si usas un teléfono físico, conectado a la misma red Wi-Fi que la PC, toca el engrane de la pantalla de login y escribe `http://IP\\\_DE\\\_LA\\\_PC:8080/`.

4\. Ejecuta la app.



\### Usuarios de prueba

| Usuario | Contraseña | Rol |

|---|---|---|

| admin | admin123 | Administrador |

| encargado | encargado123 | Encargado |

| vendedor | vendedor123 | Vendedor |



\## Endpoints de la API

Todas las rutas, excepto el login y la foto, requieren el encabezado `Authorization: Bearer <token>`.



| Método | Ruta | Descripción | Roles |

|---|---|---|---|

| POST | `/api/auth/login` | Inicio de sesión, devuelve el token | Público |

| GET | `/api/productos` | Lista de productos | Todos |

| GET | `/api/productos/{id}` | Detalle de un producto | Todos |

| GET | `/api/productos/codigo/{codigo}` | Buscar por código de barras | Todos |

| GET | `/api/productos/{id}/foto` | Foto del producto | Público |

| POST | `/api/productos` | Crear producto (multipart: `producto` y `foto`) | Admin, Encargado |

| PUT | `/api/productos/{id}` | Actualizar producto | Admin |

| DELETE | `/api/productos/{id}` | Eliminar producto | Admin |

| GET | `/api/mermas` | Lista de mermas | Todos |

| POST | `/api/mermas` | Registrar merma (descuenta stock) | Admin |

| PUT | `/api/mermas/{id}` | Actualizar merma | Admin |

| DELETE | `/api/mermas/{id}` | Eliminar merma (devuelve stock) | Admin |

| GET | `/api/usuarios` | Lista de usuarios | Admin |

| POST | `/api/usuarios` | Crear usuario | Admin |

| PUT | `/api/usuarios/{id}` | Actualizar usuario | Admin |

| DELETE | `/api/usuarios/{id}` | Eliminar usuario | Admin |

