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
