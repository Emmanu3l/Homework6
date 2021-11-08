package at.technikum_wien.polzert.news.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.work.*
import at.technikum_wien.polzert.news.CoroutineNewsWorker
import at.technikum_wien.polzert.news.NewsWorker
import at.technikum_wien.polzert.news.R
import at.technikum_wien.polzert.news.viewmodels.NewsListViewModel
import java.util.concurrent.TimeUnit

@Composable
fun MainScreen(navController: NavController, viewModel : NewsListViewModel) {
    val newsItems by viewModel.newsItems.observeAsState()
    val error by viewModel.error.observeAsState()
    var expanded by remember { mutableStateOf(false) }

    val feedUrl by viewModel.feedUrl.observeAsState()
    var workerData : Data = workDataOf("feedUrl" to feedUrl)
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_title))
            },
            actions = {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.menu)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                //viewModel.reload()

                                val workRequest = OneTimeWorkRequest.Builder(NewsWorker::class.java)
                                    .setInputData(workerData)
                                    .build()
                                WorkManager.getInstance(context).enqueue(
                                    workRequest
                                )
                            },
                        ) {
                            Text(stringResource(R.string.reload))
                        }
                        DropdownMenuItem(
                            onClick = {
                                navController.navigate(Screen.SettingsScreen.route)
                            },
                        ) {
                            Text(stringResource(R.string.settings))
                        }
                    }
                }
            }
        )
    },
    content =  {
        Column {
            if (error == true)
                Text(text = stringResource(R.string.error_message))
            LazyColumn(Modifier.fillMaxWidth()) {
                //Download new data once every half an hour using a worker (periodic request).
                val workRequest = PeriodicWorkRequest.Builder(CoroutineNewsWorker::class.java, 30, TimeUnit.MINUTES)
                    //.setInputData(workerData)
                    .build()
                WorkManager.getInstance(context).enqueue(
                    workRequest
                )
                if (newsItems.isNullOrEmpty()) {
                    val workRequest = OneTimeWorkRequest.Builder(NewsWorker::class.java)
                        .setInputData(workerData)
                        .build()
                    WorkManager.getInstance(context).enqueue(
                        workRequest
                    )
                }
                itemsIndexed(newsItems ?: listOf()) { index, newsItem ->
                    if (index == 0)
                        NewsItemFirstRow(
                            navController = navController,
                            index = index,
                            newsItem = newsItem,
                            viewModel = viewModel
                        )
                    else
                        NewsItemRow(
                            navController = navController,
                            index = index,
                            newsItem = newsItem,
                            viewModel = viewModel
                        )
                }
            }
        }
    })
}
