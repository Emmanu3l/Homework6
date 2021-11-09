package at.technikum_wien.polzert.news

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import at.technikum_wien.polzert.news.data.NewsRepository
import at.technikum_wien.polzert.news.data.db.ApplicationDatabase
import at.technikum_wien.polzert.news.data.db.NewsItemDao
import at.technikum_wien.polzert.news.data.download.NewsDownloader
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NewsWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters)  {

    private var _error = false
    private val newsRepository = NewsRepository(applicationContext)
    private val newsItemDao = ApplicationDatabase.getDatabase(applicationContext).newsItemDao()
    private val newsFeedUrl = inputData.getString("feedUrl")
    private val urlChanged = inputData.getBoolean("urlChanged", false)
    private val deleteOld = inputData.getBoolean("deleteOld", false)


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

        GlobalScope.launch {
            _error = false
            val newsItemsFromDownloader = NewsDownloader().load(newsFeedUrl.orEmpty())
            if (deleteOld) {
                newsItemDao.deleteOld()
            }
            when {
                newsItemsFromDownloader == null -> _error = true
                urlChanged -> newsRepository.replace(newsItemsFromDownloader)
                else -> newsRepository.insert(newsItemsFromDownloader)
            }
        }
        return if(_error) {
            Result.failure()
        } else {
            Result.success()
        }
    }

}