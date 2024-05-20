package com.feng.player.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feng.player.viewmodel.LocalMediaListViewModel
import com.feng.player.viewmodel.MediaListState

@Composable
fun LocalMediaList() {
    val context = LocalContext.current
    val viewModel: LocalMediaListViewModel = viewModel()
    val list = viewModel.list
    val state = viewModel.state
    LaunchedEffect(Unit) {
        viewModel.fetchData(context, false)
    }
    when (state) {
        MediaListState.Loading -> Text(text = "加载中")
        MediaListState.Success -> MediaList(list)
        MediaListState.Error -> Text(text = "数据加载失败")
    }
}