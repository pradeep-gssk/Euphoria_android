package com.example.euphoria.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.Extensions.getJson
import com.example.euphoria.Extensions.getJsonArray
import com.example.euphoria.R

class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.euphoria.sparta.prefs"

    val prefs: SharedPreferences by lazy {
        this.getSharedPreferences(PREFS_FILENAME, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread() {
            preloadData()
        }.start()
    }

    fun didClickForgotPassword(view: View) {
    }

    fun didClickSignIn(view: View) {
        val intent = Intent(this, HomeActivity:: class.java)
        startActivity(intent)
    }

    fun preloadData() {

        val dataLoaded = prefs.getBoolean("PRELOAD_DATA", false)
        if (!dataLoaded) {
            val db = DataBaseHelper.getDatabase(applicationContext)
            db.addQuestionnaires("Questionnaire1.json".getJson(application), 20)
            db.addQuestionnaires("Questionnaire2.json".getJson(application), 6)
            db.addQuestionnaires("Questionnaire3.json".getJson(application), 35)
            db.addQuestionnaires("Questionnaire4.json".getJson(application), 10)
            db.addQuestionnaires("Questionnaire5.json".getJson(application), 4)
            db.addExercises("Exercises.json".getJsonArray(application))
            db.addSounds("Sounds.json".getJsonArray(application))
            db.addVideos("Videos.json".getJsonArray(application))
            db.addDiet("Diet.json".getJsonArray(application))

            val prefsEditor = prefs.edit()
            prefsEditor.putBoolean("PRELOAD_DATA", true)
            prefsEditor.apply()
        }
        else {
//            println("---------START---------")
//            val db = DataBaseHelper.getDatabase(applicationContext)
//            println(db.dietDao().getUniqueDietsForElement("Earth"))


//            println(db.exercisesDao().getExercises())
//            println(db.dietDao().getDiet())
//            println(db.soundDao().getSoundList())
//            println(db.videoDao().getVideos())
//            println("---------END---------")
        }
    }
}