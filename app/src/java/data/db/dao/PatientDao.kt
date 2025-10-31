@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(patient: PatientEntity)

    @Query("SELECT * FROM patients ORDER BY registration_date DESC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE patient_id = :id LIMIT 1")
    suspend fun getPatientById(id: String): PatientEntity?
}
