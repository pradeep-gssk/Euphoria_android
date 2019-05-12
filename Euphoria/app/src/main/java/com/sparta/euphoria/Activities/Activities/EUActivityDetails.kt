package com.sparta.euphoria.Activities.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.sparta.euphoria.Enums.ActivityType
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Model.EUActivity
import com.sparta.euphoria.R

class EUActivityDetails: AppCompatActivity() {
    private var mTitleTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setTitle("ACTIVITIES")

        mTitleTextView = findViewById(R.id.titleTextView)
        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val activityType = bundle.get("activityType") as ActivityType
            val activity = bundle.get("activity") as EUActivity
            mTitleTextView?.text = activityType.title

            findViewById<TextView>(R.id.treatmentDescription).text = activity.trtDescription
            findViewById<TextView>(R.id.therapist).text = activity.appointmentTherapist
            findViewById<TextView>(R.id.priceTextView).text = activity.trtPrice.toString()
            findViewById<TextView>(R.id.discountPercentTextView).text = activity.discountPrcnt.toString() + "%"
            findViewById<TextView>(R.id.discountTextView).text = activity.discountValue.toString()
            findViewById<TextView>(R.id.dateTextView).text = activity.appointmentDate
            findViewById<TextView>(R.id.durationTextView).text = activity.trtDuration.toString()
            findViewById<TextView>(R.id.startTimeTextView).text = activity.appointmentStartTime
            findViewById<TextView>(R.id.endTimeTextView).text = activity.appointmentEndTime
            findViewById<TextView>(R.id.roomTextView).text = activity.appointmentRoom
            findViewById<TextView>(R.id.roomNumberTextView).text = activity.pmsRoom
            findViewById<TextView>(R.id.productDescription).text = activity.prodDescr
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