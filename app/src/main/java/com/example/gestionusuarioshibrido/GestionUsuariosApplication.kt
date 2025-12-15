package com.example.gestionusuarioshibrido
import android.app.Application
import com.example.gestionusuarioshibrido.data.AppContainer
import com.example.gestionusuarioshibrido.data.AppDataContainer

/**
 * Clase principal de la aplicación que inicializa y mantiene el contenedor
 * de dependencias usado en toda la app.
 *
 * Esta clase extiende [Application] y se encarga de crear la instancia de
 * [AppContainer] que provee los repositorios y fuentes de datos necesarios
 * para el funcionamiento de la aplicación.
 */

class GestionUsuariosApplication : Application() {

    /**
     * Contenedor de dependencias que proporciona las instancias necesarias
     * para acceder a los repositorios y otros componentes de datos.
     *
     * Se inicializa en [onCreate] y permanece vivo durante todo el ciclo
     * de vida de la aplicación.
     */

    lateinit var container: AppContainer

    /**
     * Método invocado cuando la aplicación se inicia.
     *
     * Aquí se inicializa el contenedor de dependencias [AppDataContainer],
     * el cual recibe el contexto de aplicación y configura los componentes
     * necesarios para el acceso a los datos.
     */

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
