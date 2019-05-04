package com.sparta.euphoria.Enums

import com.sparta.euphoria.R

enum class ActivityType(val index: Int,
                        val title: String,
                        val character: String,
                        val characterImage: Int,
                        val accessoryImage: Int) {

    calendar(
        0,
        "my Calendar",
        "C",
        R.mipmap.oval_brown,
        R.mipmap.rectangle_brown
    ),

    programmes(
        1,
        "Euphoria Programmes & Retreats",
        "",
        R.mipmap.logo_circle,
        R.mipmap.rectangle_beige
    );

    companion object {
        private val map = ActivityType.values().associateBy(ActivityType::index)
        fun getActivityType(type: Int) = map[type]
    }
}