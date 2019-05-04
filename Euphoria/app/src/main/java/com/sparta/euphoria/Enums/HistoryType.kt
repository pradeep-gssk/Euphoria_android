package com.sparta.euphoria.Enums

import com.sparta.euphoria.R

enum class HistoryType(val title: String,
                       val character: String,
                       val image: String,
                       val placeholder: Int,
                       val emailSubject: String,
                       val emailMessage: String) {

    face(
        "my Face",
        "F",
        "myFace",
        R.mipmap.face,
        "Face Image",
        "This is Face Image"
    ),

    tongue(
        "my Tongue",
        "T",
        "myTongue",
        R.mipmap.tongue,
        "Tongue Image",
        "This is Tongue Image"
    )
}