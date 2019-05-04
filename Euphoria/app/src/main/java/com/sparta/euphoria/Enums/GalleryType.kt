package com.sparta.euphoria.Enums

enum class GalleryType(val title: String,
                       val character: String) {
    videos(
        "Videos",
        "V"
    ),

    others(
        "Others",
        "O"
    );

    companion object {
        private val map = GalleryType.values().associateBy(GalleryType::title)
        fun getGalleryType(type: String?) = map[type]
    }
}