package com.example.euphoria.Activities.Timer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.example.euphoria.Models.EUEditText
import com.example.euphoria.Models.EUSession
import com.example.euphoria.Models.EUStop
import com.example.euphoria.R

class TimerSetupActivity: AppCompatActivity() {

    var selectedHours: Int = 0
    var selectedMinutes: Int = 0
    var selectedTime: Long = 0

    private var hourPicker: NumberPicker? = null
    private var minutesPicker: NumberPicker? = null
    private var mOKButton: Button? = null
    private var mHoursTextView: TextView? = null
    private var mMinutesTextView: TextView? = null
    private var mNameEditText: EUEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timersetup)

        hourPicker = findViewById(R.id.hours)
        minutesPicker = findViewById(R.id.minutes)
        mHoursTextView = findViewById(R.id.hoursTextView)
        mMinutesTextView = findViewById(R.id.minutesTextView)
        mOKButton = findViewById(R.id.okButton)
        mNameEditText = findViewById(R.id.nameEditText)

        mHoursTextView?.setText(R.string.hours)
        mMinutesTextView?.setText(R.string.minutes)

        hourPicker?.minValue = 0
        hourPicker?.maxValue = 24
        hourPicker?.isFocusableInTouchMode = false
        hourPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedHours = newVal

            if (selectedHours == 24) {
                selectedMinutes = 0
                minutesPicker?.value = 0
            }

            mHoursTextView?.text = if (selectedHours == 1) "Hour" else "Hours"

            selectedTime = ((selectedHours * 3600) + (selectedMinutes * 60)).toLong()
        }

        minutesPicker?.minValue = 0
        minutesPicker?.maxValue = 59
        minutesPicker?.wrapSelectorWheel = true
        minutesPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedMinutes = newVal

            if (selectedHours == 24 && selectedMinutes != 0) {
                selectedHours = 23
                hourPicker?.value = 23
            }

            selectedTime = ((selectedMinutes  * 60) + (selectedHours * 3600)).toLong()
        }
    }

    fun didTapOk(view: View) {
        if (selectedTime <= 0) {
            //TODO: show error
            return
        }

        val text = mNameEditText?.text?.toString()

        if (text == null) return

        if (text.trim().length <= 0) {
            //TODO: show error
            return
        }

        val session = EUSession(text, selectedTime, ArrayList())

        val bundle = Bundle()
        bundle.putSerializable("session", session)
        val intent = Intent(this, AddSessionActivity::class.java)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }
}