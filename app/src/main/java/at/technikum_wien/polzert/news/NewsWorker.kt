package at.technikum_wien.polzert.news

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class NewsWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters)  {

    companion object {
        val LOG_TAG: String = NewsWorker::class.java.simpleName
        fun createWorkerTest(context: Context) {
            val workRequest = OneTimeWorkRequest.Builder(NewsWorker::class.java)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            Log.d(LOG_TAG, "Scheduling " + workRequest.id)

            WorkManager.getInstance(context).enqueueUniqueWork(
                NewsWorker::class.java.canonicalName ?: "",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override fun doWork(): Result {
        Log.d(LOG_TAG, "Work started.")
        Thread.sleep(2000)
        Log.d(LOG_TAG, "Work finished.")
        createWorkerTest(applicationContext) //TODO FIXME!!!!! Only for testing!!!!
        return Result.success()
    }

}