package com.feng.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SeekPanel extends FrameLayout {
    public static final String TAG = "SeekPanel";
    private int mMaxProgress = 100;
    private int mProgress = 0;
    private OnSeekPanelListener mOnSeekPanelListener;
    private final GestureDetector mGestureDetector;
    private final SimpleExtOnGestureListener simpleExtOnGestureListener;
    private SeekBar mSeekBar;

    public SeekPanel(@NonNull Context context) {
        super(context);
        simpleExtOnGestureListener = new SimpleExtOnGestureListener();
        mGestureDetector = new GestureDetector(context, simpleExtOnGestureListener);
    }

    public SeekPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        simpleExtOnGestureListener = new SimpleExtOnGestureListener();
        mGestureDetector = new GestureDetector(context, simpleExtOnGestureListener);
    }

    public SeekPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        simpleExtOnGestureListener = new SimpleExtOnGestureListener();
        mGestureDetector = new GestureDetector(context, simpleExtOnGestureListener);
    }

    public void setSeekBar(SeekBar seekBar) {
        this.mSeekBar = seekBar;
    }

    public void setProgress(int value) {
        this.mProgress = value;
    }

    public void setMaxProgress(int value) {
        this.mMaxProgress = value;
    }

    public void setOnSeekPanelListener(OnSeekPanelListener listener) {
        this.mOnSeekPanelListener = listener;
    }

    public int getProgress() {
        return mProgress;
    }

    public interface OnSeekPanelListener {

        void onProgressChanged(SeekPanel seekPanel, int progress);

        void onStartTrackingTouch(SeekPanel seekPanel);

        void onStopTrackingTouch(SeekPanel seekPanel);

    }

    private @interface TouchRect {
        int LEFT = 1;
        int CENTER = 2;
        int RIGHT = 3;
    }


    private class SimpleExtOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private @TouchRect int touchRect(MotionEvent e) {
            int width = SeekPanel.this.getWidth();
            float tw = width / 3f;
            if (e.getX() < tw) {
                return TouchRect.LEFT;
            } else if (e.getX() > tw && e.getX() < width - tw) {
                return TouchRect.CENTER;
            } else if (e.getX() > width - tw) {
                return TouchRect.RIGHT;
            }
            return 0;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击事件
            int touchRect = touchRect(e);
            switch (touchRect) {
                case TouchRect.LEFT:
                    mProgress = mProgress - 10000;
                    mOnSeekPanelListener.onStopTrackingTouch(SeekPanel.this);
                    break;
                case TouchRect.CENTER:
                    break;
                case TouchRect.RIGHT:
                    mProgress = mProgress + 10000;
                    mOnSeekPanelListener.onStopTrackingTouch(SeekPanel.this);
                    break;
            }
            return true;
        }

        private int tempProgress;

        public void onUp(MotionEvent e) {
            if (isScrolling) {
                mOnSeekPanelListener.onStopTrackingTouch(SeekPanel.this);
                isScrolling = false;
            }
        }

        private boolean isScrolling = false;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isScrolling) {
                tempProgress = mProgress;
                mOnSeekPanelListener.onStartTrackingTouch(SeekPanel.this);
            }
            isScrolling = true;
            float dx = e2.getX() - e1.getX();
            Log.d(TAG, "onScroll dx=" + dx);
            int width = getWidth();
            int diffProgress = (int) (mMaxProgress * (dx / width));
            Log.d(TAG, "onScroll diffProgress=" + diffProgress);
            int progress = tempProgress + diffProgress;
            Log.d(TAG, "onScroll progress=" + progress);
            mProgress = progress;
            mOnSeekPanelListener.onProgressChanged(SeekPanel.this, progress);
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        public boolean isScrolling() {
            return isScrolling;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            simpleExtOnGestureListener.onUp(event);
        }
        return true;
    }
}
