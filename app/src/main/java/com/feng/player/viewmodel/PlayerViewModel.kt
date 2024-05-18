package com.feng.player.viewmodel

import android.content.Context
import android.view.TextureView
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feng.media.FPlayer
import com.feng.media.IPlayStateCallback
import com.feng.media.PlayInfo
import com.feng.media.Player
import com.feng.media.State
import com.feng.resize.ResizeView
import java.lang.Exception

class PlayerViewModel : ViewModel(), IPlayStateCallback {
    private val player = MutableLiveData<Player>()
    private val playerView = MutableLiveData<ResizeView>()
    var progress: MutableLiveData<Int> = MutableLiveData(0)
    var duration: MutableLiveData<Int> = MutableLiveData(0)

    fun initPlayer(context: Context) {
        val videoView: View = TextureView(context)
        player.value = FPlayer(context)
        playerView.value = ResizeView(context.applicationContext)
        playerView.value!!.bind(player.value as FPlayer, videoView)
        player.value!!.setPlayStateCallback(this)
    }

    fun getPlayerView(): ResizeView {
        return playerView.value ?: throw Exception("playerView is null")
    }

    fun play(url: String) {
        player.value?.play(url, 0)
    }

    fun pause() {
        player.value?.pause()
    }

    fun mute() {
    }

    fun resume() {
        player.value?.start()
    }

    fun seekTo(position: Int) {
        progress.value = position
        player.value?.seekTo(position)
    }

    fun stop() {
        player.value?.stop()
    }

    override fun onCompletion() {

    }

    override fun onBufferingProgressChanged(percent: Int) {

    }

    override fun onMediaError(ex: Exception?) {

    }

    override fun onPrepared(playInfo: PlayInfo?) {
        duration.value = playInfo?.duration
    }

    override fun onPlayerStateChanged(oldState: State?, newState: State?) {

    }

    override fun onVideoSizeChange(width: Int, height: Int) {
        playerView.value?.refreshVideoSize(width, height)
    }

    override fun onPlayPositionChanged(percent: Float, position: Long, duration: Long) {
        progress.value = position.toInt()
    }

    fun startOrPause() {
        val state = player.value!!.state
        if (state == State.PAUSE) {
            player.value!!.start()
        } else if (state == State.PLAYING) {
            player.value!!.pause()
        }
    }
}