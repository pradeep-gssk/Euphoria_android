package com.sparta.euphoria.Activities.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.sparta.euphoria.Enums.ActivityType
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.R

class EUCalendarActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setTitle("ACTIVITIES")

        mTitleTextView = findViewById(R.id.titleTextView)
        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val activityType = bundle.get("activityType") as ActivityType
            mTitleTextView?.text = activityType.title
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

}