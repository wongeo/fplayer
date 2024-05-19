package com.feng.player.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.feng.player.ui.theme.PlayerTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent() {
    val pagerState = rememberPagerState()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Top app bar")
        })
    }, content = {
        Content(pagerState)
    }, bottomBar = {
        val scope = rememberCoroutineScope()
        BottomBar(pagerState.currentPage) {
            //点击页签后在协程中滚动到指定页
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        }
    });
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(pagerState: PagerState = rememberPagerState()) {
    HorizontalPager(
        pageCount = 2, state = pagerState
    ) { page ->
        when (page) {
            0 -> MediaList()
            else -> UserCenter()
        }
    }
}

@Composable
fun BottomBar(selected: Int = 0, onSelectedChanged: (Int) -> Unit) {
    BottomNavigation() {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, "首页") },
            label = { Text(text = "首页") },
            selected = selected == 0,
            onClick = {
                onSelectedChanged(0)
            })
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Person, "个人中心") },
            label = { Text(text = "个人中心") },
            selected = selected == 1,
            onClick = {
                onSelectedChanged(1)
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