package com.feng.player.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feng.media.ErrorInfo
import com.feng.media.IPlayStateCallback
import com.feng.media.PlayInfo
import com.feng.media.IPlayer
import com.feng.media.State

class PlayerViewModel : ViewModel(), IPlayStateCallback {
    private lateinit var player: IPlayer

    //mvvm动态变量
    var progress: MutableLiveData<Int> = MutableLiveData(0)
    var duration: MutableLiveData<Int> = MutableLiveData(0)
    var state: MutableLiveData<State> = MutableLiveData(State.STOP)
    var videoSize: MutableLiveData<Pair<Int, Int>> = MutableLiveData()

    fun createPlayer(context: Context): IPlayer {
        player = IPlayer.create(context, null).apply {
            setPlayStateCallback(this@PlayerViewModel)
        }
        return player
    }

    fun play(url: String) {
        player.play(url, 0)
    }

    fun pause() {
        player.pause()
    }

    fun mute() {
    }

    fun resume() {
        player.start()
    }

    fun seekTo(position: Int) {
        progress.value = position
        player.seekTo(position)
    }

    fun stop() {
        player.stop()
    }

    override fun onCompletion() {

    }

    override fun onBufferingProgressChanged(percent: Int) {

    }

    override fun onMediaError(ex: ErrorInfo?) {

    }

    override fun onPrepared(playInfo: PlayInfo?) {
        duration.value = playInfo?.duration
    }

    override fun onPlayerStateChanged(oldState: State?, newState: State?) {
        state.value = newState
    }

    override fun onVideoSizeChange(width: Int, height: Int) {
        videoSize.value = Pair(width, height)
    }

    override fun onPlayPositionChanged(percent: Float, position: Long, duration: Long) {
        progress.value = position.toInt()
    }

    fun startOrPause() {
        val state = player.state
        if (state == State.PAUSE) {
            player.start()
        } else if (state == State.PLAYING) {
            player.pause()
        }
    }
}