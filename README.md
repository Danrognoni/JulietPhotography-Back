# Dennis Wanderlight Photography — Backend Spring Boot 3 & SQLite

Backend RESTful desarrollado en **Java 17+ (Spring Boot 3.3.5)** con persistencia en **SQLite**, seguridad con **Spring Security 6**, autenticación **JWT**, endpoints CMS dinámicos para edición in-situ y gestión de archivos multimedia.

---

## 🚀 Requisitos Previos

- **Java JDK 17 o superior** (Detectado en tu equipo: `C:\Users\danro\.jdks\ms-25.0.4.1`).
- **Maven 3.8+** (o el wrapper local: `.mvn/wrapper`).

---

## 🛠️ Cómo Ejecutar el Proyecto

### Terminal (PowerShell)
```powershell
# Configurar JAVA_HOME si no está en PATH:
$env:JAVA_HOME = "C:\Users\danro\.jdks\ms-25.0.4.1"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

# Compilar y ejecutar:
& "C:\Users\danro\.m2\wrapper\dists\apache-maven-3.9.9-bin\4nf9hui3q3djbarqar9g711ggc\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

El servidor REST iniciará en: **`http://localhost:8080`**.

---

## 🔐 Credenciales y Seguridad

- **Usuarios Administradores Sembrados Automáticamente**:
  - **Email**: `admin@denniswanderlight.com` (o `julietamarateo4@gmail.com`)
  - **Password**: `12345678` (cifrado con `BCrypt` en SQLite).
  - **Rol**: `ROLE_ADMIN`
- **Generación de Token**: Al realizar `POST /api/auth/login`, el backend devuelve un token JWT con vigencia de 24 horas.
- **Autorización**:
  - `GET` en `/api/photos`, `/api/albums`, `/api/site-content`, `/api/profile` y `POST /api/contact` son públicos.
  - Toda operación de modificación (`POST`, `PUT`, `DELETE`, `PATCH`) requiere el encabezado:
    ```http
    Authorization: Bearer <TU_TOKEN_JWT>
    ```

---

## 📡 Endpoints de la API

### 1. Autenticación (`/api/auth`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Autenticación con email y password. Devuelve JWT. |
| `GET` | `/api/auth/me` | Autenticado | Información del usuario con sesión activa. |

### 2. CMS y Configuración Dinámica del Sitio (`/api/site-content`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/site-content` | Público | Recupera todos los textos, imágenes y configuraciones de la web. |
| `PUT` | `/api/site-content` | `ROLE_ADMIN` | Actualiza y persiste inmediatamente cualquier texto o configuración. |
| `POST` | `/api/site-content/upload` | `ROLE_ADMIN` | Sube y actualiza una imagen de sección (hero, vignettes, story, about). |

### 3. Catálogo de Fotografías (`/api/photos`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/photos` | Público | Lista fotos. Parámetros opcionales: `?category=Tokyo+Neon+Pulse` |
| `GET` | `/api/photos/{id}` | Público | Obtiene el detalle y ficha técnica EXIF de una fotografía. |
| `POST` | `/api/photos` | `ROLE_ADMIN` | Sube una nueva fotografía con archivo físico (`file`) o JSON. |
| `PUT` | `/api/photos/{id}` | `ROLE_ADMIN` | Actualiza metadatos o reemplaza el archivo físico. |
| `DELETE` | `/api/photos/{id}` | `ROLE_ADMIN` | Elimina la foto de la BD y borra el archivo físico en el servidor. |

### 4. Mensajes de Contacto (`/api/contact`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/contact` | Público | Envía un mensaje desde el formulario de contacto público. |
| `GET` | `/api/contact` | `ROLE_ADMIN` | Lista los mensajes recibidos ordenados por fecha descendente. |
| `PATCH` | `/api/contact/{id}/read` | `ROLE_ADMIN` | Marca un mensaje como leído. |
| `DELETE` | `/api/contact/{id}` | `ROLE_ADMIN` | Elimina un mensaje de contacto. |

### 5. Archivos Multimedia (`/uploads/**`)
- `GET /uploads/photos/{filename}`: Acceso directo a imágenes almacenadas físicamente.
- `GET /uploads/site/{filename}`: Acceso directo a imágenes de secciones CMS.

---

## 💾 Persistencia en SQLite

Los datos se guardan en el archivo `julietphotography.db` ubicado en el directorio de ejecución del backend. No requiere ningún servidor externo.
