package com.feng.media;

import android.view.Surface;

public interface Player {

    void play(String uri, int point);

    void setPlayStateCallback(IPlayStateCallback callback);

    State getState();

    void start();

    void pause();

    void stop();

    void setDisplay(Surface surface);

    void seekTo(int value);

    void setSpeed(float speed);
}
