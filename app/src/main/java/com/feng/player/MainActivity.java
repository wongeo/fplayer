package com.feng.player;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.feng.mvp.BaseActivity;
import com.feng.video.fragment.CustomVideoFragment;

public class MainActivity extends BaseActivity {
    public static final int PERMISSION_REQUEST = 2000;
    public static final int REQUEST_CODE_SETTING = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllPermissions();
        startFragment(new CustomVideoFragment());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                boolean isSecondRequest = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (isSecondRequest) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, PERMISSION_REQUEST);
                } else {
                    this.showSettingDialog();
                }
            }
        }

    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permission:");
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.setPositiveButton("设置", (dialog, which) -> {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
            MainActivity.this.startActivity(intent);
        });
        builder.create().show();
    }

    public void requestAllPermissions() {
        String[] permissions = new String[]{"android.permission.INTERNET", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"};
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == -1) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
                return;
            }
        }
    }
}