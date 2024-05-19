package com.feng.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserCenter() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "fjiefjie")
    }
}


@Preview(showBackground = true)
@Composable
fun UserCenterPreview() {
    UserCenter()
}