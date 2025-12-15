package com.example.gestionusuarioshibrido.sensors

import android.content.Context
import android.widget.Toast
import com.example.gestionusuarioshibrido.viewmodel.UserViewModel

/**
 * Clase responsable de iniciar y detener el SensorShakeDetector
 * y cuando detecta una sacudida ejecuta una sincronización.
 */
class ShakeUserCoordinator(
    private val context: Context,
    private val userViewModel: UserViewModel
) {
    private val sensorShakeDetector: SensorShakeDetector

    init {
        // Inicializar el detector y definir el callback
        sensorShakeDetector = SensorShakeDetector(context) {
            handleShakeEvent()
        }
    }

    /**
     * Lógica que se ejecuta al detectar una sacudida.
     */
    private fun handleShakeEvent() {
        Toast.makeText(context, "Sacudida detectada: Sincronizando...", Toast.LENGTH_SHORT).show()

        userViewModel.sync()
    }

    fun startListening() {
        sensorShakeDetector.start()
    }

    fun stopListening() {
        sensorShakeDetector.stop()
    }
}