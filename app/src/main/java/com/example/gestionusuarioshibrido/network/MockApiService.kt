package com.example.gestionusuarioshibrido.network

import com.example.gestionusuarioshibrido.data.remote.RemoteUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MockApiService {

    /*   A IMPLEMENTAR POR EL ESTUDIANTE  */
    @GET("users")
    suspend fun getAllUsers(): List<RemoteUser>

    @POST("users")
    suspend fun createUser(@Body user: RemoteUser): RemoteUser

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: RemoteUser): RemoteUser

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>
}
