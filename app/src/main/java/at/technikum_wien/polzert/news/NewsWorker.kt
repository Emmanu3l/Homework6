package at.technikum_wien.polzert.news

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import at.technikum_wien.polzert.news.data.NewsRepository
import at.technikum_wien.polzert.news.data.db.ApplicationDatabase
import at.technikum_wien.polzert.news.data.download.NewsDownloader
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NewsWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters)  {

    private val _error = MutableLiveData<Boolean>(false)
    private var lastFeedUrl : String? = null
    val newsRepository = NewsRepository(applicationContext)

    /*companion object {
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
    }*/

    @DelicateCoroutinesApi
    override fun doWork(): Result {
        /*Log.d(LOG_TAG, "Work started.")
        Thread.sleep(2000)
        Log.d(LOG_TAG, "Work finished.")
        createWorkerTest(applicationContext) //TODO FIXME!!!!! Only for testing!!!!
        return Result.success()*/

        //fill database, check whether empty in activity

        val newsFeedUrl = inputData.getString("url")
        val urlChanged = false

        GlobalScope.launch {
            _error.value = false
            val newsItemsFromDownloader = NewsDownloader().load(newsFeedUrl.orEmpty())
            when {
                newsItemsFromDownloader == null -> _error.value = true
                urlChanged -> newsRepository.replace(newsItemsFromDownloader)
                else -> newsRepository.insert(newsItemsFromDownloader)
            }
        }
        return Result.success()
    }

}