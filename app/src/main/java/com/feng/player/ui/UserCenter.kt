package com.feng.player.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feng.player.ui.theme.PlayerTheme
import com.feng.player.viewmodel.UserCenterViewModel

@Composable
fun UserCenter() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            val context: Context = LocalContext.current
            val viewModel: UserCenterViewModel = viewModel()
            var text by remember { mutableStateOf(viewModel.load(context)) }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                enabled = viewModel.enableInput,
                label = { Text("请输入IP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = {
                viewModel.submit(context, text)
                viewModel.enableInput = false
            }) {
                Text(text = "确定")
            }
        }
    }
}

@Preview
@Composable
fun UserCenterPreview() {
    PlayerTheme {
        UserCenter()
    }
}