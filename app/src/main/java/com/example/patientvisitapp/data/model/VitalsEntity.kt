package com.example.patientvisitapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "vitals", indices = [Index(value = ["patient_id", "visit_date"], unique = true)])
data class VitalsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "patient_id") val patientId: String,
    @ColumnInfo(name = "visit_date") val visitDate: Long,
    @ColumnInfo(name = "height_cm") val heightCm: Double,
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "bmi") val bmi: Double
)
