package com.feng.media;

public interface IPlayStateCallback {


    void onMediaError(ErrorInfo ex);

    void onBufferingProgressChanged(int percent);

    void onVideoSizeChange(int width, int height);

    void onPlayerStateChanged(State oldState, State newState);

    void onPrepared(PlayInfo playInfo);

    void onPlayPositionChanged(float percent, long position, long duration);

    void onCompletion();
}
