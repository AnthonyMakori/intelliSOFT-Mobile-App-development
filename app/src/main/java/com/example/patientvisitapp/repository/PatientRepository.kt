package com.example.patientvisitapp.repository

import com.example.patientvisitapp.data.db.AppDatabase
import com.example.patientvisitapp.data.model.AssessmentEntity
import com.example.patientvisitapp.data.model.PatientEntity
import com.example.patientvisitapp.data.model.SyncQueueEntity
import com.example.patientvisitapp.data.model.VitalsEntity
import com.example.patientvisitapp.network.ApiService
import com.example.patientvisitapp.util.AuthManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PatientRepository(
    private val db: AppDatabase,
    private val api: ApiService,
    private val auth: AuthManager
) {
    private val patientDao = db.patientDao()
    private val vitalsDao = db.vitalsDao()
    private val assessmentDao = db.assessmentDao()
    private val queueDao = db.syncQueueDao()
    private val gson = Gson()

    suspend fun registerPatientLocal(patient: PatientEntity) = withContext(Dispatchers.IO) {
        patientDao.insert(patient)
        val payload = mapOf(
            "patient_id" to patient.patientId,
            "first_name" to patient.firstName,
            "last_name" to patient.lastName,
            "registration_date" to patient.registrationDate,
            "dob" to patient.dob,
            "gender" to patient.gender
        )
        queueDao.enqueue(SyncQueueEntity(endpoint = "patients/register", payloadJson = gson.toJson(payload)))
        trySubmitQueue()
    }

    suspend fun addVitalsLocal(vitals: VitalsEntity) = withContext(Dispatchers.IO) {
        vitalsDao.insert(vitals)
        val payload = mapOf(
            "patient_id" to vitals.patientId,
            "visit_date" to vitals.visitDate,
            "height_cm" to vitals.heightCm,
            "weight_kg" to vitals.weightKg,
            "bmi" to vitals.bmi
        )
        queueDao.enqueue(SyncQueueEntity(endpoint = "vitals/add", payloadJson = gson.toJson(payload)))
        trySubmitQueue()
    }

    suspend fun addAssessmentLocal(assessment: AssessmentEntity) = withContext(Dispatchers.IO) {
        assessmentDao.insert(assessment)
        val payload = mapOf(
            "patient_id" to assessment.patientId,
            "visit_date" to assessment.visitDate,
            "type" to assessment.type,
            "general_health" to assessment.generalHealth,
            "used_diet_before" to assessment.usedDietBefore,
            "using_drugs" to assessment.usingDrugs,
            "comments" to assessment.comments
        )
        queueDao.enqueue(SyncQueueEntity(endpoint = "visits/add", payloadJson = gson.toJson(payload)))
        trySubmitQueue()
    }

    private suspend fun trySubmitQueue() = withContext(Dispatchers.IO) {
        val token = auth.getToken() ?: return@withContext
        val clientToken = "Bearer $token"
        val items = queueDao.getAll()
        for (item in items) {
            val bodyMap: Map<String, Any> = gson.fromJson(item.payloadJson, Map::class.java) as Map<String, Any>
            try {
                val response = when(item.endpoint) {
                    "patients/register" -> api.registerPatient(clientToken, bodyMap)
                    "vitals/add" -> api.addVitals(clientToken, bodyMap)
                    "visits/add" -> api.visitsAdd(clientToken, bodyMap["patient_id"].toString(), bodyMap["type"]?.toString() ?: "A")
                    else -> null
                }
                if (response != null && response.isSuccessful) {
                    queueDao.delete(item)
                } else {
                    queueDao.incrementAttempts(item.id)
                }
            } catch (e: Exception) {
                queueDao.incrementAttempts(item.id)
            }
        }
    }
}
