@Dao
interface SyncQueueDao {
    @Insert
    suspend fun enqueue(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    suspend fun getAll(): List<SyncQueueEntity>

    @Delete
    suspend fun delete(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET attempts = attempts + 1 WHERE id = :id")
    suspend fun incrementAttempts(id: Long)
}
