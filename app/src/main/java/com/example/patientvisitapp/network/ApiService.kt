package com.example.patientvisitapp.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("user/signup")
    suspend fun signup(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    @POST("user/login")
    suspend fun login(@Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("patients/register")
    suspend fun registerPatient(@Header("Authorization") token: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    @GET("patients/list")
    suspend fun getPatients(@Header("Authorization") token: String): Response<Map<String, Any>>

    @GET("patients/show/{id}")
    suspend fun getPatient(@Header("Authorization") token: String, @Path("id") id: String): Response<Map<String, Any>>

    @POST("vitals/add")
    suspend fun addVitals(@Header("Authorization") token: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    @POST("visits/view")
    suspend fun visitsByDate(@Header("Authorization") token: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @GET("visits/add")
    suspend fun visitsAdd(@Header("Authorization") token: String, @Query("patient_id") patientId: String, @Query("type") type: String): Response<Map<String, Any>>
}
