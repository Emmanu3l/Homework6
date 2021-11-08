package at.technikum_wien.polzert.news

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class CoroutineNewsWorker(context: Context,
                          params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        val LOG_TAG : String = CoroutineNewsWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result { //---> DefaultDispatcher
        /*Log.d(LOG_TAG, "Work started.")
        delay(2000);
        Log.d(LOG_TAG, "Work finished.")*/
        val workRequest = OneTimeWorkRequest.Builder(NewsWorker::class.java)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(
            workRequest
        )
        return Result.success()
    }


}