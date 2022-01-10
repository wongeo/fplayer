package com.feng.video.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

/**
 * new texture view to display video.
 */

public class NewTextureView extends TextureView {
    public static final int VIDEO_PERCENT_50 = 50;
    public static final int VIDEO_PERCENT_75 = 75;
    public static final int VIDEO_PERCENT_100 = 100;
    public static final int VIDEO_PERCENT_FULL = -1;
    private boolean mIsFullScreen;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mViewPercent = VIDEO_PERCENT_100;
    private int mParentWidth;
    private int mParentHeight;
    private int mOrientation;
    private LayoutChangeListener mLayoutChangeListener;
    private static String TAG = NewTextureView.class.getSimpleName();

    // 当视频比例与view比例小于0.01时不再进行resize
    private static final float RESIZE_RATE = 0.01f;

    public NewTextureView(Context context) {
        super(context);
    }

    public NewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoSize(int w, int h) {
        mVideoWidth = w;
        mVideoHeight = h;
        requestLayout();
    }

    public void setParentSize(int width, int height) {
        mParentWidth = width;
        mParentHeight = height;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setViewPercent(int percent) {
        mViewPercent = percent;
        requestLayout();
    }

    public void setIsFullScreen(boolean isFullScreen) {
        mIsFullScreen = isFullScreen;
    }

    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        int videoWidth = mVideoWidth;
        int videoHeight = mVideoHeight;
        if (mOrientation == 1 || mOrientation == 2) {
            videoHeight = mVideoWidth;
            videoWidth = mVideoHeight;
        }
        if (!(mViewPercent == VIDEO_PERCENT_FULL)) {
            if (videoWidth > 0 && videoHeight > 0) {
                int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
                width = widthSpecSize;
                height = heightSpecSize;
                if (isFullScreen()) {
                    width = mParentWidth;
                    height = mParentHeight;
                    if (mViewPercent == VIDEO_PERCENT_75) {
                        width = width / 4 * 3;
                        height = height / 4 * 3;
                    } else if (mViewPercent == VIDEO_PERCENT_50) {
                        width = width / 2;
                        height = height / 2;
                    }
                }

                if (Math.abs(videoWidth * height - videoHeight * width) > RESIZE_RATE * height * videoHeight) {
                    if (videoWidth * height < videoHeight * width) {
                        width = videoWidth * height / videoHeight;
                    } else if (videoWidth * height > videoHeight * width) {
                        height = videoHeight * width / videoWidth;
                    }
                    // 奇数宽高在有些设备上会造成只显示半屏
                    if (width % 2 == 1) {
                        width -= 1;
                    }

                    if (height % 2 == 1) {
                        height -= 1;
                    }
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    protected static String sizeToString(int size) {
        if (size == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return "wrap-content";
        }
        if (size == ViewGroup.LayoutParams.MATCH_PARENT) {
            return "match-parent";
        }
        return String.valueOf(size);
    }

    public void recreateSurfaceHolder() {
        setVisibility(View.INVISIBLE);
        setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mLayoutChangeListener != null) {
            mLayoutChangeListener.onLayoutChange();
        }
    }

    public void setLayoutChangeListener(LayoutChangeListener listener) {
        mLayoutChangeListener = listener;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public interface LayoutChangeListener {
        void onLayoutChange();
    }

    private SurfaceTexture mSurfaceTexture;

    public void setCacheSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getSurfaceTexture() != mSurfaceTexture) {
            setSurfaceTexture(mSurfaceTexture);
        }
    }
}
