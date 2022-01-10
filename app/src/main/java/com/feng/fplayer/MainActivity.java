package com.feng.fplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.feng.activity.PermissionCompatActivity;
import com.feng.mvp.BaseFragment;
import com.feng.video.fragment.CustomVideoFragment;

public class MainActivity extends PermissionCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startFragment(new CustomVideoFragment());
    }
}