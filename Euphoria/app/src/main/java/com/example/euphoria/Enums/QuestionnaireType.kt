package com.example.euphoria.Enums

import android.media.Image

enum class QuestionnaireType(val questionnaire: String,
                             val character: String,
                             val characterImage: Image?,
                             val accessoryImage: Image?) {

    Questionnaire1(
        "Questionnaire 1",
        "Q1",
        null,
        null
    ),

    Questionnaire2(
        "Questionnaire 2",
        "Q2",
        null,
        null
    ),

    Questionnaire3(
        "Questionnaire 3",
        "Q3",
        null,
        null
    ),

    Questionnaire4(
        "Questionnaire 4",
        "Q4",
        null,
        null
    ),

    Questionnaire5(
        "Questionnaire 5",
        "Q5",
        null,
        null
    );

    companion object {
        private val map = QuestionnaireType.values().associateBy(QuestionnaireType::questionnaire)
        fun getQuestionnaireType(type: String) = map[type]
    }
}