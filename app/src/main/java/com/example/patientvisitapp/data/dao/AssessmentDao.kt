package com.example.patientvisitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.patientvisitapp.data.model.AssessmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Insert
    suspend fun insert(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessments WHERE patient_id = :patientId ORDER BY visit_date DESC")
    fun getForPatient(patientId: String): Flow<List<AssessmentEntity>>
}
