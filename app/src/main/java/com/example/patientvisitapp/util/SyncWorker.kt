package com.example.patientvisitapp.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.patientvisitapp.data.db.AppDatabase
import com.example.patientvisitapp.network.RetrofitClient

class SyncWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val auth = AuthManager(applicationContext)
        val api = RetrofitClient.create(auth)
        val queue = db.syncQueueDao().getAll()
        for (item in queue) {
            try {
                if (item.endpoint == "vitals/add") {
                    val body = com.google.gson.Gson().fromJson(item.payloadJson, Map::class.java)
                    api.addVitals("Bearer " + (auth.getToken() ?: ""), body as Map<String, Any>)
                    db.syncQueueDao().delete(item)
                } else if (item.endpoint == "patients/register") {
                    val body = com.google.gson.Gson().fromJson(item.payloadJson, Map::class.java)
                    api.registerPatient("Bearer " + (auth.getToken() ?: ""), body as Map<String, Any>)
                    db.syncQueueDao().delete(item)
                }
            } catch (e: Exception) {
            }
        }
        return Result.success()
    }
}
