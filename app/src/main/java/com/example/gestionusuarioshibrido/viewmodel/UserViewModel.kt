package com.example.gestionusuarioshibrido.viewmodel

import android.content.Context
import androidx.core.os.registerForAllProfilingResults
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestionusuarioshibrido.GestionUsuariosApplication
import com.example.gestionusuarioshibrido.data.RepositoryResult
import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.data.UserRepository
import com.example.gestionusuarioshibrido.data.testUsers
import com.example.gestionusuarioshibrido.sensors.ShakeUserCoordinator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar la lógica de presentación relacionada con usuarios.
 *
 * Orquesta las operaciones CRUD, la sincronización con el repositorio híbrido
 * (local + remoto), la comunicación de mensajes hacia la UI y la escucha del
 * sensor “shake” para sincronizar local y remoto con una sacudida.
 *
 * @property userRepository Repositorio híbrido que gestiona acceso local y remoto.
 */
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    /**
     * Canal privado para emitir eventos puntuales hacia la UI
     * (mensajes Toast, SnackBars, etc.).
     */
    private val _events = Channel<String>()
    /**
     * Flujo público de eventos que la UI observará.
     */
    val events = _events.receiveAsFlow()

    /**
     * Contador de usuarios de prueba que ya se han insertado.
     */
    private var usersAddedCount = 0

    /**
     * Coordinador encargado de escuchar el gesto shake del dispositivo
     * y notificar al ViewModel para realizar la sincronización.
     */
    private var shakeUserCoordinator: ShakeUserCoordinator? = null

    /**
     * Flujo observable de usuarios, proveniente del repositorio.
     * Utiliza `stateIn` para mantener un estado interno que persiste
     * mientras la UI esté suscrita.
     */
    val users: StateFlow<List<User>> = userRepository.getAllUsersStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Inserta un usuario en el repositorio y procesa el resultado,
     * enviando un mensaje a la UI si es necesario.
     *
     * @param user Usuario a insertar.
     */
    fun insertUser(user: User) = viewModelScope.launch {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Actualiza un usuario existente en el repositorio y notifica el resultado.
     *
     * @param user Usuario actualizado.
     */
    fun updateUser(user: User) = viewModelScope.launch {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Marca un usuario para borrado (soft-delete) y procesa la respuesta.
     *
     * @param user Usuario a eliminar.
     */
    fun deleteUser(user: User) = viewModelScope.launch {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Inicializa el listener del gesto de agitar el dispositivo (shake)
     * si aún no está configurado. Este gesto permite añadir usuarios de prueba.
     *
     * @param context Contexto necesario para registrar los sensores.
     */
    fun setupShakeListener(context: Context) {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Limpia los listeners del sensor al destruir el ViewModel.
     */

    override fun onCleared() {
        throw UnsupportedOperationException("A completar por el estudiante")
    }


    /**
     * Añade un usuario de prueba de la lista `testUsers`.
     * Envía un mensaje informando el número restante de usuarios disponibles.
     */

    fun addTestUser() = viewModelScope.launch {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Ejecuta la sincronización híbrida completa:
     *
     * 1. Sube cambios pendientes del cliente → servidor (altas, modificaciones y borrados).
     * 2. Descarga el estado más reciente del servidor → cliente.
     *
     * Tras cada fase, procesa el resultado y lo comunica a la UI.
     */
    fun sync() = viewModelScope.launch {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Procesa un resultado del repositorio, enviando a la UI el mensaje apropiado.
     *
     * @param result Resultado devuelto por el repositorio.
     */
    private suspend fun processResult(result: RepositoryResult) {
        when (result) {
            is RepositoryResult.Success ->
                _events.send(result.message)

            is RepositoryResult.Error ->
                _events.send("${result.message} ${result.exception?.message ?: ""}".trim())
        }
    }

    /**
     * Factoría para crear instancias de [UserViewModel] utilizando el contenedor
     * de dependencias de la aplicación.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GestionUsuariosApplication)
                val userRepository = application.container.userRepository
                UserViewModel(userRepository = userRepository)
            }
        }
    }
}