package com.example.gestionusuarioshibrido.data

import android.content.Context
import androidx.room.Room
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
    private val baseUrl = "https://69286b9db35b4ffc5015a129.mockapi.io/api/wirtz/"
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: MockApiService by lazy {
        retrofit.create(MockApiService::class.java)
    }
    private val database: UserDatabase by lazy {
        Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        )
            .build()
    }


    override val userRepository: UserRepository by lazy {
        DefaultUserRepository(
            local = database.userDao(),
            remote = retrofitService
        )
    }
}