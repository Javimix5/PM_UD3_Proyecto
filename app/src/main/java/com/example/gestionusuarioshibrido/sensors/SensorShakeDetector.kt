package com.example.gestionusuarioshibrido.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

/**
 * Detecta una sacudida del dispositivo usando el acelerómetro.
 *
 * @property context Contexto necesario para acceder al sensor.
 * @property onShake Acción que se ejecutará cuando se detecte una sacudida.
 */
class SensorShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Último tiempo registrado para evitar múltiples eventos seguidos
    private var lastShakeTime = 0L
    // Última precisión conocida del sensor
    var lastAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE

    /**
     * Registra el listener del acelerómetro.
     */
    fun start() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * Detiene el listener del acelerómetro.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Maneja los eventos generados por el acelerómetro.
     *
     * Esta función se ejecuta automáticamente cada vez que el sensor detecta
     * cambios en la aceleración del dispositivo. Su objetivo es calcular la
     * fuerza G total y determinar si el usuario ha realizado una sacudida.
     *
     * Flujo de funcionamiento:
     * 1. Se obtiene la aceleración en los ejes X, Y y Z.
     * 2. Cada eje se normaliza dividiendo entre la gravedad estándar (9.81 m/s²),
     *    lo que convierte la aceleración en "fuerza G".
     * 3. Se calcula la magnitud del vector (fuerza total en G).
     * 4. Si la fuerza supera el umbral definido (`shakeThreshold`) y ha pasado
     *    suficiente tiempo desde la última detección, se considera una sacudida
     *    válida y se llama a `onShake()`.
     *
     * @param event Objeto con la información del sensor, incluyendo los valores
     *              de aceleración en cada eje. Si es null, la función se detiene.
     */

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()

            if (lastShakeTime + MIN_TIME_BETWEEN_SHAKES <= now) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        lastAccuracy = accuracy
        when (accuracy) {
            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                Log.w(TAG, "Sensor accuracy unreliable")
            }
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                Log.d(TAG, "Sensor accuracy low")
            }
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                Log.d(TAG, "Sensor accuracy medium")
            }
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                Log.d(TAG, "Sensor accuracy high")
            }
            else -> {
                Log.d(TAG, "Sensor accuracy changed: $accuracy")
            }
        }
    }

    // Constantes utilizadas en la detección de sacudidas
    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY: Float = 1.3f
        private const val MIN_TIME_BETWEEN_SHAKES: Long = 500L
        private const val TAG = "SensorShakeDetector"
    }
}

