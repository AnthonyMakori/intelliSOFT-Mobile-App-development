interface ApiService {
    @POST("user/signup")
    suspend fun signup(@Body body: Map<String, Any>): Response<ApiResponse<Any>>

    @POST("user/login")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("patients/register")
    suspend fun registerPatient(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @GET("patients/list")
    suspend fun getPatients(@Header("Authorization") token: String): Response<ApiResponse<List<PatientNetworkModel>>>

    @POST("vitals/add")
    suspend fun addVitals(@Header("Authorization") token: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @POST("visits/view")
    suspend fun visitsByDate(@Header("Authorization") token: String, @Body body: Map<String, String>): Response<ApiResponse<List<VisitNetworkModel>>>

    @GET("visits/add")
    suspend fun visitsAdd(@Header("Authorization") token: String, @Query("patient_id") patientId: String, @Query("type") type: String): Response<ApiResponse<Any>>
}
