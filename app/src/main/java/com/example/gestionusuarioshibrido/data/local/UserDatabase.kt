package com.example.gestionusuarioshibrido.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local para la gestión de usuarios.
 *
 * Esta clase define la configuración principal de Room, incluyendo las entidades
 * y la versión de la base de datos. Room genera automáticamente la implementación
 * concreta durante la compilación.
 *
 * La base de datos se implementa como un *singleton* para evitar múltiples instancias
 * simultáneas que puedan generar fugas de memoria o inconsistencias de acceso.
 */

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de usuarios.
     *
     * @return Instancia de [UserDao] gestionada por Room.
     */

    abstract fun userDao(): UserDao

    companion object {

        /**
         * Instancia única de la base de datos.
         *
         * Está marcada como `@Volatile` para asegurar la visibilidad entre hilos
         * y evitar problemas de sincronización en entornos multi-thread.
         */

        @Volatile
        private var Instance: UserDatabase? = null

        /**
         * Obtiene la instancia de la base de datos, creándola si es necesario.
         *
         * Utiliza un bloqueo (`synchronized`) para garantizar que solo se cree
         * una instancia, incluso si varios hilos acceden simultáneamente.
         *
         * La base de datos utiliza `fallbackToDestructiveMigration()`, por lo que
         * cualquier cambio en la versión que no tenga migración definida provocará
         * la recreación completa de la tabla (se perderán los datos anteriores).
         *
         * @param context Contexto de la aplicación, necesario para inicializar Room.
         * @return Instancia única de [UserDatabase].
         */

        fun getDatabase(context: Context): UserDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}