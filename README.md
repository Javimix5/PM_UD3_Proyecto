# Gestión de Usuarios (Híbrido)

Este proyecto es una aplicación Android que gestiona una lista de usuarios con soporte híbrido local/remoto.

Usa las clases de Android (Repository, ViewModel, Jetpack Compose) y librerías (Retrofit, Room, Coil).

La app mantiene una base de datos local (Room) y sincroniza altas, modificaciones y borrados con una API externa (MockAPI).
Incluye un detector de sacudida (shake) que lanza la sincronización en caso de que se active dicho estado.

Contenido de este README
- Descripción del proyecto
- Principales características
- Estructura y archivos importantes
- Modelo de datos y mapeos (Local <-> Remote)
- Requisitos y herramientas
- Cómo probar las funciones principales (CRUD, sincronización, shake)
---

## Descripción

La app permite crear, editar y borrar usuarios localmente. 
Los cambios locales se marcan como pendientes y se suben al servidor remoto cuando se solicita una sincronización de dos formas distintas: 

1. De forma manual con el icono de sincronizar.
2. Usando el sistema de sacudida, que activa la sincronización automática.

Esta sincronización se hace en dos fases:

1. Subida de cambios pendientes (altas, modificaciones, borrados) de LOCAL -> REMOTE.
2. Descarga completa del estado del servidor REMOTE -> LOCAL (reemplazo con OnConflictStrategy.REPLACE).

La comunicación con la API usa Retrofit y DTOs serializables (`RemoteUser`), mientras que la persistencia local usa la entidad Room `User`. El repositorio convierte entre ambas representaciones con funciones de extensión `toRemote()` / `toLocal()`.


## Principales características
- CRUD de usuarios (crear, editar, borrar) usando Room
- Sincronización híbrida (uploadPendingChanges + syncFromServer)
- Detección de sacudida (shake) para disparar sincronización
- Imágenes de usuario con Coil
- Arquitectura: Repositorio, ViewModel, UI (Jetpack Compose)


## Estructura y archivos importantes
Rutas relevantes dentro de `app/src/main/java/com/example/gestionusuarioshibrido/`:

- `data/`
  - `UserRepository.kt` — Implementación `DefaultUserRepository` con lógica de sincronización.
  - `AppContainer.kt` — Construcción de Retrofit y Room (baseUrl, Json config).
  - `TestUsers.kt` — Usuarios de prueba usados por la app.
  - `remote/RemoteUser.kt` — DTO serializable usado por Retrofit.
  - `local/User.kt` — Entidad Room + `toRemote()`.
  - `local/UserDao.kt` — DAO con consultas para usuarios.
  - `local/UserDatabase.kt` — Base de datos Room.

- `network/MockApiService.kt` — Interfaz Retrofit: expone `getAllUsers()`, `createUser()`, `updateUser()`, `deleteUser()` (usa `RemoteUser`).

- `viewmodel/UserViewModel.kt` — Orquesta llamadas al repositorio, expone `users` y eventos y maneja `setupShakeListener()` y `sync()`.

- `sensors/`
  - `SensorShakeDetector.kt` — Lógica para detectar sacudida mediante acelerómetro.
  - `ShakeUserCoordinator.kt` — Coordina detector + ViewModel (muestra Toast y llama `userViewModel.sync()`).

- `ui/`
  - `views/`
    - `AppNavigation.kt` - Navegación Compose entre pantallas.
    - `UserListScreen.kt` - Pantalla principal con lista de usuarios y acciones.
    - `UserFormScreen.kt` - Pantalla para crear/editar usuario en formato de formulario.
  - `components/UserCard.kt` — Componente de tarjeta de usuario.

- `GestionUsuariosApplication.kt` — Inicializa `AppDataContainer`.


## Modelo de datos y mapeos
- Local (Room): `User` (id: String, firstName, lastName, email, age, userName, positionTitle, imagen, pendingSync, pendingDelete)
- Remoto (API): `RemoteUser` (@Serializable) con campos: `id: String?`, `firstName`, `lastName`, `email`, `age`, `userName`, `positionTitle`, `imagen`.

Mapeos:
- `User.toRemote()` — Convierte `User` a `RemoteUser`
  - Si `User.id` empieza por `local_`, `RemoteUser.id` queda `null` para que el servidor asigne id.
- `RemoteUser.toLocal()` — Convierte `RemoteUser` a `User` asignando `id` remoto (o generando `local_` si faltara).


## Requisitos y herramientas
- JDK 11+ (según configuración del proyecto)
- Android Studio (Arctic o versión compatible con Kotlin/Compose del proyecto)
- Gradle (wrapper incluido)
- Dispositivo o emulador Android con Internet
- adb (Android Platform Tools)


## Probar funciones principales (pasos)
1. Crear usuario
   - Pulsa el botón +, completa nombre/apellidos/email y pulsa Crear.
   - Resultado esperado: el usuario aparece en la lista con id `local_<timestamp>`.

2. Editar usuario
   - Pulsa el icono editar en la tarjeta, cambia datos y guarda.
   - Resultado: datos actualizados localmente.

3. Borrar usuario
   - Pulsa el icono eliminar; el usuario se marca para borrado localmente.

4. Añadir Test User (desde AppBar)
   - Pulsa el icono de añadir test user; la app añade usuarios de ejemplo definidos en `TestUsers.kt`.

5. Sincronización manual
   - Pulsa el icono Sync (barra superior).
   - Resultado esperado: La app actualizará los datos del MockAPI en función de los cambios realizados (altas/actualizaciones/borrados pendientes).
     - Seguidamente descarga la lista completa del servidor.
     - Se mostrará un SnackBar con la información que indica que se está sincronizando, seguido de la sincronización completa.

6. Sincronización por Shake
   - Agita el dispositivo; la app muestra un Toast y ejecuta  la sincronización `sync()` del `UserViewModel`.
   - En emulador: usa el panel de sensores para emular aceleración`, ajustando el movimiento en el eje Y.
   - Resultado: Se mostrará un SnackBar con la información que indica que se está sincronizando, seguido de la sincronización completa. 
     - La app actualizará los datos del MockAPI en función de los cambios realizados.


Mensajes clave que indican correcto funcionamiento:
- "Sincronización iniciada" y "Sincronización finalizada" (usando la función `sync()` desde `UserViewModel`).
- "Sacudida detectada: iniciando sync() in ViewModel" (desde `ShakeUserCoordinator`).
- Mensajes de `DefaultUserRepository` con resumen "Subidos: X, Borrados: Y" y "Descargados: A nuevos, B actualizados".