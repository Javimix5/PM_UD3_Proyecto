package com.example.gestionusuarioshibrido.data.remote

import com.example.gestionusuarioshibrido.data.local.User
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUser(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val userName: String,
    val positionTitle: String,
    val imagen: String
)


fun RemoteUser.toLocal(): User =
    User(
        id = id ?: "local_${System.nanoTime()}",
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        userName = userName,
        positionTitle = positionTitle,
        imagen = imagen,
        pendingSync = false,
        pendingDelete = false
    )