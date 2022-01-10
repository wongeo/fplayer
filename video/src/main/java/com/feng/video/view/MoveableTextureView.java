package com.feng.video.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

public class MoveableTextureView extends TextureView {

    private SurfaceTexture mSurfaceTexture;

    public MoveableTextureView(Context context) {
        super(context);
    }

    public MoveableTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveableTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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
