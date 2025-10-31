@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    @ColumnInfo(name = "patient_id")
    val patientId: String, // unique patient ID (assignment requirement)

    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "registration_date") val registrationDate: Long, // epoch millis
    @ColumnInfo(name = "dob") val dob: Long, // epoch millis
    @ColumnInfo(name = "gender") val gender: String
)
