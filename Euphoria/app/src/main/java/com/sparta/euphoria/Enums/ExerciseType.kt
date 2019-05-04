package com.sparta.euphoria.Enums

enum class ExerciseType(val exercise: String,
                        val title: String,
                        val character: String) {

    yoga(
        "Yoga",
        "Yoga",
        "Y"
    ),

    pilates(
        "Pilates",
        "Pilates",
        "P"
    ),

    running(
        "Running",
        "Running",
        "R"
    ),

    climbing(
        "Climbing",
        "Climbing",
        "C"
    ),

    qigong(
        "Qi-Gong",
        "Qi Gong",
        "Q"
    ),

    stretching(
        "Stretching",
        "Stretching",
        "S"
    );

    companion object {
        private val map = ExerciseType.values().associateBy(ExerciseType::exercise)
        fun getExerciseType(type: String?) = map[type]
    }
}