@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name="endpoint") val endpoint: String, // e.g. "patients/register", "vitals/add"
    @ColumnInfo(name="payload_json") val payloadJson: String,
    @ColumnInfo(name="created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name="attempts") val attempts: Int = 0
)
