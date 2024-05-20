package com.feng.player.viewmodel

import android.content.Context

class LocalMediaListViewModel : MediaListViewModel() {
    fun fetchData(context: Context) {
        super.fetchData(context, false)
    }
}