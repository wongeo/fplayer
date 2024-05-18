package com.feng.player

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.feng.player.ui.MediaList

class ComposeActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST: Int = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAllPermissions()
        setContent {
            ScaffoldExample()
        }
    }

    private fun requestAllPermissions() {
        val permissions = arrayOf("android.permission.INTERNET", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE")
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == -1) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
                return
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.filter { it == PackageManager.PERMISSION_DENIED }.forEach {
            val isSecondRequest = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
            if (isSecondRequest) {
                ActivityCompat.requestPermissions(this, arrayOf("android.permission.RECORD_AUDIO"), PERMISSION_REQUEST)
            } else {
                showSettingDialog()
            }
        }
    }

    private val settingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            // 处理选择的图片Uri，例如上传到服务器或显示在ImageView中
            // 这里只是一个示例，实际操作根据需求进行
            println("Selected Image Uri: $imageUri")
        }
    }

    private fun showSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle("注意！！！")
            .setMessage("需要设置权限")
            .setNegativeButton("取消") { _, _ -> finish() }
            .setPositiveButton("设置") { dialog: DialogInterface, _ ->
                //打开设置页面授权
                val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                intent.setData(Uri.parse("package:$packageName"))
                settingLauncher.launch(intent)
                dialog.dismiss()
            }.create().show()
    }
}

@Composable
fun ScaffoldExample() {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Top app bar")
            })
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