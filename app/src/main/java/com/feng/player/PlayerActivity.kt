package com.feng.player

import android.os.Bundle
import android.view.TextureView
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.feng.media.FPlayer
import com.feng.resize.ResizeView

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayerContent()
        }
    }
}

@Composable
fun PlayerContent() {
    val playerView = AndroidView(factory = { context ->
        ResizeView(context).apply {
            // 可以在这里设置FrameLayout的属性
            val player = FPlayer(context)
            val videoView: View = TextureView(context)
            bind(player, videoView)
        }
    })
    Column {
        playerView
    }
}

