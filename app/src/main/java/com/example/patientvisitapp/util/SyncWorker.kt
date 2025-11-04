package com.example.patientvisitapp.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.patientvisitapp.data.db.AppDatabase
import com.example.patientvisitapp.network.RetrofitClient
import com.google.gson.Gson

class SyncWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val auth = AuthManager(applicationContext)
        val token = auth.getToken()

        if (token.isNullOrBlank()) {
            Log.e("SyncWorker", "Auth token is missing. Retrying later.")
            return Result.retry()
        }

        val api = RetrofitClient.create(auth)
        val queue = db.syncQueueDao().getAll()
        val gson = Gson()

        for (item in queue) {
            try {
                @Suppress("UNCHECKED_CAST")
                val body = gson.fromJson(item.payloadJson, Map::class.java) as Map<String, Any>
                val bearerToken = "Bearer $token"

                when (item.endpoint) {
                    "vitals/add" -> {
                        api.addVitals(bearerToken, body)
                    }
                    "patients/register" -> {
                        api.registerPatient(bearerToken, body)
                    }
                    else -> {
                        Log.w("SyncWorker", "Unknown endpoint: ${item.endpoint}")
                    }
                }
                // If the API call was successful, delete the item from the queue
                db.syncQueueDao().delete(item)
                Log.d("SyncWorker", "Successfully synced and deleted item for endpoint ${item.endpoint}")

            } catch (e: Exception) {
                // If any error occurs during the API call, log it and retry the whole worker later.
                Log.e("SyncWorker", "Failed to sync item for endpoint ${item.endpoint}. Retrying later.", e)
                return Result.retry()
            }
        }
        // If all items in the queue have been processed, return success
        return Result.success()
    }
}
