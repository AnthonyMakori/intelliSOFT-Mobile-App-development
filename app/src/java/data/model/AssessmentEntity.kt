@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "patient_id") val patientId: String,
    @ColumnInfo(name = "visit_date") val visitDate: Long,
    @ColumnInfo(name = "type") val type: String, // "general" or "overweight"
    @ColumnInfo(name = "general_health") val generalHealth: String,
    @ColumnInfo(name = "used_diet_before") val usedDietBefore: String?, // for general
    @ColumnInfo(name = "using_drugs") val usingDrugs: String?, // for overweight
    @ColumnInfo(name = "comments") val comments: String?
)
