package com.feng.player.fragment

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.feng.media.IPlayer
import com.feng.media.State
import com.feng.player.R
import com.feng.player.util.formatTime
import com.feng.player.viewmodel.PlayerViewModel
import com.feng.resize.ResizeView
import com.feng.video.view.SeekPanel
import com.feng.video.view.SeekPanel.OnSeekPanelListener

class PlayerFragment : Fragment(), View.OnClickListener, OnSeekBarChangeListener {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var mSmallContainer: ViewGroup
    private lateinit var mFullContainer: ViewGroup
    private lateinit var mPipContainer: ViewGroup
    private lateinit var mPlayerContainer: ViewGroup
    private lateinit var mSeekBar: SeekBar
    private lateinit var mSeekPanel: SeekPanel
    private lateinit var mTimeTextView: TextView
    private lateinit var mStartOrPause: ImageView
    private lateinit var mFullScreen: View
    private lateinit var playerView: ResizeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        this.context?.let {
            playerView = ResizeView(it)
            val player: IPlayer = viewModel.createPlayer(it)
            playerView.bind(player, TextureView(it))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.player_fragment, container, false)

        //播放器父容器
        mSmallContainer = view.findViewById(R.id.small_screen_player_view_container)
        mFullContainer = view.findViewById(R.id.full_screen_player_view_container)
        mPipContainer = view.findViewById(R.id.pip_screen_player_view_container)
        mPlayerContainer = view.findViewById(R.id.player_container)

        //初始化播放器和surface并且进行绑定
        mPlayerContainer.addView(playerView, 0)

        //无状态按钮
        listOf(R.id.pip).forEach {
            view.findViewById<View>(it).setOnClickListener(this)
        }
        mFullScreen = view.findViewById<View?>(R.id.full).apply {
            setOnClickListener(this@PlayerFragment)
        }
        mStartOrPause = view.findViewById<ImageView>(R.id.start_or_pause_iv).apply {
            setOnClickListener(this@PlayerFragment)
        }

        mTimeTextView = view.findViewById(R.id.time_tv)
        mSeekBar = view.findViewById(R.id.seek_bar)
        mSeekBar.setOnSeekBarChangeListener(this)
        mSeekPanel = view.findViewById(R.id.seek_panel)
        mSeekPanel.setOnSeekPanelListener(playControlListener)
        return view
    }

    private fun formatTime(position: Int, duration: Int): String {
        return "${formatTime(position.toLong())} / ${formatTime(duration.toLong())}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.progress.observe(viewLifecycleOwner) {
            if (!isSeekTouch) {
                mSeekBar.progress = it
                mTimeTextView.text = formatTime(it, viewModel.duration.value!!)
            }
        }
        viewModel.duration.observe(viewLifecycleOwner) {
            mSeekBar.max = it
            mSeekPanel.setMaxProgress(it)
        }

        viewModel.videoSize.observe(viewLifecycleOwner) {
            playerView.refreshVideoSize(it.first, it.second)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                State.PLAYING -> {
                    mStartOrPause.setImageResource(R.drawable.ic_player_pause)
                }

                else -> {
                    mStartOrPause.setImageResource(R.drawable.ic_player_playing)
                }
            }
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

            R.id.start_or_pause_iv -> {
                viewModel.startOrPause()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                refreshPlayerView(true)
            }

            else -> {
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
        return mFullContainer.childCount > 0
    }

    private fun refreshPlayerView(isFull: Boolean) {
        val viewGroup = mPlayerContainer.parent as ViewGroup
        viewGroup.removeView(mPlayerContainer)
        if (isFull) {
            mFullScreen.visibility = View.GONE
            mFullContainer.addView(mPlayerContainer)
        } else {
            mFullScreen.visibility = View.VISIBLE
            mSmallContainer.addView(mPlayerContainer)
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
        mTimeTextView.text = formatTime(progress, viewModel.duration.value!!)
    }

    private fun seekEnd(progress: Int) {
        isSeekTouch = false
        viewModel.seekTo(progress)
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

    private val playControlListener = object : OnSeekPanelListener {

        override fun onProgressChanged(seekPanel: SeekPanel?, diffProgress: Int) {
            viewModel.progress.value?.let {
                val progress = it + diffProgress
                mSeekBar.progress = progress
                seeking(progress)
            }
        }

        override fun onStartTrackingTouch(seekPanel: SeekPanel?) {
            seekStart()
        }

        override fun onStopTrackingTouch(seekPanel: SeekPanel?) {
            viewModel.progress.value?.let {
                val progress = it + (seekPanel?.progress ?: 0)
                seekEnd(progress)
            }
        }

        override fun onCenterDoubleTap(seekPanel: SeekPanel?) {
            startOrPause()
        }

        override fun onLongPress(seekPanel: SeekPanel?, touchRect: Int, press: Boolean) {
            if (press) {
                //开始长按
                viewModel.setSpeed(3F)
            } else {
                //停止长按
                viewModel.setSpeed(1F)
            }
        }
    }
}