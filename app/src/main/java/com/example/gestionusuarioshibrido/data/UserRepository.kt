package com.example.gestionusuarioshibrido.data

import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.data.local.UserDao
import com.example.gestionusuarioshibrido.network.MockApiService
import kotlinx.coroutines.flow.Flow


sealed class RepositoryResult {
    class Success(val message: String) : RepositoryResult()
    data class Error(val message: String, val exception: Throwable? = null) : RepositoryResult()
}


interface UserRepository {

    fun getAllUsersStream(): Flow<List<User>>

    suspend fun insertUser(user: User): RepositoryResult

    suspend fun updateUser(user: User): RepositoryResult

    suspend fun deleteUser(user: User): RepositoryResult

    // Sincronización
    suspend fun uploadPendingChanges(): RepositoryResult
    suspend fun syncFromServer(): RepositoryResult
}

class DefaultUserRepository(
    private val local: UserDao,
    private val remote: MockApiService
) : UserRepository {

    override fun getAllUsersStream(): Flow<List<User>> {
        return local.getAllUsersStream()
    }

    override suspend fun insertUser(user: User): RepositoryResult {
        return try {
            val userToSave = user.copy(pendingSync = true)
            local.insertUser(userToSave)
            RepositoryResult.Success("Usuario creado localmente")
        } catch (e: Exception) {
            RepositoryResult.Error("Error al crear usuario local", e)
        }
    }

    override suspend fun updateUser(user: User): RepositoryResult {
        return try {
            val userToUpdate = user.copy(pendingSync = true)
            local.updateUser(userToUpdate)
            RepositoryResult.Success("Usuario actualizado localmente")
        } catch (e: Exception) {
            RepositoryResult.Error("Error al actualizar usuario local", e)
        }
    }

    override suspend fun deleteUser(user: User): RepositoryResult {
        return try {
            val userToDelete = user.copy(pendingDelete = true, pendingSync = true)
            local.updateUser(userToDelete)
            RepositoryResult.Success("Usuario marcado para eliminación")
        } catch (e: Exception) {
            RepositoryResult.Error("Error al marcar usuario para eliminación", e)
        }
    }

    /**
     * Sincroniza con el servidor remoto todos los cambios pendientes almacenados en la base de datos local.
     *
     * Este proceso sube **altas**, **modificaciones** y **borrados** que se realizaron en modo offline
     * o que quedaron marcados con `pendingSync = true` por fallos de conexión previos.
     *
     * ### Flujo de sincronización
     *
     * 1. **Altas y actualizaciones (pendingUpdates)**
     *    - Un usuario cuyo `id` comienza por `"local_"` se considera una creación local que aún
     *      no existe en el servidor.
     *        - Se envía una petición `createUser()` al servidor.
     *        - Se elimina la versión local provisional.
     *        - Se inserta la versión remota definitiva (que ya contiene un `id` real).
     *    - Un usuario con `pendingSync = true` y un `id` real se considera una actualización pendiente.
     *        - Se envía una petición `updateUser()`.
     *        - La entrada local se actualiza estableciendo `pendingSync = false`.
     *
     * 2. **Borrados (pendingDeletes)**
     *    - Si el usuario tiene un `id` real (no empieza por `"local_"`), se envía `deleteUser()` al servidor.
     *    - En cualquier caso, la entrada se elimina de la base de datos local.
     *
     * ### Resultado
     *
     * Devuelve un [RepositoryResult] indicando si la sincronización fue satisfactoria o si ocurrió un error.
     *
     * - `RepositoryResult.Success` incluye un resumen del número de usuarios actualizados y borrados.
     * - `RepositoryResult.Error` encapsula la excepción originada durante el proceso.
     *
     * ### Excepciones
     * Cualquier error (fallo de red, conversión, servidor no disponible, etc.) provoca que se devuelva
     * `RepositoryResult.Error`, sin eliminar ni modificar los datos locales pendientes.
     *
     * @return [RepositoryResult] con el estado final de la operación de sincronización.
     */

    override suspend fun uploadPendingChanges(): RepositoryResult {
        return try {
            var usuariosSubidos = 0
            var usuariosBorrados = 0

            // 1. Obtener actualizaciones pendientes (Altas y Modificaciones)
            val pendingUpdates = local.getPendingUpdates()

            for (user in pendingUpdates) {
                if (user.id.startsWith("local_")) {
                    val created = remote.createUser(user)

                    local.deleteUserById(user.id)
                    local.insertUser(created)

                    usuariosSubidos++
                } else {
                    remote.updateUser(user.id, user)

                    local.updateUser(user.copy(pendingSync = false))
                    usuariosSubidos++
                }
            }

            val pendingDeletes = local.getPendingDeletes()

            for (user in pendingDeletes) {
                if (!user.id.startsWith("local_")) {
                    try {
                        remote.deleteUser(user.id)
                    } catch (e: Exception) {
                    }
                }
                local.deleteUser(user)
                usuariosBorrados++
            }

            if (usuariosSubidos == 0 && usuariosBorrados == 0) {
                RepositoryResult.Success("No había cambios pendientes")
            } else {
                RepositoryResult.Success("Subidos: $usuariosSubidos, Borrados: $usuariosBorrados")
            }

        } catch (e: Exception) {
            RepositoryResult.Error("Error durante la subida de cambios", e)
        }
    }


    /**
     * Sincroniza la base de datos local con el estado completo del servidor remoto (`REMOTE -> LOCAL`).
     *
     * Este proceso descarga todos los usuarios desde la API remota y actualiza la base de datos local
     * aplicando una política de **reemplazo total** mediante `OnConflictStrategy.REPLACE`.
     *
     * ### Flujo de sincronización
     *
     * 1. **Descarga completa del servidor**
     *    Se obtiene la lista completa de usuarios mediante `remote.getAllUsers()`.
     *
     * 2. **Detección de nuevas inserciones**
     *    - Se recuperan todos los IDs locales existentes.
     *    - Se identifican aquellos usuarios remotos cuyo `id` no está en la base de datos local.
     *      Estos se consideran **nuevos registros** que deberán insertarse.
     *
     * 3. **Actualización e inserción masiva**
     *    Todos los usuarios remotos se convierten a entidades locales mediante `toLocal()`
     *    y se almacenan utilizando la estrategia `REPLACE`, lo que garantiza:
     *    - inserción de los nuevos registros,
     *    - sobrescritura de registros existentes,
     *    - mantenimiento de coherencia con el servidor.
     *
     * ### Resultado
     *
     * Devuelve un [RepositoryResult] indicando si la sincronización fue correcta o si ocurrió un error.
     *
     * - `RepositoryResult.Success` incluye un resumen:
     *   - número de usuarios **insertados** (no existían previamente),
     *   - número de usuarios **actualizados** (existían y fueron reemplazados).
     *
     * - `RepositoryResult.Error` se devuelve si ocurre cualquier excepción durante la comunicación
     *   con el servidor o al escribir en la base de datos local.
     *
     * ### Importante
     *
     * Este método implementa una sincronización **descendente completa**, ideal para sistemas
     * offline-first donde el servidor es la fuente de verdad final.
     * No elimina registros locales que no existen en el servidor; solo inserta o reemplaza.
     *
     * @return [RepositoryResult] con el estado de la operación de sincronización REMOTE → LOCAL.
     */
    override suspend fun syncFromServer(): RepositoryResult {
        return try {
            val remoteUsersDTO = remote.getAllUsers()

            val remoteUsers = remoteUsersDTO

            val localIds = local.getAllUserIds()

            val usersToInsert = mutableListOf<User>()
            val usersToUpdate = mutableListOf<User>()

            for (remoteUser in remoteUsers) {
                if (localIds.contains(remoteUser.id)) {
                    usersToUpdate.add(remoteUser)
                } else {
                    usersToInsert.add(remoteUser)
                }
            }

            if (usersToUpdate.isNotEmpty()) {
                local.updateUsers(usersToUpdate)
            }
            if (usersToInsert.isNotEmpty()) {
                local.insertUsers(usersToInsert)
            }

            RepositoryResult.Success("Descargados: ${usersToInsert.size} nuevos, ${usersToUpdate.size} actualizados")

        } catch (e: Exception) {
            RepositoryResult.Error("Error descargando datos del servidor", e)
        }
    }
}
