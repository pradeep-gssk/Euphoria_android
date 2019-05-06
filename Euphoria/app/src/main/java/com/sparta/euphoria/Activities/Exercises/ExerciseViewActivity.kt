package com.sparta.euphoria.Activities.Exercises

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.sparta.euphoria.DataBase.Exercises
import com.sparta.euphoria.Enums.ExerciseType
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.R

class ExerciseViewActivity: AppCompatActivity() {
    private var mTitleTextView: TextView? = null
    private var mVideoNameTextView: TextView? = null
    private var mVideoDescriptionTextView: TextView? = null
    private var mediaController: MediaController? = null
    private var mPlayer: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoview)
        mTitleTextView = findViewById(R.id.titleView)
        mVideoNameTextView = findViewById(R.id.videoName)
        mVideoDescriptionTextView = findViewById(R.id.videoDescription)
        mPlayer = findViewById(R.id.videoPlayer)
        mPlayer?.setZOrderOnTop(true)
        val videoBg = findViewById<ConstraintLayout>(R.id.videoView)
        videoBg.setBackgroundColor(getResources().getColor(R.color.videoBgRed))

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val exercise = bundle.get("exercise") as Exercises
            loadViews(exercise)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.home_button -> {
                gotoHome()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadViews(exercise: Exercises) {
        val exerciseType= ExerciseType.getExerciseType(exercise.exercise)
        mTitleTextView?.text = exerciseType?.title
        mVideoNameTextView?.text = exercise.videoName
        mVideoDescriptionTextView?.text = exercise.videoDescription
        val uriPath = exercise.videoPath
        if (uriPath is String) {
            configureVideoView(uriPath)
        }
    }

    fun configureVideoView(uriPath: String) {
        mPlayer?.setVideoPath(uriPath)
        mediaController = MediaController(this)
        mediaController?.setAnchorView(mPlayer)
        mPlayer?.setMediaController(mediaController)
        mPlayer?.start()
    }
}
