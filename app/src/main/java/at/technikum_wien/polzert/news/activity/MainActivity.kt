package at.technikum_wien.polzert.news.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.ViewModelProvider
import at.technikum_wien.polzert.news.data.NewsRepository
import at.technikum_wien.polzert.news.data.db.ApplicationDatabase
import at.technikum_wien.polzert.news.settings.UserPreferencesRepository
import at.technikum_wien.polzert.news.settings.dataStore
//import at.technikum_wien.polzert.news.data.createDummyNews
import at.technikum_wien.polzert.news.ui.theme.NewsTheme
import at.technikum_wien.polzert.news.view.Navigation
import at.technikum_wien.polzert.news.viewmodels.NewsListViewModel
import at.technikum_wien.polzert.news.viewmodels.NewsListViewModelFactory
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val newsItems = createDummyNews(count = 30)
        val viewModel = ViewModelProvider(this, NewsListViewModelFactory(NewsRepository(applicationContext), UserPreferencesRepository(dataStore))).get(NewsListViewModel::class.java)

        setContent {
            NewsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Navigation(viewModel = viewModel)
                }
            }
        }
    }
}
