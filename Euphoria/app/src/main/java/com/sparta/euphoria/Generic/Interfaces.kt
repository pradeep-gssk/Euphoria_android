package com.sparta.euphoria.Generic

import android.view.View

interface OnItemClickListener {
    fun onItemClick(view: View?, position: Int)
}


interface OnQuestionnaireItemClickListener {
    fun onItemClick(view: View?, position: Int)
    fun toolBarVisibility(hasFocus: Boolean)
    fun updateDetails(details: String)
}

interface OnStopClickListener {
    fun onStopClick(view: View?, position: Int)
    fun onSoundClick(view: View?, position: Int)
}