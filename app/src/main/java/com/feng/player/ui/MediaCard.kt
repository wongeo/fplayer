package com.feng.player.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.feng.player.PlayerActivity
import com.feng.player.empty.MediaData

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