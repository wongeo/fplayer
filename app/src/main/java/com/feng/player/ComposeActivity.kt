package com.feng.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.feng.player.ui.MediaList

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldExample()
        }
    }
}

@Composable
fun MyComposeUI() {
    Button(onClick = {}) {
        Text("Hello, Compose!")
    }
}

@Composable
fun ScaffoldExample() {
    Scaffold(
        topBar = {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "fjeijfie")
            }
        },
    ) {
        MediaList()
    }
}


// 用于预览的Composable
@Preview
@Composable
fun PreviewScaffoldExample() {
    ScaffoldExample()
}