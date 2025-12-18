package com.example.gestionusuarioshibrido.data

import android.util.Log
import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.data.local.toRemote
import com.example.gestionusuarioshibrido.data.local.UserDao
import com.example.gestionusuarioshibrido.network.MockApiService
import com.example.gestionusuarioshibrido.data.remote.RemoteUser
import com.example.gestionusuarioshibrido.data.remote.toLocal
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

    private val TAG = "DefaultUserRepository"

    override fun getAllUsersStream(): Flow<List<User>> {
        return local.getAllUsersStream()
    }

    override suspend fun insertUser(user: User): RepositoryResult {
        return try {
            val userToSave = user.copy(pendingSync = true)
            local.insertUser(userToSave)
            RepositoryResult.Success("Usuario creado localmente")
        } catch (e: Exception) {
            Log.e(TAG, "Error insertUser", e)
            RepositoryResult.Error("Error al crear usuario local", e)
        }
    }

    override suspend fun updateUser(user: User): RepositoryResult {
        return try {
            val userToUpdate = user.copy(pendingSync = true)
            local.updateUser(userToUpdate)
            RepositoryResult.Success("Usuario actualizado localmente")
        } catch (e: Exception) {
            Log.e(TAG, "Error updateUser", e)
            RepositoryResult.Error("Error al actualizar usuario local", e)
        }
    }

    override suspend fun deleteUser(user: User): RepositoryResult {
        return try {
            val userToDelete = user.copy(pendingDelete = true, pendingSync = true)
            local.updateUser(userToDelete)
            RepositoryResult.Success("Usuario marcado para eliminación")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleteUser", e)
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
     */

    override suspend fun uploadPendingChanges(): RepositoryResult {
        return try {
            var usuariosSubidos = 0
            var usuariosBorrados = 0

            // 1. Obtener actualizaciones pendientes (Altas y Modificaciones)
            val pendingUpdates = local.getPendingUpdates()

            for (user in pendingUpdates) {
                if (user.id.startsWith("local_")) {
                    // Crear en remoto usando DTO remoto
                    val createdRemote: RemoteUser = remote.createUser(user.toRemote())

                    // Reemplazar local provisional por la versión remota
                    local.deleteUserById(user.id)
                    local.insertUser(createdRemote.toLocal())

                    usuariosSubidos++
                } else {
                    // Actualizar en remoto usando DTO remoto
                    remote.updateUser(user.id, user.toRemote())

                    // Marcar como sincronizado
                    local.updateUser(user.copy(pendingSync = false))
                    usuariosSubidos++
                }
            }

            val pendingDeletes = local.getPendingDeletes()

            for (user in pendingDeletes) {
                if (!user.id.startsWith("local_")) {
                    try {
                        val response = remote.deleteUser(user.id)
                        if (!response.isSuccessful) {
                            Log.w(TAG, "deleteUser response not successful for ${user.id}: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "deleteUser failed for ${user.id}", e)
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
            Log.e(TAG, "Error during uploadPendingChanges", e)
            RepositoryResult.Error("Error durante la subida de cambios", e)
        }
    }


    /**
     * Sincroniza la base de datos local con el estado completo del servidor remoto (`REMOTE -> LOCAL`).
     */
    override suspend fun syncFromServer(): RepositoryResult {
        return try {
            val remoteUsersDTO: List<RemoteUser> = remote.getAllUsers()

            val remoteUsers = remoteUsersDTO

            val localIds = local.getAllUserIds()

            val usersToInsert = mutableListOf<User>()
            val usersToUpdate = mutableListOf<User>()

            for (remoteUser in remoteUsers) {
                val localUser = remoteUser.toLocal()
                if (localIds.contains(localUser.id)) {
                    usersToUpdate.add(localUser)
                } else {
                    usersToInsert.add(localUser)
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
            Log.e(TAG, "Error during syncFromServer", e)
            RepositoryResult.Error("Error descargando datos del servidor", e)
        }
    }
}
