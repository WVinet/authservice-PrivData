# Privdata - AuthService

Microservicio de autenticación y autorización para el proyecto **Privdata**, una plataforma SaaS orientada al cumplimiento de normativas de protección de datos.

Este servicio es el encargado de gestionar la identidad de los usuarios dentro del sistema, incluyendo registro, autenticación y control de acceso basado en roles.

---

##Responsabilidad del servicio

El AuthService centraliza toda la lógica relacionada a seguridad:

- Registro de usuarios
- Inicio de sesión (login)
- Encriptación de contraseñas
- Generación y validación de tokens (JWT)
- Control de acceso basado en roles
- Exposición de información del usuario autenticado (`/auth/me`)

Este servicio será consumido por otros microservicios del sistema para validar identidad y permisos.

---

##Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- PostgreSQL
- JPA / Hibernate
- Maven
- Lombok
- Docker(aun no)

---

##Estructura del proyecto

El proyecto sigue una estructura modular para mantener separación de responsabilidades:

- `controller/`: Controladores REST.
- `service/`: Lógica de negocio de la aplicación.
- `repository/`: Interfaces de acceso a datos (Spring Data JPA).
- `model/`: Entidades de dominio mapeadas a base de datos.
- `dto/`: Objetos de transferencia de datos para Request y Response.
- `security/`: Configuración y filtros de Spring Security y JWT.
- `shared/`: Clases genéricas y compartidas (ej. `ApiResponseDTO`).

---

## Modelo de Datos Principal

El AuthService utiliza un modelo de datos relacional para gestionar usuarios, roles, permisos y autenticación. A continuación se describen las entidades principales:

### User
- **id** (UUID): Identificador único
- **personId** (UUID): ID de la persona asociada
- **organizationId** (UUID): ID de la organización
- **email** (String): Correo electrónico único
- **passwordHash** (String): Contraseña encriptada
- **failedLoginAttempts** (Integer): Intentos fallidos de login
- **status** (UserStatus): Estado del usuario (ej. ACTIVE, INACTIVE)
- **isActive** (boolean): Si el usuario está activo
- **lockedUntil** (LocalDateTime): Fecha/hora hasta la que está bloqueado
- **passwordChangedAt** (LocalDateTime): Último cambio de contraseña
- **lastLoginAt** (LocalDateTime): Último login
- **Timestamps**: createdAt, updatedAt

### Role
- **id** (UUID): Identificador único
- **name** (String): Nombre único del rol
- **description** (String): Descripción
- **isActive** (boolean): Si el rol está activo
- **Timestamps**: createdAt, updatedAt

### Permission
- **id** (UUID): Identificador único
- **module** (String): Módulo funcional
- **action** (String): Acción permitida
- **description** (String): Descripción
- **isActive** (boolean): Si el permiso está activo
- **Timestamps**: createdAt, updatedAt

### UserRole
- **id** (UUID): Identificador único
- **user** (User): Usuario asignado
- **role** (Role): Rol asignado
- **assignedBy** (UUID): Quién asignó el rol
- **active** (boolean): Si la asignación está activa
- **assignedAt** (LocalDateTime): Fecha de asignación
- **expiresAt** (LocalDateTime): Fecha de expiración

### RolePermissions
- **id** (UUID): Identificador único
- **role** (Role): Rol asociado
- **permission** (Permission): Permiso asociado
- **isActive** (boolean): Si la relación está activa
- **createdAt** (LocalDateTime): Fecha de creación

### RefreshToken
- **id** (UUID): Identificador único
- **user** (User): Usuario asociado
- **token** (String): Token de refresco
- **expiresAt** (LocalDateTime): Expiración
- **revokedAt** (LocalDateTime): Revocación
- **createdAt** (LocalDateTime): Creación
- **updatedAt** (LocalDateTime): Actualización

### SecurityUser
- Implementa `UserDetails` de Spring Security para autenticación
- Contiene: id, email, password, active, authorities (roles/permisos)

---

### Función de cada entidad

- **User**: Representa a los usuarios del sistema. Almacena información de autenticación, estado, organización y persona asociada. Es el sujeto principal de autenticación y autorización.

- **Role**: Define los roles que agrupan permisos y determinan el nivel de acceso de los usuarios (ej: ADMIN, USER, SUPER_ADMIN, COMPANY). Un usuario puede tener varios roles.

- **Permission**: Representa permisos granulares sobre acciones específicas en módulos del sistema (ej: ARCO_VIEW, USER_CREATE). Los roles se componen de permisos.

- **UserRole**: Relaciona usuarios con roles, permitiendo asignar múltiples roles a un usuario y controlar vigencia/asignador de cada relación.

- **RolePermissions**: Relaciona roles con permisos, permitiendo que cada rol tenga múltiples permisos y cada permiso pertenezca a varios roles.

- **RefreshToken**: Gestiona los tokens de refresco para sesiones de usuario, permitiendo renovar el JWT sin reautenticación y controlar revocación/expiración.

- **SecurityUser**: Implementación de Spring Security para representar al usuario autenticado en el contexto de seguridad, incluyendo roles y permisos.

---

### Relaciones principales

- Un **User** puede tener varios **UserRole** (muchos a muchos con **Role**)
- Un **Role** puede tener varios **RolePermissions** (muchos a muchos con **Permission**)
- Un **User** puede tener varios **RefreshToken**

---

## API Endpoints

Las respuestas de los endpoints devuelven una estructura estandarizada basada en `ApiResponseDTO`:

```json
{
  "success": true,
  "message": "Mensaje sobre la operación",
  "data": { ... } // Datos devueltos (opcional)
}
```

### Autenticación y Gestión de Usuarios (`/auth`)

#### 1. Registro de Usuario
- **Endpoint:** `POST /auth/register`
- **Descripción:** Registra un nuevo usuario vinculado a una organización y persona específica.
- **Body (JSON):**
  ```json
  {
    "email": "usuario@ejemplo.com",
    "password": "mi_password",
    "organizationId": "UUID_de_la_organizacion",
    "personId": "UUID_de_la_persona"
  }
  ```

#### 2. Iniciar Sesión (Login)
- **Endpoint:** `POST /auth/login`
- **Descripción:** Autentica a un usuario en el sistema.
- **Body (JSON):**
  ```json
  {
    "email": "usuario@ejemplo.com",
    "password": "mi_password"
  }
  ```

#### 3. Obtener Perfil Actual
- **Endpoint:** `GET /auth/me`
- **Descripción:** Retorna los datos del usuario autenticado.
- **Headers Requeridos:** `Authorization: Bearer <tu_token_jwt>`

#### 4. Asignar Rol a un Usuario
- **Endpoint:** `POST /auth/users/{userId}/roles`
- **Descripción:** Asigna un rol a un usuario determinado.
- **Requisitos:** Requiere que quien hace la petición tenga el permiso `ROLE_ASSIGN`.
- **Headers Requeridos:** `Authorization: Bearer <tu_token_jwt>`
- **Path Variable:** `userId` (UUID del usuario).
- **Body (JSON):**
  ```json
  {
    "roleName": "NOMBRE_DEL_ROL"
  }
  ```

### Pruebas de Autorización y Roles

Endpoints de prueba para validar que el control de acceso funcione correctamente según roles y permisos asignados.

#### Por Roles de Sistema
- `GET /user/test`: Valida acceso de nivel USER.
- `GET /admin/test`: Valida acceso de nivel ADMIN.
- `GET /superadmin/test`: Valida acceso de nivel SUPER_ADMIN.
- `GET /company/test`: Valida acceso de nivel COMPANY.

#### Por Permisos (Authorities - `/test`)
- `GET /test/arco-view`: Valida que se posea la autoridad `ARCO_VIEW`.
- `GET /test/arco-create`: Valida que se posea la autoridad `ARCO_CREATE`.
- `GET /test/user-create`: Valida que se posea la autoridad `USER_CREATE`.

---

## Listado de Endpoints y Funcionalidades

| Método | Endpoint                       | Funcionalidad                                                      |
|--------|---------------------------------|--------------------------------------------------------------------|
| POST   | /auth/register                 | Registrar un nuevo usuario                                        |
| POST   | /auth/login                    | Autenticar usuario y obtener token JWT                             |
| GET    | /auth/me                       | Obtener datos del usuario autenticado                              |
| POST   | /auth/users/{userId}/roles     | Asignar un rol a un usuario (requiere permiso ROLE_ASSIGN)         |
| GET    | /user/test                     | Prueba de acceso para rol USER                                     |
| GET    | /admin/test                    | Prueba de acceso para rol ADMIN                                    |
| GET    | /superadmin/test               | Prueba de acceso para rol SUPER_ADMIN                              |
| GET    | /company/test                  | Prueba de acceso para rol COMPANY                                  |
| GET    | /test/arco-view                | Prueba de permiso ARCO_VIEW                                        |
| GET    | /test/arco-create              | Prueba de permiso ARCO_CREATE                                      |
| GET    | /test/user-create              | Prueba de permiso USER_CREATE                                      |

---

## Data Transfer Objects (DTOs)

### Request DTOs

- **LoginRequestDTO**
  - `email` (String, requerido): Correo electrónico del usuario
  - `password` (String, requerido): Contraseña

- **RegisterRequestDTO**
  - `email` (String, requerido): Correo electrónico
  - `password` (String, requerido): Contraseña
  - `organizationId` (UUID, requerido): ID de la organización
  - `personId` (UUID, requerido): ID de la persona

- **AssignRoleRequestDTO**
  - `roleName` (String, requerido): Nombre del rol a asignar

### Response DTOs

- **LoginResponseDTO**
  - `token` (String): Token JWT de acceso

- **RegisterResponseDTO**
  - `id` (UUID): ID del usuario
  - `email` (String): Correo electrónico
  - `status` (UserStatus): Estado del usuario
  - `organizationId` (UUID): ID de la organización
  - `personId` (UUID): ID de la persona

- **MeResponseDTO**
  - `id` (UUID): ID del usuario
  - `email` (String): Correo electrónico
  - `organizationId` (UUID): ID de la organización
  - `personId` (UUID): ID de la persona
  - `status` (UserStatus): Estado del usuario
  - `authorities` (List<String>): Lista de permisos y roles asignados

---

## Ejemplo de Respuestas

Todas las respuestas siguen el formato:

```json
{
  "success": true,
  "message": "Mensaje sobre la operación",
  "data": { ... } // Datos específicos de la operación
}
```

### Ejemplo de respuesta exitosa de login
```json
{
  "success": true,
  "message": "Inicio de sesión correcto",
  "data": {
    "token": "<jwt_token>"
  }
}
```

### Ejemplo de error
```json
{
  "success": false,
  "message": "Credenciales inválidas",
  "data": null
}
```

---

## Seguridad

- Todos los endpoints protegidos requieren autenticación JWT vía header `Authorization: Bearer <token>`.
- El control de acceso se realiza por roles y permisos (authorities).
- Los endpoints `/test/*` y `/user/test`, `/admin/test`, etc. permiten validar la configuración de roles y permisos.

---

## Notas adicionales

- Para más detalles sobre los roles, permisos y estructura de los tokens, revisar la configuración en `src/main/java/com/privdata/authservice/security/`.
- El microservicio está preparado para integrarse con otros servicios mediante validación de tokens JWT y consulta de roles/permisos.
- Para pruebas locales, consultar el archivo `application.properties` y la base de datos de ejemplo en `src/main/resources/db/auth_db`.
