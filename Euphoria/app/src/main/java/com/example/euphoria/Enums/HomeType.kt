package com.example.euphoria.Enums

import android.media.Image
import android.support.v7.app.AppCompatActivity
import com.example.euphoria.Activities.Diet.DietActivity
import com.example.euphoria.Activities.Exercises.ExerciseActivity
import com.example.euphoria.Activities.Gallery.GalleryActivity
import com.example.euphoria.Activities.History.HistoryActivity
import com.example.euphoria.Activities.Questionnaires.QuestionnairesActivity
import com.example.euphoria.Activities.Timer.SessionsActivity

enum class HomeType(val title: String,
                    val character: String,
                    val characterImage: Image?,
                    val accessoryImage: Image?,
                    val cls: Class<AppCompatActivity>) {

    Questionnaires(
        "my Questionnaires",
        "Q",
        null,
        null,
        QuestionnairesActivity::class.java as Class<AppCompatActivity>
    ),

    Diet(
        "my Diet",
        "D",
        null,
        null,
        DietActivity::class.java as Class<AppCompatActivity>
    ),

    Exercises(
        "my Exercises",
        "E",
        null,
        null,
        ExerciseActivity::class.java as Class<AppCompatActivity>
    ),

    Activities(
        "my Activites",
        "A",
        null,
        null,
        QuestionnairesActivity::class.java as Class<AppCompatActivity>
    ),

    History(
        "my History",
        "H",
        null,
        null,
        HistoryActivity::class.java as Class<AppCompatActivity>
    ),

    Gallery(
        "my Gallery",
        "G",
        null,
        null,
        GalleryActivity::class.java as Class<AppCompatActivity>
    ),

    Timer(
        "my Timer",
        "T",
        null,
        null,
        SessionsActivity::class.java as Class<AppCompatActivity>
    );
}