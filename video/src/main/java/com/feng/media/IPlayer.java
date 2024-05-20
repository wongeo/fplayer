package com.feng.media;

import android.content.Context;
import android.view.Surface;

public interface IPlayer {

    void play(String uri, int point);

    void setPlayStateCallback(IPlayStateCallback callback);

    State getState();

    void start();

    void pause();

    void stop();

    void setDisplay(Surface surface);

    void seekTo(int value);

    void setSpeed(float speed);

    long getCurrentPosition();

    static IPlayer create(Context context, Class<? extends IPlayer> classz) {
        return new EPlayer(context);
    }
}
