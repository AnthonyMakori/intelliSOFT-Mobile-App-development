package com.example.patientvisitapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.patientvisitapp.data.db.AppDatabase
import com.example.patientvisitapp.data.model.AssessmentEntity
import com.example.patientvisitapp.data.model.PatientEntity
import com.example.patientvisitapp.data.model.VitalsEntity
import com.example.patientvisitapp.repository.PatientRepository
import com.example.patientvisitapp.util.AuthManager
import com.example.patientvisitapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

data class PatientListItem(
    val patientId: String,
    val name: String,
    val age: Int,
    val lastBmi: Double?,
    val bmiStatus: String?
)

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val auth = AuthManager(application)
    private val api = RetrofitClient.create(auth)
    private val repo = PatientRepository(db, api, auth)

    /** StateFlows and LiveData **/
    private val _registrationState = MutableLiveData<Result<Unit>>()
    val registrationState: LiveData<Result<Unit>> = _registrationState

    private val _patientList = MutableLiveData<List<PatientListItem>>()
    val patientList: LiveData<List<PatientListItem>> = _patientList

    // Expose current loaded patient
    private val _currentPatient = MutableStateFlow<PatientEntity?>(null)
    val currentPatient: StateFlow<PatientEntity?> = _currentPatient

    init {
        loadPatients()
    }

    /** -------- Patient Registration -------- **/
    fun registerPatient(
        patientId: String,
        firstName: String,
        lastName: String,
        dobMillis: Long,
        gender: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val patient = PatientEntity(
                    patientId = patientId,
                    firstName = firstName,
                    lastName = lastName,
                    registrationDate = System.currentTimeMillis(),
                    dob = dobMillis,
                    gender = gender
                )
                repo.registerPatientLocal(patient)
                _registrationState.postValue(Result.success(Unit))
                loadPatients()
            } catch (e: Exception) {
                _registrationState.postValue(Result.failure(e))
            }
        }
    }

    /** Alternative add function with callback **/
    fun addPatient(
        patientId: String,
        regDate: Long,
        first: String,
        last: String,
        dob: Long,
        gender: String,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val patient = PatientEntity(patientId, first, last, regDate, dob, gender)
                repo.registerPatientLocal(patient)
                withContext(Dispatchers.Main) { onComplete?.invoke() }
                loadPatients()
            } catch (_: Exception) {}
        }
    }

    /** ------- Load Specific Patient ------- **/
    fun loadPatientById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentPatient.value = db.patientDao().getPatientById(id)
        }
    }

    /** ------- Add Vitals ------- **/
    fun addVitals(
        patientId: String,
        visitDate: Long,
        heightCm: Double,
        weightKg: Double,
        onNext: (Double) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val heightM = heightCm / 100.0
            val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0.0
            val roundedBmi = round(bmi * 100) / 100.0

            val vitals = VitalsEntity(
                patientId = patientId,
                visitDate = visitDate,
                heightCm = heightCm,
                weightKg = weightKg,
                bmi = roundedBmi
            )
            repo.addVitalsLocal(vitals)
            loadPatients()
            withContext(Dispatchers.Main) { onNext(roundedBmi) }
        }
    }

    /** ------- Add Assessment ------- **/
    fun addAssessment(
        patientId: String,
        visitDate: Long,
        type: String,
        generalHealth: String,
        usedDiet: String?,
        usingDrugs: String?,
        comments: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val assessment = AssessmentEntity(
                patientId = patientId,
                visitDate = visitDate,
                type = type,
                generalHealth = generalHealth,
                usedDietBefore = usedDiet,
                usingDrugs = usingDrugs,
                comments = comments
            )
            repo.addAssessmentLocal(assessment)
            loadPatients()
        }
    }

    /** ------- Load Patient List ------- **/
    fun loadPatients() {
        viewModelScope.launch(Dispatchers.IO) {
            db.patientDao().getAllPatients().collect { list ->
                val items = list.map { p ->
                    val lastVitals = db.vitalsDao().getLastVitalsForPatient(p.patientId)
                    val bmi = lastVitals?.bmi
                    val status = when {
                        bmi == null -> null
                        bmi < 18.5 -> "Underweight"
                        bmi < 25.0 -> "Normal"
                        else -> "Overweight"
                    }
                    PatientListItem(
                        p.patientId,
                        "${p.firstName} ${p.lastName}",
                        calculateAge(p.dob),
                        bmi,
                        status
                    )
                }
                _patientList.postValue(items)
            }
        }
    }

    /** ------- Calculate Age ------- **/
    private fun calculateAge(dobMillis: Long): Int {
        val dob = java.util.Calendar.getInstance().apply { timeInMillis = dobMillis }
        val now = java.util.Calendar.getInstance()
        var age = now.get(java.util.Calendar.YEAR) - dob.get(java.util.Calendar.YEAR)
        if (now.get(java.util.Calendar.DAY_OF_YEAR) < dob.get(java.util.Calendar.DAY_OF_YEAR)) age--
        return age
    }

    /** ------- Filter By Visit Date ------- **/
    fun filterByVisitDate(dateMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val from = dateMillis
            val to = dateMillis + 24 * 60 * 60 * 1000 - 1
            val vitals = db.vitalsDao().getVitalsByDateRange(from, to)
            val patientIds = vitals.map { it.patientId }.toSet()

            val items = patientIds.mapNotNull { id ->
                val p = db.patientDao().getPatientById(id) ?: return@mapNotNull null
                val lastVitals = db.vitalsDao().getLastVitalsForPatient(id)
                val bmi = lastVitals?.bmi
                val status = when {
                    bmi == null -> null
                    bmi < 18.5 -> "Underweight"
                    bmi < 25.0 -> "Normal"
                    else -> "Overweight"
                }
                PatientListItem(p.patientId, "${p.firstName} ${p.lastName}", calculateAge(p.dob), bmi, status)
            }
            _patientList.postValue(items)
        }
    }
}
