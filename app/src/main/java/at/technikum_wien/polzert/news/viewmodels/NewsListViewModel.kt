package at.technikum_wien.polzert.news.viewmodels

import androidx.lifecycle.*
import at.technikum_wien.polzert.news.data.download.NewsDownloader
import at.technikum_wien.polzert.news.data.NewsRepository
import at.technikum_wien.polzert.news.settings.UserPreferencesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsListViewModel(private val newsRepository : NewsRepository, private val userPreferencesRepository : UserPreferencesRepository) : ViewModel() {
    private val _error = MutableLiveData<Boolean>(false)
    private var lastFeedUrl : String? = null
    val newsItems by lazy { newsRepository.newsItems }

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect {
                feedUrl.value = it.feedUrl
                showImages.value = it.showImages
                downloadImages.value = it.downloadImages
                /*if (lastFeedUrl != null && lastFeedUrl != it.feedUrl) {
                    downloadNewsItems(it.feedUrl, delete = true)
                }*/
                lastFeedUrl = it.feedUrl
            }
        }
    }

    val error : LiveData<Boolean>
        get() = _error
    //val feedUrl = MutableLiveData("")
    val feedUrl = MutableLiveData("https://www.engadget.com/rss.xml")
    val showImages = MutableLiveData(false)
    val downloadImages = MutableLiveData(false)

    private fun downloadNewsItems(newsFeedUrl: String, delete : Boolean) {
        _error.value = false
        viewModelScope.launch {
            val newsItemsFromDownloader = NewsDownloader().load(newsFeedUrl)
            when {
                newsItemsFromDownloader == null -> _error.value = true
                delete -> newsRepository.replace(newsItemsFromDownloader)
                else -> newsRepository.insert(newsItemsFromDownloader)
            }
        }
    }

    fun updatePreferences(feedUrl : String, showImages : Boolean, downloadImages : Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateFeedUrl(feedUrl = feedUrl)
            userPreferencesRepository.updateShowImages(showImages = showImages)
            userPreferencesRepository.updateDownloadImages(downloadImages = downloadImages)
        }
    }

    fun reload() {
        lastFeedUrl?.let { downloadNewsItems(it, delete = false) }
    }
}
