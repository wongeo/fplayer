package com.feng.player.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feng.player.PlayerActivity
import com.feng.player.empty.MediaData
import com.feng.player.viewmodel.MediaListViewModel

@Composable
fun MediaList(listViewModel: MediaListViewModel = viewModel()) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        listViewModel.fetchData(context)
    }

    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxHeight(),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // 设置元素之间的水平间距
    ) {
        items(listViewModel.list) { item ->
            MediaCard(item)
        }
    }
}

@Composable
fun MediaCard(data: MediaData) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("url", data.url)
            launcher.launch(intent)
        }) {
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