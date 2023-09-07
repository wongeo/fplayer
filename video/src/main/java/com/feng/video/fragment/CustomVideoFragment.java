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

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CustomVideoFragment extends BaseFragment<CustomVideoPresenter> implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

    private final static String TAG = "CustomVideoFragment";
    private final CustomVideoPresenter mPresenter;
    public ResizeView mPlayerView;
    private Player mPlayer;
    private View mRootView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private FrameLayout mSmallContainer, mFullContainer, mPipContainer;
    private SeekBar mSeekBar;
    private EditText mAddressEdt;
    private String mAddress;
    private Button mStartOrPauseButton;
    private TextView mPositionTxt, mDurationTxt;

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

            //初始化播放器和surface并且进行绑定
            mPlayer = new FPlayer(getContext());
            mPlayerView = new ResizeView(getContext());
            View videoView = new TextureView(getContext());
            mPlayerView.bind(mPlayer, videoView);

            mPlayerView.setOnLongClickListener(this);
            mPlayerView.setOnTouchListener(this);

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

            refreshPlayerView(false);
            showControlBar();
        }
        return mRootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mPlayer.setSpeed(1);
        }
        return mGestureDetector.onTouchEvent(event);
    }


    /**
     * 返回手势的点击区域
     *
     * @param e 手势
     * @return 1, 2, 3
     */
    private int touchRect(MotionEvent e) {
        int width = mPlayerView.getWidth();
        float tw = width / 3f;
        if (e.getX() < tw) {
            return 1;
        } else if (e.getX() > tw && e.getX() < width - tw) {
            return 2;
        } else if (e.getX() > width - tw) {
            return 3;
        }
        return 0;
    }

    /**
     * 手势
     */
    private final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {//单击事件
            if (touchRect(e) == 2) {//单击显示播控栏区域
                //点击右侧

            } else {
                //进行快速seek
                long position = mSeekBar.getProgress();
                if (touchRect(e) == 1) {
                    //快退N秒
                    position -= 10000;
                } else if (touchRect(e) == 3) {
                    //快进N秒
                    position += 10000;
                }
                mPlayer.seekTo((int) position);
                mSeekBar.setProgress((int) position);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击事件
            if (touchRect(e) == 2) {
                State state = mPlayer.getState();
                if (state == State.PLAYING) {
                    mPlayer.pause();
                } else if (state == State.PAUSE) {
                    mPlayer.start();
                }
            } else {
                //进行快速seek
                long position = mSeekBar.getProgress();
                if (touchRect(e) == 1) {
                    //快退N秒
                    position -= 10000;
                } else if (touchRect(e) == 3) {
                    //快进N秒
                    position += 10000;
                }
                mPlayer.seekTo((int) position);
                mSeekBar.setProgress((int) position);
            }
            return true;
        }
    });

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
                ss();
//                btn.setText("取消");
//                mAddress = mAddressEdt.getText().toString();
//                mAddressEdt.setEnabled(false);
//                SharedPreferencesUtil.getInstance(getContext()).put("ip", mAddress);
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
        layoutParams.gravity = Gravity.BOTTOM;
        float pixel = DensityUtils.dpToPx(getContext(), 40);
        layoutParams.bottomMargin = (int) pixel;

        ViewGroup viewGroup = (ViewGroup) mPlayerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }

        if (isFull) {
            mFullContainer.addView(mPlayerView);
        } else {
            mSmallContainer.addView(mPlayerView);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPositionTxt.setText(TimeUtil.format(progress));
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

    public void onPrepared(int duration) {
        mSeekBar.setMax(duration);
        mDurationTxt.setText(TimeUtil.format(duration));
    }

    public void onBufferingProgressChanged(int percent) {
        mSeekBar.setSecondaryProgress(percent);
    }

    private void setPositionForView(int position) {
        mSeekBar.setProgress(position);
        mPositionTxt.setText(TimeUtil.format(position));
    }

    @Override
    public boolean onLongClick(View view) {
        mPlayer.setSpeed(3);
        return false;
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

                //需要设计给icon
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

    private void ss() {
    }
}
