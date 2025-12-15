package com.example.gestionusuarioshibrido.network

import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.data.remote.RemoteUser
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MockApiService {

    /*   A IMPLEMENTAR POR EL ESTUDIANTE  */
    @GET("users")
    suspend fun getAllUsers(): List<User>

    @POST("users")
    suspend fun createUser(@Body user: User): User

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): User
}
