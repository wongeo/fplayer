package com.feng.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.feng.player.empty.MediaData

@Composable
fun MediaList(list: List<MediaData>) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxHeight(),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // 设置元素之间的水平间距
    ) {
        items(list) { item ->
            MediaCard(item)
        }
    }
}