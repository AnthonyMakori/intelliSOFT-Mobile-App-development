package com.example.patientvisitapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.patientvisitapp.data.dao.AssessmentDao
import com.example.patientvisitapp.data.dao.PatientDao
import com.example.patientvisitapp.data.dao.SyncQueueDao
import com.example.patientvisitapp.data.dao.VitalsDao
import com.example.patientvisitapp.data.model.AssessmentEntity
import com.example.patientvisitapp.data.model.PatientEntity
import com.example.patientvisitapp.data.model.SyncQueueEntity
import com.example.patientvisitapp.data.model.VitalsEntity

@Database(entities = [PatientEntity::class, VitalsEntity::class, AssessmentEntity::class, SyncQueueEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "patient_app.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
