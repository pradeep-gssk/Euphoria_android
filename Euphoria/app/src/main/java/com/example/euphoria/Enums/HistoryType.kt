package com.example.euphoria.Enums

import android.media.Image

enum class HistoryType(val title: String,
                       val character: String,
                       val image: String,
                       val placeholder: String,
                       val emailSubject: String,
                       val emailMessage: String,
                       val characterImage: Image?,
                       val accessoryImage: Image?) {

    face(
        "my Face",
        "F",
        "myFace",
        "facePlaceholder",
        "Face Image",
        "This is Face Image",
        null,
        null
    ),

    tongue(
        "my Tongue",
        "T",
        "myTongue",
        "tonguePlaceholder",
        "Tongue Image",
        "This is Tongue Image",
        null,
        null
    )
}