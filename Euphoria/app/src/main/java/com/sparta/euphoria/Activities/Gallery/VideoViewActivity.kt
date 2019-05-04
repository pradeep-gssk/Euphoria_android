package com.sparta.euphoria.Activities.Gallery

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.sparta.euphoria.DataBase.Video
import com.sparta.euphoria.R

class VideoViewActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mVideoNameTextView: TextView? = null
    private var mVideoDescriptionTextView: TextView? = null
    private var mediaController: MediaController? = null
    private var mPlayer: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoview)
        setTitle("VIDEOS")

        mTitleTextView = findViewById(R.id.titleView)
        mVideoNameTextView = findViewById(R.id.videoName)
        mVideoDescriptionTextView = findViewById(R.id.videoDescription)
        mPlayer = findViewById(R.id.videoPlayer)
        mPlayer?.setZOrderOnTop(true)
        val videoBg = findViewById<ConstraintLayout>(R.id.videoView)
        videoBg.setBackgroundColor(getResources().getColor(R.color.videoBgGreen))

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val video = bundle.get("video") as Video
            loadViews(video)
        }
    }

    fun loadViews(video: Video) {
        mTitleTextView?.text = video.title
        mVideoNameTextView?.text = video.videoName
        mVideoDescriptionTextView?.text = video.videoDescription
        val uriPath = video.videoPath
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