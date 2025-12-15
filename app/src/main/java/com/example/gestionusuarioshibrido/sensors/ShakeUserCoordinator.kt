package com.example.gestionusuarioshibrido.sensors

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarioshibrido.data.testUsers
import com.example.gestionusuarioshibrido.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Clase responsable de iniciar y detener el SensorShakeDetector
 * y cuando detecta una sacudida ejecuta una sincronización
 */
class ShakeUserCoordinator(
    private val context: Context,
    private val userViewModel: UserViewModel
) {
    private val sensorShakeDetector: SensorShakeDetector

    init {
        // Inicializar el detector y definir el callback
        sensorShakeDetector = SensorShakeDetector(context) {
            throw UnsupportedOperationException("A completar por el estudiante")
        }
    }

    /**
     * Lógica que se ejecuta al detectar una sacudida.
     */
    private fun handleShakeEvent() {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    fun startListening() {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    fun stopListening() {
        throw UnsupportedOperationException("A completar por el estudiante")
    }
}