package com.feng.video.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.feng.media.FPlayer;
import com.feng.media.PlayInfo;
import com.feng.media.Player;
import com.feng.media.State;
import com.feng.mvp.BaseFragment;
import com.feng.resize.ResizeView;
import com.feng.util.DensityUtils;
import com.feng.video.R;
import com.feng.video.adapter.FileAdapter;
import com.feng.video.adapter.Item;
import com.feng.video.db.LocalDataSource;
import com.feng.video.db.NetDataSource;
import com.feng.video.util.DiscoverNetIpUtil;
import com.feng.video.util.SharedPreferencesUtil;
import com.feng.video.util.TimeUtil;
import com.feng.video.view.SeekPanel;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CustomVideoFragment extends BaseFragment<CustomVideoPresenter> implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, SeekPanel.OnSeekPanelListener {

    private final static String TAG = "CustomVideoFragment";
    private final CustomVideoPresenter mPresenter;
    public ResizeView mPlayerView;
    private Player mPlayer;
    private View mRootView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private FrameLayout mSmallContainer, mFullContainer, mPipContainer, mPlayerContainer;
    private SeekBar mSeekBar;
    private SeekPanel mSeekPanel;
    private EditText mAddressEdt;
    private String mAddress;
    private Button mStartOrPauseButton;
    private TextView mPositionTxt, mDurationTxt, progressTextView;

    public CustomVideoFragment() {
        mPresenter = new CustomVideoPresenter(this);
        setPresenter(mPresenter);
    }

    public Player getPlayer() {
        return mPlayer;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.custom_video_fragment2, container, false);
            View view = mRootView;
            //播放器父容器
            mSmallContainer = view.findViewById(R.id.small_screen_player_view_container);
            mFullContainer = view.findViewById(R.id.full_screen_player_view_container);
            mPipContainer = view.findViewById(R.id.pip_screen_player_view_container);
            mPlayerContainer = view.findViewById(R.id.player_container);

            //初始化播放器和surface并且进行绑定
            mPlayer = new FPlayer(getContext());
            mPlayerView = new ResizeView(requireContext());
            View videoView = new TextureView(requireContext());
            mPlayerView.bind(mPlayer, videoView);
            mPlayerContainer.addView(mPlayerView, 0);
            //播放器控制按钮
            view.findViewById(R.id.play_path).setOnClickListener(this);
            view.findViewById(R.id.play_json).setOnClickListener(this);
            mStartOrPauseButton = view.findViewById(R.id.start_or_pause_btn);
            mStartOrPauseButton.setOnClickListener(this);
            view.findViewById(R.id.jingyin).setOnClickListener(this);
            view.findViewById(R.id.full).setOnClickListener(this);
            view.findViewById(R.id.pip).setOnClickListener(this);
            view.findViewById(R.id.apply_address_btn).setOnClickListener(this);

            mAddressEdt = view.findViewById(R.id.address_edt);
            mAddressEdt.clearFocus();
            String ip = SharedPreferencesUtil.getInstance(getContext()).get("ip");
            if (!TextUtils.isEmpty(ip)) {
                mAddressEdt.setText(ip);
            }
            mPositionTxt = view.findViewById(R.id.position_txt);
            mDurationTxt = view.findViewById(R.id.duration_txt);
            mSeekBar = view.findViewById(R.id.seek_bar);
            mSeekBar.setOnSeekBarChangeListener(this);
            mSeekPanel = view.findViewById(R.id.seek_panel);
            mSeekPanel.setOnSeekPanelListener(this);
            refreshPlayerView(false);
            showControlBar();
        }
        return mRootView;
    }

    /**
     * 显示播控栏
     */
    private void showControlBar() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 隐藏播控栏
     */
    private void hideControlBar() {
        mHandler.removeCallbacks(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_path) {
            dialogList(true);
        } else if (id == R.id.play_json) {
            dialogList(false);
        } else if (id == R.id.start_or_pause_btn) {
            mPresenter.startOrPause();
        } else if (id == R.id.full) {
            goFullScreen();
        } else if (id == R.id.pip) {
            enterPipMode();
        } else if (id == R.id.apply_address_btn) {
            Button btn = (Button) v;
            if ("确认".equals(btn.getText())) {
                btn.setText("取消");
                mAddress = mAddressEdt.getText().toString();
                mAddressEdt.setEnabled(false);
                SharedPreferencesUtil.getInstance(getContext()).put("ip", mAddress);
            } else {
                btn.setText("确认");
                mAddressEdt.setEnabled(true);
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
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * 刷新UI，重置全屏非全屏
     *
     * @param isFull 是否是全屏
     */
    public void refreshPlayerView(boolean isFull) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ViewGroup viewGroup = (ViewGroup) mPlayerContainer.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mPlayerContainer);
        }
        if (isFull) {
            mFullContainer.addView(mPlayerContainer);
        } else {
            mSmallContainer.addView(mPlayerContainer);
        }
    }

    public boolean isFull() {
        return mFullContainer.getChildCount() > 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isInPictureInPictureMode) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                refreshPlayerView(true);
            } else {
                refreshPlayerView(false);
            }
        }
    }

    @Override
    public boolean onBackPress() {
        if (isFull()) {
            goSmallScreen();
            return true;
        }
        mPlayer.stop();
        return super.onBackPress();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.pause();
    }

    /**
     * 播放本地视频
     */
    private void dialogList(boolean isLocal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 0);
        builder.setTitle("视频列表");
        new Thread(() -> {
            List<Item> items;
            if (isLocal) {
                items = LocalDataSource.getLocalFiles(requireActivity());
            } else {
                items = NetDataSource.getItems(getActivity(), mAddress);
            }
            runOnUiThread(() -> {
                if (items == null || items.size() == 0) {
                    Toast.makeText(getActivity(), "获取数据为空", Toast.LENGTH_LONG).show();
                    return;
                }
                builder.setAdapter(new FileAdapter(getActivity(), items), (dialog, which) -> {
                    dialog.dismiss();
                    Item file = items.get(which);
                    mPresenter.playWithUri(file.getUri());
                });
                builder.create().show();
            });
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
            setPositionForView(position);
        }
    }

    public void onPlayerStart() {
        runOnUiThread(() -> {
            mStartOrPauseButton.setText("暂停");
        });
    }

    public void onPlayerPause() {
        runOnUiThread(() -> mStartOrPauseButton.setText("开始"));
    }

    @Override
    public void onProgressChanged(SeekPanel seekPanel, int progress) {
        mIsSeekTouch = true;
        mPositionTxt.setText(TimeUtil.format(progress));
        if (playInfo != null) {
            progressTextView.setText(TimeUtil.format(progress) + "/" + TimeUtil.format(playInfo.getDuration()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekPanel seekPanel) {
        mIsSeekTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekPanel seekPanel) {
        mIsSeekTouch = false;
        int value = seekPanel.getProgress();
        mPresenter.seekTo(value);
    }

    @Override
    public void onCenterDoubleTap(SeekPanel seekPanel) {
        State state = mPlayer.getState();
        switch (state) {
            case PLAYING:
                mPresenter.pause();
                break;
            case PAUSE:
                mPresenter.startOrPause();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPositionTxt.setText(TimeUtil.format(progress));
            if (playInfo != null) {
                progressTextView.setText(TimeUtil.format(progress) + "/" + TimeUtil.format(playInfo.getDuration()));
            }
        }
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

    private PlayInfo playInfo;

    public void onPrepared(PlayInfo playInfo) {
        this.playInfo = playInfo;
        int duration = playInfo.getDuration();
        mSeekBar.setMax(duration);
        mSeekPanel.setMaxProgress(duration);
        mDurationTxt.setText(TimeUtil.format(duration));
    }

    public void onBufferingProgressChanged(int percent) {
        mSeekBar.setSecondaryProgress(percent);
    }

    private void setPositionForView(int position) {
        mSeekBar.setProgress(position);
        mSeekPanel.setProgress(position);
        mPositionTxt.setText(TimeUtil.format(position));
        if (playInfo != null) {
            progressTextView.setText(TimeUtil.format(position) + "/" + TimeUtil.format(playInfo.getDuration()));
        }
    }

    private boolean isInPictureInPictureMode;

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        this.isInPictureInPictureMode = isInPictureInPictureMode;
        ViewGroup viewGroup = (ViewGroup) mPlayerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        if (isInPictureInPictureMode) {
            mPipContainer.addView(mPlayerView);
        } else {
            mSmallContainer.addView(mPlayerView);
        }
    }

    private final PictureInPictureParams.Builder mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();

    public int enterPipMode() {
        Log.d(TAG, "enterPipMode");
        if (!isSupportPip()) {
            Log.d(TAG, "do not support pip, just return");
            return -1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9);
            // Calculate the aspect ratio of the PiP screen.
            mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            Activity activity = getActivity();
            if (activity != null) {
                boolean pipRet = false;
                try {
                    pipRet = activity.enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
//                updateActions();
                if (pipRet) {
                    return 0;
                }
            }
        }
        return -1;
    }

    public boolean isSupportPip() {
        boolean ret = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.e(TAG, "Android level lower than Android 8.0, do not support PIP");
            return ret;
        }
        Activity activity = getActivity();
        if (activity != null) {
            PackageManager pm = activity.getApplicationContext().getPackageManager();
            if (pm != null) {
                ret = pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                if (!ret) {
                    Log.e(TAG, "PackageManager hasSystemFeature return false, do not support PIP");
                    return ret;
                }
            }
        } else {
            return ret;
        }
        Log.d(TAG, "isSupportPip: " + ret);
        return ret;
    }
}
