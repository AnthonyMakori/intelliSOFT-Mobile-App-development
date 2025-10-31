@Dao
interface AssessmentDao {
    @Insert
    suspend fun insert(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessments WHERE patient_id = :patientId ORDER BY visit_date DESC")
    fun getForPatient(patientId: String): Flow<List<AssessmentEntity>>
}
