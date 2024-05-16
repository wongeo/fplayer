package com.feng.video.fragment;

import android.os.Bundle;
import android.view.View;

import com.feng.media.IPlayStateCallback;
import com.feng.media.PlayInfo;
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
        mPlayer = mView.getPlayer();
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
            switch (newState) {
                case PLAYING:
                    mView.onPlayerStart();
                    break;
                case PAUSE:
                    mView.onPlayerPause();
                    break;
            }
        }

        @Override
        public void onPrepared(PlayInfo playInfo) {
            mView.onPrepared(playInfo);
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

    public void startOrPause() {
        if (mPlayer.getState() == State.PLAYING) {
            mPlayer.pause();
        } else if (mPlayer.getState() == State.PAUSE) {
            mPlayer.start();
        }
    }

    public void seekTo(int value) {
        mPlayer.seekTo(value);
    }

    public void showNetPlayList() {

    }
}
