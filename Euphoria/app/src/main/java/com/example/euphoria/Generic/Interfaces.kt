package com.example.euphoria.Generic

import android.view.View

interface OnItemClickListener {
    fun onItemClick(view: View?, position: Int)
}

interface OnStopClickListener {
    fun onStopClick(view: View?, position: Int)
    fun onSoundClick(view: View?, position: Int)
}