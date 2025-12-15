package com.example.gestionusuarioshibrido.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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

    /**
     * Registra el listener del acelerómetro.
     */
    fun start() {
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    /**
     * Detiene el listener del acelerómetro.
     */
    fun stop() {
        throw UnsupportedOperationException("A completar por el estudiante")
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
        throw UnsupportedOperationException("A completar por el estudiante")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        throw UnsupportedOperationException("A completar por el estudiante")
    }
}
