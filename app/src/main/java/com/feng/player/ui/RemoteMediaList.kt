package com.feng.player.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feng.player.viewmodel.MediaListState
import com.feng.player.viewmodel.RemoteMediaListViewModel

@Composable
fun RemoteMediaList() {
    val context = LocalContext.current
    val viewModel: RemoteMediaListViewModel = viewModel()
    val list = viewModel.list
    val state = viewModel.state
    if (state != MediaListState.Success) {
        LaunchedEffect(Unit) {
            viewModel.fetchData(context)
        }
    }
    when (state) {
        MediaListState.Loading -> Text(text = "加载中")
        MediaListState.Success -> MediaList(list)
        MediaListState.Error -> Text(text = "数据加载失败")
    }
}