package com.feng.player.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.feng.player.db.getDataFromNet
import com.feng.player.db.getLocalFiles
import com.feng.player.empty.MediaData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MediaListViewModel : ViewModel() {
    var state by mutableStateOf(MediaListState.Loading)

    var list by mutableStateOf<List<MediaData>>(listOf())
        private set

    @OptIn(DelicateCoroutinesApi::class)
    open fun fetchData(context: Context, remote: Boolean = true) {
        GlobalScope.launch(Dispatchers.IO) {
            state = MediaListState.Loading
            val items = if (remote) getDataFromNet(context) else getLocalFiles(context)
            //排序
            items
                ?.sortedWith(compareBy { it.name })//排序
                ?.map { MediaData(it.name, it.name, it.path, "null") }?.let {
                    withContext(Dispatchers.Main) {
                        state = MediaListState.Success
                        list = it
                    }
                } ?: run {
                withContext(Dispatchers.Main) {
                    state = MediaListState.Error
                }
            }
        }
    }
}

enum class MediaListState {
    Loading,
    Success,
    Error
}