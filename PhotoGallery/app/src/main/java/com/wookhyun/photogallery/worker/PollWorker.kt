package com.wookhyun.photogallery.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wookhyun.photogallery.PhotoRepository
import com.wookhyun.photogallery.PreferenceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "PollWorker"

@HiltWorker
class PollWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters
    ,val preferenceRepository: PreferenceRepository
    ,val photoRepository: PhotoRepository
) : CoroutineWorker(context, workerParameters) {

//    @Inject
//    lateinit var preferenceRepository: PreferenceRepository
//
//    @Inject
//    lateinit var photoRepository: PhotoRepository

    override suspend fun doWork(): Result {
        Log.i(TAG, "work request triggered")
        val query = preferenceRepository.storedQuery.first()
        val lastId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing elary")
            return Result.success()
        }
        return try {
            val items = photoRepository.searchPhotos(query)
            if(items.isNotEmpty()) {
                val newResultId = items.first().id
                if(newResultId == lastId) {
                    Log.i(TAG, "Still have the same result $newResultId ")
                } else{
                    Log.i(TAG, "Got a new result: $newResultId")
                    preferenceRepository.setLastResultId(newResultId)
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update filed", ex)
            Result.failure()
        }
//        return Result.success()
    }
}