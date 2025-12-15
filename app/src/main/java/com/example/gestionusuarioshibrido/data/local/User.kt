package com.example.gestionusuarioshibrido.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestionusuarioshibrido.data.remote.RemoteUser

/**
 * Representa un usuario almacenado en la base de datos local.
 *
 * Esta entidad se utiliza dentro de Room para mapear la tabla `users`.
 * Cada instancia de [User] corresponde a un registro individual en la base de datos.
 *
 * @property id Identificador único del usuario. Se genera automáticamente.
 * @property firstName Nombre del usuario.
 * @property lastName Apellidos del usuario.
 * @property email Correo electrónico del usuario.
 * @property age Edad del usuario.
 * @property userName Nombre de usuario único para identificación interna.
 * @property positionTitle Cargo, puesto o rol que desempeña el usuario.
 * @property imagen Ruta o referencia a la imagen asociada al usuario
 * (por ejemplo: URL, nombre de archivo, o base64).
 */

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val userName: String,
    val positionTitle: String,
    val imagen: String,

    // Sincronización
    val pendingSync: Boolean = false,
    val pendingDelete: Boolean = false
)

fun User.toRemote(): RemoteUser =
    RemoteUser(
        id = if (id.startsWith("local_")) null else id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        userName = userName,
        positionTitle = positionTitle,
        imagen = imagen
    )
