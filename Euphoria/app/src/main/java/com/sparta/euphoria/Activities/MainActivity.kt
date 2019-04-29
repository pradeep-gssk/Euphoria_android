package com.sparta.euphoria.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sparta.euphoria.R

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
//        val intent = Intent(this, HomeActivity:: class.java)
//        startActivity(intent)
    }

    fun preloadData() {
        val dataLoaded = prefs.getBoolean("PRELOAD_DATA", false)
    }
}
