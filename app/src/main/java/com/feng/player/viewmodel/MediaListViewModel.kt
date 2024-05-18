package com.feng.player.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.feng.player.empty.MediaData

class MediaListViewModel : ViewModel() {
    var list by mutableStateOf<List<MediaData>>(listOf())

    fun fetchData() {
        list = listOf(
            MediaData("title1", "desc1", "url1", "img1"),
            MediaData("title2", "desc2", "url2", "img2"),
            MediaData("title3", "desc3", "url3", "img3"),
        )
    }
}