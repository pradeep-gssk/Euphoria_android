package com.example.euphoria.Enums

import android.media.Image

enum class GalleryType(val title: String,
                       val character: String,
                       val characterImage: Image?,
                       val accessoryImage: Image?) {
    videos(
        "Videos",
        "V",
        null,
        null
    ),

    others(
        "Others",
        "O",
        null,
        null
    );

    companion object {
        private val map = GalleryType.values().associateBy(GalleryType::title)
        fun getGalleryType(type: String?) = map[type]
    }
}