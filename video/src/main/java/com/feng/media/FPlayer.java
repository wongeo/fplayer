package com.feng.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

public class FPlayer implements Player {

    private State mState = State.STOP;
    private IPlayStateCallback mPlayStateCallback;

    private final MediaPlayer mMediaPlayer;
    private int mProgress;
    private Handler mHandler;
    private float mSpeed;

    public FPlayer(Context context) {
        mMediaPlayer = new MediaPlayer();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                FPlayer.this.handleMessage(msg);
            }
        };

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayStateCallback.onCompletion();
                onStateChange(State.STOP);
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mState = State.PREPARED;
                if (mPlayStateCallback != null) {
                    mPlayStateCallback.onPrepared(mp.getDuration());
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
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Exception exception = new Exception("what:" + what + " extra:" + extra);
                mPlayStateCallback.onMediaError(exception);
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
                mHandler.sendMessageDelayed(next, 1000);
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
            e.printStackTrace();
            mPlayStateCallback.onMediaError(e);
        }
    }

    public void setPlayStateCallback(IPlayStateCallback playStateCallback) {
        mPlayStateCallback = playStateCallback;
    }

    public void seekTo(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        mMediaPlayer.seekTo(progress);
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
