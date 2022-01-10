package com.feng.video.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feng.media.FPlayer;
import com.feng.media.IPlayStateCallback;
import com.feng.media.Player;
import com.feng.media.State;
import com.feng.resize.ResizeView;

/**
 * Created by feng on 2017/11/9.
 */

public class CustomVideoView extends FrameLayout {

    private MoveableTextureView mTextureView;

    private Player mPlayer;

    public CustomVideoView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(new ColorDrawable(Color.BLACK));
        }
        initMediaPlayer();
        initTextureView();
    }

    private ResizeView mResizeView;

    private void initTextureView() {
        mResizeView = new ResizeView(getContext());
        mTextureView = new MoveableTextureView(getContext());
        mResizeView.bind(mPlayer, mTextureView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mResizeView, params);
    }

    /**
     * 设置播放View 尺寸
     *
     * @param width
     * @param height
     */
    public void rebuildSize(int width, int height) {
        ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mTextureView.setLayoutParams(lp);
    }

    public void setPlayStateCallback(IPlayStateCallback callback) {
        mPlayer.setPlayStateCallback(callback);
    }

    private void initMediaPlayer() {
        mPlayer = new FPlayer(getContext());
    }

    public void refreshVideoSize(int width, int height) {
        mResizeView.refreshVideoSize(width, height);
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public State getState() {
        return mPlayer.getState();
    }

    public void play(String s, int i) {
        mPlayer.play(s, i);
    }

    public void start() {
        mPlayer.start();
    }

    public void pause() {
        mPlayer.pause();
    }
}
