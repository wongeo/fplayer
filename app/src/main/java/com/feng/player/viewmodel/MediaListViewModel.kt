package com.feng.player.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.feng.player.ui.ItemData

class MediaListViewModel : ViewModel() {
    var list by mutableStateOf<List<ItemData>>(listOf())

    fun fetchData() {
        list = listOf(
            ItemData("title1", "desc1", "url1", "img1"),
            ItemData("title2", "desc2", "url2", "img2"),
            ItemData("title3", "desc3", "url3", "img3"),
        )
    }
}