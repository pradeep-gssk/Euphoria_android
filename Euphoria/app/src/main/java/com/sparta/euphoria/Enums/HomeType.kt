package com.sparta.euphoria.Enums

import android.media.Image
import android.support.v7.app.AppCompatActivity
import com.sparta.euphoria.Activities.*
import com.sparta.euphoria.R

enum class HomeType(val title: String,
                    val character: String,
                    val characterImage: Int,
                    val accessoryImage: Int,
                    val cls: Class<AppCompatActivity>) {

    Questionnaires(
        "my Questionnaires",
        "Q",
        R.mipmap.oval_blue,
        R.mipmap.rectangle_blue,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    Diet(
        "my Diet",
        "D",
        R.mipmap.oval_green,
        R.mipmap.rectangle_green,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    Exercises(
        "my Exercises",
        "E",
        R.mipmap.oval_red,
        R.mipmap.rectangle_red,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    Activities(
        "my Activites",
        "A",
        R.mipmap.oval_brown,
        R.mipmap.rectangle_brown,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    History(
        "my History",
        "H",
        R.mipmap.oval_blue,
        R.mipmap.rectangle_blue,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    Gallery(
        "my Gallery",
        "G",
        R.mipmap.oval_green,
        R.mipmap.rectangle_green,
        HomeActivity::class.java as Class<AppCompatActivity>
    ),

    Timer(
        "my Timer",
        "T",
        R.mipmap.oval_red,
        R.mipmap.rectangle_red,
        HomeActivity::class.java as Class<AppCompatActivity>
    );
}