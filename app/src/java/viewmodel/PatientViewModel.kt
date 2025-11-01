class PatientViewModel(private val repo: PatientRepository, private val app: Application): AndroidViewModel(app) {

    val registrationState = MutableLiveData<Result<Unit>>()

    fun registerPatient(patientId: String, firstName: String, lastName: String, dobMillis: Long, gender: String) {
        viewModelScope.launch {
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
                registrationState.postValue(Result.success(Unit))
            } catch (e: SQLiteConstraintException) {
                registrationState.postValue(Result.failure(Exception("Patient ID already exists.")))
            } catch (e: Exception) {
                registrationState.postValue(Result.failure(e))
            }
        }
    }

    fun addVitals(patientId:String, visitDate:Long, heightCm:Double, weightKg:Double, onNextScreen: (Double)->Unit) {
        viewModelScope.launch {
            val bmi = Utils.calculateBMI(heightCm, weightKg)
            val vitals = VitalsEntity(patientId = patientId, visitDate = visitDate, heightCm = heightCm, weightKg = weightKg, bmi = bmi)
            repo.addVitalsLocal(vitals)
            onNextScreen(bmi) // caller decides which screen to navigate to
        }
    }
}
