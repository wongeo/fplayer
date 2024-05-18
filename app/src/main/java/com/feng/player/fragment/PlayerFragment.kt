package com.feng.player.fragment

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.feng.player.R
import com.feng.player.viewmodel.PlayerViewModel
import com.feng.video.util.TimeUtil
import com.feng.video.view.SeekPanel
import com.feng.video.view.SeekPanel.OnSeekPanelListener

class PlayerFragment : Fragment(), View.OnClickListener, OnSeekBarChangeListener, OnSeekPanelListener {

    private lateinit var viewModel: PlayerViewModel

    private val mHandler = Handler(Looper.getMainLooper())

    //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private var mSmallContainer: FrameLayout? = null  //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private var mFullContainer: FrameLayout? = null  //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private var mPipContainer: FrameLayout? = null  //播放器父容器,小窗口容器，全屏容器，pip小窗容器
    private var mPlayerContainer: FrameLayout? = null
    private var mSeekBar: SeekBar? = null
    private var mSeekPanel: SeekPanel? = null
    private var mStartOrPauseButton: Button? = null
    private var mTimeTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        this.context?.let { viewModel.initPlayer(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.player_fragment, container, false);

        //播放器父容器
        mSmallContainer = view.findViewById(R.id.small_screen_player_view_container)
        mFullContainer = view.findViewById(R.id.full_screen_player_view_container)
        mPipContainer = view.findViewById(R.id.pip_screen_player_view_container)
        mPlayerContainer = view.findViewById(R.id.player_container)

        //初始化播放器和surface并且进行绑定
        mPlayerContainer!!.addView(viewModel.getPlayerView(), 0)

        //播放器控制按钮
        mStartOrPauseButton = view.findViewById(R.id.start_or_pause_btn)
        mStartOrPauseButton!!.setOnClickListener(this)
        view.findViewById<View>(R.id.jingyin).setOnClickListener(this)
        view.findViewById<View>(R.id.full).setOnClickListener(this)
        view.findViewById<View>(R.id.pip).setOnClickListener(this)
        mTimeTextView = view.findViewById(R.id.time_tv)
        mSeekBar = view.findViewById(R.id.seek_bar)
        mSeekBar!!.setOnSeekBarChangeListener(this)
        mSeekPanel = view.findViewById(R.id.seek_panel)
        mSeekPanel!!.setOnSeekPanelListener(this)

        return view
    }

    private fun formatTime(position: Int, duration: Int): String {
        return "${TimeUtil.format(position.toLong())} / ${TimeUtil.format(duration.toLong())}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.progress.observe(viewLifecycleOwner) {
            if (!isSeekTouch) {
                mSeekBar!!.progress = it
                mSeekPanel!!.progress = it
                mTimeTextView!!.text = formatTime(it, viewModel.duration.value!!)
            }
        }

        viewModel.duration.observe(viewLifecycleOwner) {
            mSeekBar!!.max = it
            mSeekPanel!!.setMaxProgress(it)
        }

        activity?.intent?.getStringExtra("url").let {
            if (it != null) {
                viewModel.play(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.full -> {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            R.id.pip -> {

            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                refreshPlayerView(true)
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                refreshPlayerView(false)
            }
        }
    }

    fun onBackPress(): Boolean {
        if (isFull()) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            return true
        }
        return false
    }

    private fun isFull(): Boolean {
        return mFullContainer!!.childCount > 0
    }

    private fun refreshPlayerView(isFull: Boolean) {
        val viewGroup = mPlayerContainer!!.parent as ViewGroup
        viewGroup.removeView(mPlayerContainer)
        if (isFull) {
            mFullContainer!!.addView(mPlayerContainer)
        } else {
            mSmallContainer!!.addView(mPlayerContainer)
        }
    }

    private fun startOrPause() {
        viewModel.startOrPause()
    }

    private var isSeekTouch = false

    private fun seekStart() {
        isSeekTouch = true
    }

    private fun seeking(progress: Int) {
        isSeekTouch = true
        mTimeTextView?.text = formatTime(progress, viewModel.duration.value!!)
    }

    private fun seekEnd(progress: Int) {
        if (isSeekTouch) {
            isSeekTouch = false
            viewModel.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekStart()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            seeking(progress)
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekEnd(progress = seekBar!!.progress)
    }

    override fun onProgressChanged(seekPanel: SeekPanel?, progress: Int) {
        seeking(progress)
    }

    override fun onStartTrackingTouch(seekPanel: SeekPanel?) {
        seekStart()
    }

    override fun onStopTrackingTouch(seekPanel: SeekPanel?) {
        seekEnd(progress = seekPanel!!.progress)
    }

    override fun onCenterDoubleTap(seekPanel: SeekPanel?) {
        viewModel.startOrPause()
    }
}