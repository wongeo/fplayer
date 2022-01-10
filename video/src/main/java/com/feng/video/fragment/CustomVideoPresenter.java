package com.feng.video.fragment;

import android.os.Bundle;
import android.view.View;

import com.feng.media.IPlayStateCallback;
import com.feng.media.Player;
import com.feng.media.State;
import com.feng.mvp.BasePresenter;

/**
 * Created by 巫鸦 on 2017/11/10.
 */

public class CustomVideoPresenter extends BasePresenter<CustomVideoFragment> {

    private Player mPlayer;

    public CustomVideoPresenter(CustomVideoFragment view) {
        super(view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPlayer = mView.mPlayerView.getPlayer();
        mPlayer.setPlayStateCallback(mPlayStateCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }

    private final IPlayStateCallback mPlayStateCallback = new IPlayStateCallback() {
        @Override
        public void onMediaError(Exception ex) {

        }

        @Override
        public void onBufferingProgressChanged(int percent) {
            mView.onBufferingProgressChanged(percent);
        }

        @Override
        public void onVideoSizeChange(int width, int height) {
            mView.refreshVideoSize(width, height);
        }

        @Override
        public void onPlayerStateChanged(State oldState, State newState) {

        }

        @Override
        public void onPrepared(int duration) {
            mView.onPrepared(duration);
        }

        @Override
        public void onPlayPositionChanged(float percent, long position, long duration) {
            mView.onPlayPositionChanged((int) position);
        }

        @Override
        public void onCompletion() {

        }
    };

    public void playWithUri(String uri) {
        mPlayer.play(uri, 0);
    }

    public void pause() {
        mPlayer.pause();
    }

    public void start() {
        mPlayer.start();
    }

    public void seekTo(int value) {
        mPlayer.seekTo(value);
    }

    public void showNetPlayList() {

    }
}
