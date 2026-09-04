# Juliet Photography - Backend Spring Boot 3 & SQLite

Backend RESTful desarrollado en **Java 17+ (Spring Boot 3.3.5)** con persistencia en **SQLite**, seguridad con **Spring Security 6**, autenticación **JWT** y gestión de archivos multimedia para el portfolio fotográfico.

---

## 🚀 Requisitos Previos

- **Java JDK 17 o superior** (Detectado en tu equipo: `C:\Users\danro\.jdks\ms-25.0.4.1`).
- **Maven 3.8+** (o ejecutarlo directamente importando la carpeta en **IntelliJ IDEA** o **VS Code**).

---

## 🛠️ Cómo Ejecutar el Proyecto

### Opción 1: Desde IntelliJ IDEA / Eclipse / VS Code
1. Abre tu IDE y selecciona **Open / Import Project**.
2. Selecciona la carpeta `backend-spring`.
3. El IDE detectará automáticamente el archivo `pom.xml` y descargará las dependencias.
4. Ejecuta la clase `JulietPhotographyApplication.java` con el botón **Run ▶**.

### Opción 2: Desde la Terminal (PowerShell)
Si tienes configurado `mvn` o el JDK:
```powershell
# Configurar JAVA_HOME si no está en PATH:
$env:JAVA_HOME = "C:\Users\danro\.jdks\ms-25.0.4.1"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

# Compilar y ejecutar:
mvn spring-boot:run
```

El servidor iniciará en: **`http://localhost:8080`**.

---

## 🔐 Credenciales y Seguridad

- **Usuario Administrador Sembrado Automáticamente**:
  - **Email**: `julietamarateo4@gmail.com`
  - **Password**: `12345678` (cifrado con `BCrypt` en la base de datos `julietphotography.db`).
  - **Rol**: `ROLE_ADMIN`
- **Generación de Token**: Al realizar `POST /api/auth/login`, el backend devuelve un token JWT con vigencia de 24 horas.
- **Autorización**:
  - Las consultas `GET` de fotos, servicios y perfil son públicas.
  - Toda operación de creación (`POST`), edición (`PUT`) o eliminación (`DELETE`) requiere el encabezado:
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

### 2. Catálogo de Fotografías (`/api/photos`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/photos` | Público | Lista todas las fotos. Parámetros opcionales: `?category=Paisajismo&q=acantilado` |
| `GET` | `/api/photos/{id}` | Público | Obtiene el detalle y ficha técnica de una fotografía. |
| `POST` | `/api/photos` | `ROLE_ADMIN` | Crea una nueva foto. Admite subida de archivo físico (`MultipartFile file`) o JSON. |
| `PUT` | `/api/photos/{id}` | `ROLE_ADMIN` | Actualiza los datos o reemplaza la foto física. |
| `DELETE` | `/api/photos/{id}` | `ROLE_ADMIN` | Elimina la foto de la base de datos y borra el archivo físico en el servidor. |

### 3. Servicios Fotográficos (`/api/services`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/services` | Público | Lista todos los paquetes de servicios profesionales. |
| `GET` | `/api/services/{id}` | Público | Detalle de un servicio. |
| `POST` | `/api/services` | `ROLE_ADMIN` | Crea un nuevo servicio (JSON o Multipart). |
| `PUT` | `/api/services/{id}` | `ROLE_ADMIN` | Actualiza un servicio. |
| `DELETE` | `/api/services/{id}` | `ROLE_ADMIN` | Elimina un servicio. |

### 4. Perfil y Biografía (`/api/profile`)
| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/profile` | Público | Datos de biografía, redes sociales y contacto. |
| `PUT` | `/api/profile` | `ROLE_ADMIN` | Actualiza los datos informativos del perfil. |
| `POST` | `/api/profile/image` | `ROLE_ADMIN` | Sube y actualiza físicamente la imagen del perfil. |

### 5. Archivos Multimedia (`/uploads/**`)
- `GET /uploads/photos/{filename}`: Acceso directo a imágenes almacenadas físicamente.
- `GET /uploads/profile/{filename}`: Acceso a la imagen de perfil.

---

## 💾 Persistencia Inmediata en SQLite

Los datos se guardan en el archivo `julietphotography.db` ubicado en el directorio de ejecución del backend. No requiere instalar ningún servidor de base de datos externo; SQLite es autocontenido y garantiza persistencia inmediata.
