package com.feng.player.viewmodel

import android.content.Context
import android.widget.Toast
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

    var list by mutableStateOf<List<MediaData>>(
        listOf(
            MediaData("title1", "desc1", "url1", "img1"),
            MediaData("title2", "desc2", "url2", "img2"),
            MediaData("title3", "desc3", "url3", "img3"),
            MediaData("title4", "desc4", "url4", "img4"),
            MediaData("title5", "desc5", "url5", "img5"),
            MediaData("title6", "desc6", "url6", "img6"),
            MediaData("title7", "desc7", "url7", "img7"),
            MediaData("title8", "desc8", "url8", "img8"),
            MediaData("title9", "desc9", "url9", "img9"),
            MediaData("title10", "desc10", "url10", "img10"),
            MediaData("title11", "desc11", "url11", "img11"),
        )
    )

    @OptIn(DelicateCoroutinesApi::class)
    open fun fetchData(context: Context, remote: Boolean = true) {
        GlobalScope.launch(Dispatchers.IO) {
            state = MediaListState.Loading
//            val items = getLocalFiles(context)
            val items = if (remote)  getDataFromNet(context) else getLocalFiles(context)
//            val items = LocalDataSource.getLocalFiles(context)
//        val items = NetDataSource.getItems(context, "192.168.1.2");
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
                    Toast.makeText(context, "数据为空", Toast.LENGTH_LONG).show()
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