package com.feng.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class YourActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeUI()
        }
    }
}

@Composable
fun MyComposeUI() {
    Button(onClick = {}) {
        Text("Hello, Compose!")
    }
}

// 用于预览的Composable
@Preview
@Composable
fun PreviewMyComposeUI() {
    MyComposeUI()
}