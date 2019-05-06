package com.sparta.euphoria.Enums

import com.sparta.euphoria.R

enum class HTMLType(val path: String, val button: Int) {
    concent(
        "file:///android_asset/Concent.html",
        R.string.i_agree
    ),

    privacy(
        "file:///android_asset/Privacy.html",
        R.string.confirm
    );

    companion object {
        private val map = SelectionType.values().associateBy(SelectionType::option)
        fun getSelectionType(type: Int) = map[type]
    }
}