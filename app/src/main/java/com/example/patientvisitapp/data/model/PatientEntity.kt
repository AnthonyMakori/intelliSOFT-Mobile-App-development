package com.example.patientvisitapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    @ColumnInfo(name = "patient_id") val patientId: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "registration_date") val registrationDate: Long,
    @ColumnInfo(name = "dob") val dob: Long,
    @ColumnInfo(name = "gender") val gender: String
)
