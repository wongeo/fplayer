package com.feng.media

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.Listener
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer

class EPlayer(val context: Context) : IPlayer {

    private val listener = object : Listener {

        override fun onPlayerError(error: PlaybackException) {
            mPlayStateCallback?.onMediaError(ErrorInfo(error.errorCode, error.message))
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            mPlayStateCallback?.onVideoSizeChange(videoSize.width, videoSize.height)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                if (mState != State.PAUSE) {
                    onStateChange(dest = State.PREPARED)
                    onPrepared()
                }
                onStateChange(dest = State.PLAYING)
                mHandler.removeMessages(MsgId.POSITION_CHANGE)
                mHandler.sendMessage(Message.obtain().apply {
                    what = MsgId.POSITION_CHANGE
                })
            } else {
                // Not playing because playback is paused, ended, suppressed, or the player
                // is buffering, stopped or failed. Check player.playWhenReady,
                // player.playbackState, player.playbackSuppressionReason and
                // player.playerError for details.
            }
        }
    }

    val player = ExoPlayer.Builder(context).build().apply {
        this.addListener(listener)
    }

    private var mState = State.STOP
    private var mPlayStateCallback: IPlayStateCallback? = null
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            this@EPlayer.handleMessage(msg)
        }
    }

    private interface MsgId {
        companion object {
            const val POSITION_CHANGE = 0
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            MsgId.POSITION_CHANGE -> {
                val position: Long = player.currentPosition
                val duration: Long = player.duration
                mPlayStateCallback?.onPlayPositionChanged(0f, position, duration)
                mHandler.removeMessages(MsgId.POSITION_CHANGE)
                val next = Message.obtain()
                mHandler.sendMessageDelayed(next, 1000)
            }

            else -> {}
        }
    }


    override fun play(uri: String, point: Int) {
        val mediaItem = MediaItem.fromUri(uri)
        // Set the media item to be played.
        player.setMediaItem(mediaItem)
        // Prepare the player.
        player.prepare()
        // Start the playback.
        player.play()
    }

    override fun setPlayStateCallback(callback: IPlayStateCallback?) {
        mPlayStateCallback = callback
    }

    private fun onPrepared() {
        mPlayStateCallback?.onPrepared(PlayInfo(player.duration.toInt()))
    }

    private fun onStateChange(src: State = mState, dest: State) {
        mState = dest
        player.playbackState
        mPlayStateCallback!!.onPlayerStateChanged(src, dest)
    }

    override fun getState(): State {
        return mState
    }

    override fun start() {
        player.play()
    }

    override fun pause() {
        mHandler.removeMessages(MsgId.POSITION_CHANGE)
        player.pause()
        onStateChange(dest = State.PAUSE)
    }

    override fun stop() {
        mHandler.removeMessages(MsgId.POSITION_CHANGE)
        player.stop()
        onStateChange(dest = State.STOP)
    }

    override fun setDisplay(surface: Surface?) {
        player.setVideoSurface(surface)
    }

    override fun seekTo(value: Int) {
        player.seekTo(value.toLong())
    }

    override fun setSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun getCurrentPosition(): Long {
        return player.duration
    }
}