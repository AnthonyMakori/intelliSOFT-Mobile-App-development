@Dao
interface VitalsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vitals: VitalsEntity)

    @Query("SELECT * FROM vitals WHERE patient_id = :patientId ORDER BY visit_date DESC")
    fun getVitalsForPatient(patientId: String): Flow<List<VitalsEntity>>

    @Query("SELECT * FROM vitals WHERE visit_date BETWEEN :from AND :to")
    suspend fun getVitalsByDateRange(from: Long, to: Long): List<VitalsEntity>

    @Query("SELECT * FROM vitals WHERE patient_id = :patientId ORDER BY visit_date DESC LIMIT 1")
    suspend fun getLastVitalsForPatient(patientId: String): VitalsEntity?
}
