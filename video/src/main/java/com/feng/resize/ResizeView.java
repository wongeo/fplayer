package com.feng.resize;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feng.media.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResizeView extends FrameLayout implements IResize {
    public static final String TAG = "ResizeView";

    private Surface mSurface;
    private View mVideoView;
    private Player mPlayer;

    private float mTargetAspectRatio;//视频宽高比
    private float mParentAspectRatio;//容器宽高比

    private int mParentWidth;
    private int mParentHeight;
    private int mLastWidth, mLastHeight;
    private int mVideoCurMode = VIEW_CUT_MODE_NONE;

    private final List<OnResizeListener> mOnResizeListeners = new CopyOnWriteArrayList<>();
    private SurfaceTextureListener mVideoTextureListener;

    public ResizeView(@NonNull Context context) {
        super(context);
    }

    public ResizeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(@NonNull Player player, @NonNull View view) {
        mPlayer = player;
        mVideoView = view;
        mVideoView.setKeepScreenOn(true);
        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.getHolder().addCallback(mSurfaceCallback);
        } else if (view instanceof TextureView) {
            TextureView textureView = (TextureView) view;
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
            SurfaceTexture surface = textureView.getSurfaceTexture();
            if (surface != null) {
                mSurface = new Surface(surface);
                if (mPlayer != null) {
                    mPlayer.setDisplay(mSurface);
                }
            }
        }

        mVideoView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int width = right - left;
                int height = bottom - top;
                if (width != 0 && height != 0 && (width != mLastWidth || height != mLastHeight)) {
                    mLastWidth = width;
                    mLastHeight = height;
                    if (mOnResizeListeners.size() > 0) {
                        for (OnResizeListener listener : mOnResizeListeners) {
                            listener.onSizeChanged(width, height);
                        }
                    }
                }
            }
        });

        removeAllViews();
        addView(mVideoView);
        setBackgroundColor(Color.BLACK);
    }

    @Override
    public void addView(View child) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        super.addView(child, layoutParams);
    }

    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurface = holder.getSurface();
            if (mPlayer != null) {
                mPlayer.setDisplay(mSurface);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mSurface != null) {
                mSurface.release();
            }
        }
    };

    private final SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.i("lwj", "surface texturae available " + mPlayer);
            mSurface = new Surface(surface);
            if (mPlayer != null) {
                mPlayer.setDisplay(mSurface);
            }
            if (mVideoTextureListener != null) {
                mVideoTextureListener.onSurfaceTextureAvailable(surface, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (mVideoTextureListener != null) {
                mVideoTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (mSurface != null) {
                mSurface.release();
            }
            if (mVideoTextureListener != null) {
                mVideoTextureListener.onSurfaceTextureDestroyed(surface);
            }
            mSurface = null;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (mVideoTextureListener != null) {
                mVideoTextureListener.onSurfaceTextureUpdated(surface);
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        if (changed && width > 0 && height > 0) {
            mParentWidth = width;
            mParentHeight = height;
            mParentAspectRatio = width * 1F / height;
            resize();
        }
    }

    public void resize() {
        if (mParentWidth == 0 || mParentHeight == 0 || mTargetAspectRatio == 0 || mParentAspectRatio == 0) {
            return;
        }

        int resizeWidth = mParentWidth;
        int resizeHeight = mParentHeight;

        switch (mVideoCurMode) {
            case VIEW_CUT_MODE_NONE:
                if (mParentAspectRatio > mTargetAspectRatio) {//长屏幕手机，两边留黑边
                    resizeWidth = Math.round(resizeHeight * mTargetAspectRatio);
                } else {
                    resizeHeight = Math.round(resizeWidth / mTargetAspectRatio);
                }
                break;
            case VIEW_CUT_MODE_FULL:
                if (mParentAspectRatio > mTargetAspectRatio) {
                    resizeHeight = (int) (resizeWidth / mTargetAspectRatio);
                } else {
                    resizeWidth = (int) (resizeHeight * mTargetAspectRatio);
                }
                break;
            case VIEW_CUT_MODE_FITXY:
                break;
        }

        // 奇数宽高在有些设备上会造成只显示半屏
        if (resizeWidth % 2 == 1) {
            resizeWidth -= 1;
        }
        if (resizeHeight % 2 == 1) {
            resizeHeight -= 1;
        }
        mVideoView.getLayoutParams().width = resizeWidth;
        mVideoView.getLayoutParams().height = resizeHeight;
        mVideoView.requestLayout();
    }

    public View getVideoView() {
        return mVideoView;
    }

    public void refreshVideoSize(int width, int height) {
        if (width > 0 && height > 0) {
            setAspectRatio(width * 1f / height);
        }
    }

    private void setAspectRatio(float widthHeightRatio) {
        if (this.mTargetAspectRatio != widthHeightRatio) {
            this.mTargetAspectRatio = widthHeightRatio;
            resizeOnUiThread();
        }
    }

    @Override
    public void setVideoCutMode(int mode) {

        mVideoCurMode = mode;
        resizeOnUiThread();
    }

    @Override
    public void addOnResizeListener(OnResizeListener listener) {
        mOnResizeListeners.add(listener);
    }

    private void resizeOnUiThread() {
        post(new Runnable() {
            @Override
            public void run() {
                resize();
            }
        });
    }

    public void setVideoTextureListener(SurfaceTextureListener textureListener) {
        mVideoTextureListener = textureListener;
    }
}
