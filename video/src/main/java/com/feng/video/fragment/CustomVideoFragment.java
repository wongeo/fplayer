package com.feng.video.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.feng.media.Player;
import com.feng.mvp.BaseFragment;
import com.feng.video.R;
import com.feng.video.adapter.FileAdapter;
import com.feng.video.adapter.Item;
import com.feng.video.db.LocalDataSource;
import com.feng.video.db.NetDataSource;
import com.feng.video.view.CustomVideoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by 巫鸦 on 2017/11/9.
 */

public class CustomVideoFragment extends BaseFragment<CustomVideoPresenter> implements View.OnTouchListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private CustomVideoPresenter mPresenter;
    public CustomVideoView mPlayerView;
    private Player mPlayer;
    private View mRootView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    //播放器父容器
    private FrameLayout mSmallContainer, mFullContainer;
    private SeekBar mSeekBar;

    public CustomVideoFragment() {
        mPresenter = new CustomVideoPresenter(this);
        setPresenter(mPresenter);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.custom_video_fragment2, container, false);
            //播放器父容器
            mSmallContainer = mRootView.findViewById(R.id.small_screen_player_view_container);
            mFullContainer = mRootView.findViewById(R.id.full_screen_player_view_container);

            mPlayerView = new CustomVideoView(getActivity());
            mPlayerView.setOnClickListener(this);
            mPlayer = mPlayerView.getPlayer();

            //播放器控制按钮
            mRootView.findViewById(R.id.play_url).setOnClickListener(this);
            mRootView.findViewById(R.id.play_path).setOnClickListener(this);
            mRootView.findViewById(R.id.play_json).setOnClickListener(this);
            mRootView.findViewById(R.id.start_button).setOnClickListener(this);
            mRootView.findViewById(R.id.pause_button).setOnClickListener(this);
            mRootView.findViewById(R.id.jingyin).setOnClickListener(this);
            mRootView.findViewById(R.id.full).setOnClickListener(this);

            mSeekBar = new SeekBar(getActivity());
            mSeekBar.setVisibility(View.INVISIBLE);
            mSeekBar.setOnSeekBarChangeListener(this);
            refreshPlayerView(false);
            showControlBar();
        }
        return mRootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private boolean isControlBarShowing() {
        return mSeekBar.getVisibility() == View.VISIBLE;
    }

    private void showControlBar() {
        mHandler.removeCallbacksAndMessages(null);
        mSeekBar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }

    private void hideControlBar() {
        mHandler.removeCallbacks(null);
        mSeekBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_url) {
            String url = "https://cloud.video.taobao.com/play/u/1745440806/p/1/d/hd/e/6/t/1/50240790066.mp4";
            mPresenter.playWithUri(url);
        } else if (id == R.id.play_path) {
            dialogList(true);
        } else if (id == R.id.play_json) {
            dialogList(false);
        } else if (id == R.id.pause_button) {
            mPresenter.pause();
        } else if (id == R.id.start_button) {
            mPresenter.start();
        } else if (id == R.id.full) {
            goFullScreen();
        } else if (mPlayerView == v) {
            if (isControlBarShowing()) {
                hideControlBar();
            } else {
                showControlBar();
            }
        }
    }

    public void goFullScreen() {
        Activity activity = getActivity();
        assert activity != null;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void goSmallScreen() {
        Activity activity = getActivity();
        assert activity != null;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void refreshPlayerView(boolean isFull) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, this.getResources().getDisplayMetrics());
        layoutParams.bottomMargin = (int) pixel;

        if (isFull) {
            mSmallContainer.removeAllViews();
            mFullContainer.removeAllViews();
            mFullContainer.addView(mPlayerView);
            mFullContainer.addView(mSeekBar, layoutParams);
        } else {
            mSmallContainer.removeAllViews();
            mFullContainer.removeAllViews();
            mSmallContainer.addView(mPlayerView);
            mSmallContainer.addView(mSeekBar, layoutParams);
        }
    }

    public boolean isFull() {
        return mFullContainer.getChildCount() > 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            refreshPlayerView(true);
        } else {
            refreshPlayerView(false);
        }
    }

    @Override
    public boolean onBackPress() {
        if (isFull()) {
            goSmallScreen();
            return true;
        }
        return super.onBackPress();
    }

    /**
     * 播放本地视频
     */
    private void dialogList(boolean isLocal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 0);
        builder.setTitle("视频列表");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Item> items;
                if (isLocal) {
                    items = LocalDataSource.getLocalFiles(getActivity());
                } else {
                    items = NetDataSource.getItems(getActivity());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items == null || items.size() == 0) {
                            Toast.makeText(getActivity(), "存储卡根目录没有视频文件", Toast.LENGTH_LONG).show();
                            return;
                        }
                        builder.setAdapter(new FileAdapter(getActivity(), items), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Item file = items.get(which);
                                mPresenter.playWithUri(file.getUri());
                            }
                        });
                        builder.create().show();
                    }
                });
            }
        }).start();

    }

    private void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    public void refreshVideoSize(int width, int height) {
        mPlayerView.refreshVideoSize(width, height);
    }

    public void onPlayPositionChanged(int position) {
        if (!mIsSeekTouch) {
            mSeekBar.setProgress(position);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    private boolean mIsSeekTouch;

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsSeekTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsSeekTouch = false;
        int value = seekBar.getProgress();
        mPresenter.seekTo(value);
    }

    public void onPrepared(int duration) {
        mSeekBar.setMax(duration);
    }

    public void onBufferingProgressChanged(int percent) {
        mSeekBar.setSecondaryProgress(percent);
    }
}
