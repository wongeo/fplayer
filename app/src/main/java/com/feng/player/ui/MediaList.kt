package com.feng.player.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feng.player.viewmodel.MediaListViewModel


@Composable
fun MediaList(listViewModel: MediaListViewModel = viewModel()) {
    LaunchedEffect(key1 = Unit) {
        listViewModel.fetchData()
    }
    val list = listOf(
        ItemData("title1", "desc1", "url1", "img1"),
        ItemData("title2", "desc2", "url2", "img2"),
        ItemData("title3", "desc3", "url3", "img3"),
    )
    //列表组件
    LazyColumn {
        items(list.size) {
            MediaCard(list[it])
        }
    }
}

data class ItemData(val title: String, val desc: String, val url: String, val img: String)

@Composable
fun MediaCard(data: ItemData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = data.title,
            modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreviewMediaList() {
    MediaList()
}