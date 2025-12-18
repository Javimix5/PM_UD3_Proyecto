package com.example.gestionusuarioshibrido.sensors

import android.content.Context
import android.util.Log
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
    private val TAG = "ShakeUserCoordinator"

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
        Log.d(TAG, "Sacudida detectada: iniciando sync() en ViewModel")
        Toast.makeText(context, "Sacudida detectada: Sincronizando...", Toast.LENGTH_SHORT).show()

        try {
            userViewModel.sync()
            Log.d(TAG, "Llamada a sync() realizada correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al invocar sync desde ShakeUserCoordinator", e)
            Toast.makeText(context, "Error al iniciar sincronización: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun startListening() {
        Log.d(TAG, "Iniciando escucha de sensor")
        sensorShakeDetector.start()
    }

    fun stopListening() {
        Log.d(TAG, "Parando escucha de sensor")
        sensorShakeDetector.stop()
    }
}