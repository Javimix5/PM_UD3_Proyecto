package com.example.gestionusuarioshibrido.data

import android.content.Context
import com.example.gestionusuarioshibrido.data.local.UserDatabase
import com.example.gestionusuarioshibrido.network.MockApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val userRepository: UserRepository
}
class AppDataContainer(private val context: Context) : AppContainer {

    /* A IMPLEMENTAR POR EL ESTUDIANTE */

    override val userRepository: UserRepository by lazy {
        throw UnsupportedOperationException("A completar por el estudiante")
    }
}