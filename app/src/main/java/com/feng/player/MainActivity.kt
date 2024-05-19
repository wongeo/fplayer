package com.feng.player

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.feng.player.ui.MainContent
import com.feng.player.ui.theme.PlayerTheme

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE: Int = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        setContent {
            PlayerTheme {
                MainContent()
            }
        }
    }

    private val permissions = arrayOf(
        "android.permission.INTERNET", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"
    )

    /**
     * 检查并请求权限
     */
    private fun checkAndRequestPermissions() {
        val allPermissionsGranted = permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allPermissionsGranted) {
            // 所有权限都已获得，可以正常使用
            Toast.makeText(this, "所有权限已获得", Toast.LENGTH_SHORT).show()
        } else {
            // 至少有一个权限未被授予，请求权限
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission)
                }
            }
            if (deniedPermissions.isEmpty()) {
                // 所有权限都被用户接受
                Toast.makeText(this, "所有权限已接受", Toast.LENGTH_SHORT).show()
            } else {
                // 有权限被用户拒绝
                val message = "以下权限被拒绝：\n${deniedPermissions.joinToString(", ")}"
                showSettingDialog(message)
            }
        }
    }

    private val settingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        checkAndRequestPermissions()
    }

    private fun showSettingDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("注意！！！")
            .setMessage(message)
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

