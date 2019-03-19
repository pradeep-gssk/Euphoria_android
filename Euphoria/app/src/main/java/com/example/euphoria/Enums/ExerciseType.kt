package com.example.euphoria.Enums

import android.media.Image

enum class ExerciseType(val exercise: String,
                        val title: String,
                        val character: String,
                        val characterImage: Image?,
                        val accessoryImage: Image?) {

    yoga(
        "Yoga",
        "Yoga",
        "Y",
        null,
        null
    ),

    pilates(
        "Pilates",
        "Pilates",
        "P",
        null,
        null
    ),

    running(
        "Running",
        "Running",
        "R",
        null,
        null
    ),

    climbing(
        "Climbing",
        "Climbing",
        "C",
        null,
        null
    ),

    qigong(
        "Qi-Gong",
        "Qi Gong",
        "Q",
        null,
        null
    ),

    stretching(
        "Stretching",
        "Stretching",
        "S",
        null,
        null
    );

    companion object {
        private val map = ExerciseType.values().associateBy(ExerciseType::exercise)
        fun getExerciseType(type: String?) = map[type]
    }
}