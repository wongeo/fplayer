package com.feng.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.RequiresApi;

public class FPlayer implements IPlayer {
    public static final String TAG = "FPlayer";
    private State mState = State.STOP;
    private IPlayStateCallback mPlayStateCallback;

    private final MediaPlayer mMediaPlayer;
    private int mProgress;
    private final Handler mHandler;
    private float mSpeed;

    public FPlayer(Context context) {
        mMediaPlayer = new MediaPlayer();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                FPlayer.this.handleMessage(msg);
            }
        };

        mMediaPlayer.setOnCompletionListener(mp -> {
            mPlayStateCallback.onCompletion();
            onStateChange(State.STOP);
        });

        mMediaPlayer.setOnPreparedListener(mp -> {
            mState = State.PREPARED;
            if (mPlayStateCallback != null) {
                PlayInfo playInfo = new PlayInfo(mp.getDuration());
                mPlayStateCallback.onPrepared(playInfo);
            }
            int width = mp.getVideoWidth();
            int height = mp.getVideoHeight();
            if (mPlayStateCallback != null) {
                mPlayStateCallback.onVideoSizeChange(width, height);
            }
            if (mProgress != 0) {
                mp.seekTo(mProgress);
            }
            start();
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mPlayStateCallback.onMediaError(new ErrorInfo(what, "what:" + what + " extra:" + extra));
                return false;
            }
        });
    }

    private interface MsgId {
        int POSITION_CHANGE = 0;

    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MsgId.POSITION_CHANGE:
                int position = mMediaPlayer.getCurrentPosition();
                int duration = mMediaPlayer.getDuration();
                if (mPlayStateCallback != null) {
                    mPlayStateCallback.onPlayPositionChanged(0, position, duration);
                }
                Message next = Message.obtain();
                mHandler.sendMessageDelayed(next, 500);
                break;
            default:
                break;
        }
    }

    @Override
    public void play(String path, int progress) {
        try {
            mProgress = progress;
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            onStateChange(State.PREPARING);
        } catch (Exception e) {
            mPlayStateCallback.onMediaError(new ErrorInfo(0, Log.getStackTraceString(e)));
        }
    }

    public void setPlayStateCallback(IPlayStateCallback playStateCallback) {
        mPlayStateCallback = playStateCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void seekTo(int progress) {
        Log.d(TAG, "seek " + progress);
        if (progress < 0) {
            progress = 0;
        }
        mMediaPlayer.seekTo(progress, MediaPlayer.SEEK_CLOSEST);
        mMediaPlayer.start();
    }

    @Override
    public void setSpeed(float speed) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (mSpeed != speed) {
                mSpeed = speed;
                PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
                playbackParams.setSpeed(speed);
                mMediaPlayer.setPlaybackParams(playbackParams);
            }
        }
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void start() {
        mMediaPlayer.start();
        onStateChange(State.PLAYING);
    }

    public void pause() {
        mMediaPlayer.pause();
        onStateChange(State.PAUSE);
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        onStateChange(State.STOP);
    }

    @Override
    public void setDisplay(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    public State getState() {
        return mState;
    }

    private void onStateChange(State dest) {
        State src = mState;
        onStateChange(src, dest);
        Message msg = Message.obtain();
        msg.what = MsgId.POSITION_CHANGE;
        mHandler.sendMessageDelayed(msg, 1000);
    }

    private void onStateChange(State src, State dest) {
        mState = dest;
        mPlayStateCallback.onPlayerStateChanged(src, dest);
    }
}
