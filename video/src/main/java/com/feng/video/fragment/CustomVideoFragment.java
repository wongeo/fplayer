package com.feng.video.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
import androidx.annotation.RequiresApi;

import com.feng.media.FPlayer;
import com.feng.media.Player;
import com.feng.media.State;
import com.feng.mvp.BaseFragment;
import com.feng.resize.ResizeView;
import com.feng.video.R;
import com.feng.video.adapter.FileAdapter;
import com.feng.video.adapter.Item;
import com.feng.video.db.LocalDataSource;
import com.feng.video.db.NetDataSource;
import com.feng.video.util.SharedPreferencesUtil;
import com.feng.video.util.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CustomVideoFragment extends BaseFragment<CustomVideoPresenter> implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

    private final static String TAG = "CustomVideoFragment";
    private CustomVideoPresenter mPresenter;
    public ResizeView mPlayerView;
    private Player mPlayer;
    private View mRootView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    //??????????????????,?????????????????????????????????pip????????????
    private FrameLayout mSmallContainer, mFullContainer, mPipContainer;
    private SeekBar mSeekBar;
    private EditText mAddressEdt;
    private String mAddress;
    private View mControlBarPanel;
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
            //??????????????????
            mSmallContainer = view.findViewById(R.id.small_screen_player_view_container);
            mFullContainer = view.findViewById(R.id.full_screen_player_view_container);
            mPipContainer = view.findViewById(R.id.pip_screen_player_view_container);

            //?????????????????????surface??????????????????
            mPlayer = new FPlayer(getContext());
            mPlayerView = new ResizeView(getContext());
            View videoView = new TextureView(getContext());
            mPlayerView.bind(mPlayer, videoView);

            mPlayerView.setOnLongClickListener(this);
            mPlayerView.setOnTouchListener(this);

            //?????????????????????
            view.findViewById(R.id.play_url).setOnClickListener(this);
            view.findViewById(R.id.play_path).setOnClickListener(this);
            view.findViewById(R.id.play_json).setOnClickListener(this);
            view.findViewById(R.id.start_button).setOnClickListener(this);
            view.findViewById(R.id.pause_button).setOnClickListener(this);
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
            mControlBarPanel = inflater.inflate(R.layout.video_media_controller, null, false);
            mPositionTxt = mControlBarPanel.findViewById(R.id.position_txt);
            mDurationTxt = mControlBarPanel.findViewById(R.id.duration_txt);
            mSeekBar = mControlBarPanel.findViewById(R.id.seek_bar);
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
     * ???????????????????????????
     *
     * @param e ??????
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
     * ??????
     */
    private final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {//????????????
            if (touchRect(e) == 2) {//???????????????????????????
                //????????????
                if (isControlBarShowing()) {
                    hideControlBar();
                } else {
                    showControlBar();
                }
            } else {
                //????????????seek
                long position = mSeekBar.getProgress();
                if (touchRect(e) == 1) {
                    //??????N???
                    position -= 10000;
                } else if (touchRect(e) == 3) {
                    //??????N???
                    position += 10000;
                }
                mPlayer.seekTo((int) position);
                mSeekBar.setProgress((int) position);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//????????????
            if (touchRect(e) == 2) {
                State state = mPlayer.getState();
                if (state == State.PLAYING) {
                    mPlayer.pause();
                } else if (state == State.PAUSE) {
                    mPlayer.start();
                }
            }else {
                //????????????seek
                long position = mSeekBar.getProgress();
                if (touchRect(e) == 1) {
                    //??????N???
                    position -= 10000;
                } else if (touchRect(e) == 3) {
                    //??????N???
                    position += 10000;
                }
                mPlayer.seekTo((int) position);
                mSeekBar.setProgress((int) position);
            }
            return true;
        }
    });

    private boolean isControlBarShowing() {
        return mControlBarPanel.getVisibility() == View.VISIBLE;
    }

    /**
     * ???????????????
     */
    private void showControlBar() {
        mHandler.removeCallbacksAndMessages(null);
        mControlBarPanel.setVisibility(View.VISIBLE);
    }

    /**
     * ???????????????
     */
    private void hideControlBar() {
        mHandler.removeCallbacks(null);
        mControlBarPanel.setVisibility(View.INVISIBLE);
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
            mPresenter.pause();
        } else if (id == R.id.full) {
            goFullScreen();
        } else if (id == R.id.pip) {
            enterPipMode();
        } else if (id == R.id.apply_address_btn) {
            Button btn = (Button) v;
            if ("??????".equals(btn.getText())) {
                btn.setText("??????");
                mAddress = mAddressEdt.getText().toString();
                mAddressEdt.setEnabled(false);
                SharedPreferencesUtil.getInstance(getContext()).put("ip", mAddress);
            } else {
                btn.setText("??????");
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
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * ??????UI????????????????????????
     *
     * @param isFull ???????????????
     */
    public void refreshPlayerView(boolean isFull) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, this.getResources().getDisplayMetrics());
        layoutParams.bottomMargin = (int) pixel;

        ViewGroup viewGroup = (ViewGroup) mPlayerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }

        if (isFull) {
            mFullContainer.addView(mPlayerView);
            mFullContainer.addView(mControlBarPanel, layoutParams);
        } else {
            mSmallContainer.addView(mPlayerView);
            mSmallContainer.addView(mControlBarPanel, layoutParams);
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

    /**
     * ??????????????????
     */
    private void dialogList(boolean isLocal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 0);
        builder.setTitle("????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Item> items;
                if (isLocal) {
                    items = LocalDataSource.getLocalFiles(getActivity());
                } else {
                    items = NetDataSource.getItems(getActivity(), mAddress);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items == null || items.size() == 0) {
                            Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
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
            setPositionForView(position);
        }
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

                //???????????????icon
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
