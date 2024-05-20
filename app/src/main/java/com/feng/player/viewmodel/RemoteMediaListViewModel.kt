package com.feng.player.viewmodel

import android.content.Context

class RemoteMediaListViewModel : MediaListViewModel() {
    fun fetchData(context: Context) {
        super.fetchData(context, true)
    }
}