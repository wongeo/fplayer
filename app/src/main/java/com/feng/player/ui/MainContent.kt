package com.feng.player.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.feng.player.ui.theme.PlayerTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    Scaffold(
        content = { innerPadding ->
            Content(pagerState, contentPadding = innerPadding)
        },
        bottomBar = {
            val scope = rememberCoroutineScope()
            BottomBar(pagerState.currentPage) {
                //点击页签后在协程中滚动到指定页
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(pagerState: PagerState, contentPadding: PaddingValues) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxHeight(),
        contentPadding = contentPadding
    ) { page ->
        when (page) {
            0 -> RemoteMediaList()
            1 -> LocalMediaList()
            else -> UserCenter()
        }
    }
}

@Composable
fun BottomBar(selected: Int = 0, onSelectedChanged: (Int) -> Unit) {
    NavigationBar() {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, "首页") },
            label = { Text(text = "首页") },
            alwaysShowLabel = false,
            selected = selected == 0,
            onClick = {
                onSelectedChanged(0)
            })
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, "个人中心") },
            label = { Text(text = "本地媒体") },
            alwaysShowLabel = false,
            selected = selected == 1,
            onClick = {
                onSelectedChanged(1)
            })
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, "个人中心") },
            label = { Text(text = "个人中心") },
            alwaysShowLabel = false,
            selected = selected == 2,
            onClick = {
                onSelectedChanged(2)
            })
    }
}

@Preview
@Composable
fun PreviewMainContent() {
    PlayerTheme(true) {
        MainContent()
    }
}