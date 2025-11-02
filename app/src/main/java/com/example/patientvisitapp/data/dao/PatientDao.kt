package com.example.patientvisitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.patientvisitapp.data.model.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(patient: PatientEntity)

    @Query("SELECT * FROM patients ORDER BY registration_date DESC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE patient_id = :id LIMIT 1")
    suspend fun getPatientById(id: String): PatientEntity?
}
