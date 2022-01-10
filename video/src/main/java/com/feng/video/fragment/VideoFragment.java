package com.feng.video.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.feng.mvp.BaseFragment;

/**
 * Created by 巫鸦 on 2017/11/9.
 */

public class VideoFragment extends BaseFragment {
    private VideoView mVideoView;
    private MediaController mMediaController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mVideoView == null) {
            mVideoView = new VideoView(getActivity());
            initViews();
        }
        return mVideoView;
    }

    private void initViews() {
        Uri uri = Uri.parse("http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4");
        mMediaController = new MediaController(getActivity());
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.requestFocus();
    }
}
