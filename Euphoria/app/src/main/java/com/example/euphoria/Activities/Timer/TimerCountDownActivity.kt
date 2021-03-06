package com.example.euphoria.Activities.Timer

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Timer.Session
import com.example.euphoria.DataBase.Timer.Stop
import com.example.euphoria.Extensions.fullDuration
import com.example.euphoria.Generic.Constants.Companion.PAUSE
import com.example.euphoria.Generic.Constants.Companion.PLAY
import com.example.euphoria.R

class TimerCountDownActivity: AppCompatActivity() {

    private lateinit var session: Session
    private lateinit var stops: List<Stop>
    private lateinit var progressBarView: ProgressBar
    private lateinit var progressTextView: TextView
    private lateinit var playButton: ImageButton
    private lateinit var stopButton: ImageButton
    private var countDownTimer: CountDownTimer? = null
    private var tempStops = ArrayList<Stop>()

    var mProgress = 1
    var stopTime: Long = 0
    var endTime: Long = 0
    var milliSecondsRemaining: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timercountdown)

        progressBarView = findViewById(R.id.view_progress_bar)
        progressTextView = findViewById(R.id.timerTextView)
        playButton = findViewById(R.id.playButton)
        stopButton = findViewById(R.id.stopButton)
        playButton.tag = PLAY

        val makeVertical = RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        makeVertical.fillAfter = true
        progressBarView.startAnimation(makeVertical)
        progressBarView.secondaryProgress = 0
        progressBarView.progress = 0

        val bundle = intent.extras?.getBundle("bundle")
        if (bundle is Bundle) {
            session = bundle.get("session") as Session
            endTime = session.time
            Thread() {
                stops = DataBaseHelper.getDatabase(applicationContext).stopDao().getStop(session.uid)
                setTimerInitialValues()
            }.start()
        }
    }

    override fun onBackPressed() {
        countDownTimer?.cancel()
        finish()
    }

    fun setTimerInitialValues() {
        milliSecondsRemaining = session.time * 1000
        tempStops = ArrayList(stops)
        mProgress = 1
        stopTime = 0
        setSoundPlayTime()
        playButton.tag = PLAY
    }

    fun setTimerCountDown() {
        val countDownInterval: Long = 1000
        val totalMilliSeconds: Long = milliSecondsRemaining

        countDownTimer = object: CountDownTimer(totalMilliSeconds, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                milliSecondsRemaining = millisUntilFinished
                val secondsRemaining = millisUntilFinished / 1000
                val secondsCompleted = endTime - secondsRemaining
                progressTextView.text = secondsCompleted.fullDuration()
                setProgress(mProgress, endTime.toInt())
                mProgress = mProgress + 1
            }

            override fun onFinish() {
                progressTextView.text = endTime.fullDuration()
                setProgress(mProgress, endTime.toInt())
                playStoptime(endTime)
            }
        }

        countDownTimer?.start()

    }

    fun setProgress(currentTime: Int, maxTime: Int) {
        progressBarView.max = maxTime
        progressBarView.secondaryProgress = maxTime
        progressBarView.progress = currentTime
    }

    fun didTapStop(view: View) {
        countDownTimer?.cancel()
        setTimerInitialValues()
        setProgress(0, 0)
        progressTextView.text = 0.toLong().fullDuration()
    }

    fun didTapPlay(view: View) {
        if (playButton.tag == PLAY) {
            setTimerCountDown()
            playButton.tag = PAUSE
        }
        else {
            countDownTimer?.cancel()
            playButton.tag = PLAY
            mProgress = mProgress - 1
        }
    }

    fun playStoptime(seconds: Long) {
        if (seconds < stopTime) {
            return
        }
        tempStops.removeAt(0)
        setSoundPlayTime()
    }

    fun setSoundPlayTime() {
        if (tempStops.isEmpty()) {
            setTimerInitialValues()
            return
        }

        val stop = tempStops.first()
        stopTime = stop.time + stopTime
    }
}