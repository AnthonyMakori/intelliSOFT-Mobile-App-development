class PatientRepository(
    private val db: AppDatabase,
    private val api: ApiService,
    private val auth: AuthManager
) {
    private val patientDao = db.patientDao()
    private val vitalsDao = db.vitalsDao()
    private val assessmentDao = db.assessmentDao()
    private val queueDao = db.syncQueueDao()

    suspend fun registerPatientLocal(patient: PatientEntity) {
        patientDao.insert(patient)
        // enqueue network submission
        val payload = mapOf(
            "patient_id" to patient.patientId,
            "first_name" to patient.firstName,
            "last_name" to patient.lastName,
            "registration_date" to patient.registrationDate,
            "dob" to patient.dob,
            "gender" to patient.gender
        )
        queueDao.enqueue(SyncQueueEntity(endpoint = "patients/register", payloadJson = Gson().toJson(payload)))
        trySubmitQueue()
    }

    suspend fun addVitalsLocal(vitals: VitalsEntity) {
        vitalsDao.insert(vitals)
        val payload = mapOf(
            "patient_id" to vitals.patientId,
            "visit_date" to vitals.visitDate,
            "height_cm" to vitals.heightCm,
            "weight_kg" to vitals.weightKg,
            "bmi" to vitals.bmi
        )
        queueDao.enqueue(SyncQueueEntity(endpoint = "vitals/add", payloadJson = Gson().toJson(payload)))
        trySubmitQueue()
    }

    suspend fun addAssessmentLocal(assessment: AssessmentEntity) {
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
        queueDao.enqueue(SyncQueueEntity(endpoint = "visits/add", payloadJson = Gson().toJson(payload)))
        trySubmitQueue()
    }

    // A simple queue submit attempt, called after enqueueing.
    suspend fun trySubmitQueue() {
        val token = auth.getToken() ?: return
        val clientToken = "Bearer $token"
        val items = queueDao.getAll()
        for (item in items) {
            val bodyMap: Map<String, Any> = Gson().fromJson(item.payloadJson, object: TypeToken<Map<String, Any>>(){}.type)
            try {
                val response = when(item.endpoint) {
                    "patients/register" -> api.registerPatient(bodyMap)
                    "vitals/add" -> api.addVitals(clientToken, bodyMap)
                    "visits/add" -> api.visitsAdd(clientToken, bodyMap) // may require query params depending on backend
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
