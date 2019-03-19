package com.example.euphoria.Activities.Timer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Timer.Sound
import com.example.euphoria.Extensions.duration
import com.example.euphoria.Generic.BaseViewHolder
import com.example.euphoria.Generic.Constants
import com.example.euphoria.Generic.OnStopClickListener
import com.example.euphoria.Models.EUSession
import com.example.euphoria.Models.EUStop
import com.example.euphoria.R
import java.util.concurrent.TimeUnit

class AddSessionActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mPlusButton: ImageButton
    private lateinit var mSaveButton: Button
    private lateinit var session: EUSession
    private var numberOfStops = 0
    private var sessionAdapter: SessionAdapter? = null
    private var selectedPosition: Int = -1
    private var currentSelectedInterval: Long = 0
    private var remainingSelectedInterval: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        mPlusButton = findViewById(R.id.plus)
        mSaveButton = findViewById(R.id.saveButton)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        mSaveButton.setOnClickListener {
            Thread() {
                DataBaseHelper.getDatabase(applicationContext).saveSession(session)
                dismissActivity()
            }.start()
        }

        val bundle = intent.extras?.getBundle("bundle")
        if (bundle is Bundle) {
            session = bundle.get("session") as EUSession
            remainingSelectedInterval = session.timeInterval
        }
        setAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RESULT_SOUND && data is Intent) {
            val sound = data.getSerializableExtra("sound") as Sound
            saveSound(sound)
        }
    }

    fun dismissActivity() {
        runOnUiThread {
            val intent = Intent(baseContext, SessionsActivity::class.java)
            navigateUpTo(intent)
        }
    }

    fun setAdapter() {
        if (sessionAdapter == null) {
            sessionAdapter = SessionAdapter(
                session,
                numberOfStops)
            recyclerView.adapter = sessionAdapter
        }
        else {
            sessionAdapter?.numberOfStops = numberOfStops
            sessionAdapter?.notifyDataSetChanged()
        }

        sessionAdapter?.setOnStopClickListener(object : OnStopClickListener {
            override fun onStopClick(view: View?, position: Int) {

                if (position < session.stops.size) {
                    currentSelectedInterval = session.stops[position].timeInterval
                    remainingSelectedInterval = remainingSelectedInterval + currentSelectedInterval
                }

                selectedPosition = position
                showTimeDialog()
            }

            override fun onSoundClick(view: View?, position: Int) {
                selectedPosition = position
                gotoSounds()
            }
        })
    }

    fun showTimeDialog() {
        val hours = TimeUnit.SECONDS.toHours(remainingSelectedInterval)
        val minutes = TimeUnit.SECONDS.toMinutes(remainingSelectedInterval - (hours * 3600))
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.stoptime_sessiondialog)

        dialog.setOnDismissListener {
            enablePlusButton()
        }


        var selectedHours: Int = 0
        var selectedMinutes: Int = 0
        var selectedTime: Long = 0

        val hoursPicker = dialog.findViewById<NumberPicker>(R.id.hours)
        val minutesPicker = dialog.findViewById<NumberPicker>(R.id.minutes)
        val hoursTextView = dialog.findViewById<TextView>(R.id.hoursTextView)
        val minutesTextView = dialog.findViewById<TextView>(R.id.minutesTextView)
        val doneButton = dialog.findViewById<Button>(R.id.doneButton)

        hoursTextView.setText(R.string.hours)
        minutesTextView.setText(R.string.minutes)

        hoursPicker.minValue = 0
        hoursPicker.maxValue = hours.toInt()
        hoursPicker.isFocusableInTouchMode = false
        hoursPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            minutesPicker.maxValue = if (newVal.toLong() == hours) minutes.toInt() else 59

            selectedHours = newVal

            if (selectedHours == 24) {
                selectedMinutes = 0
                minutesPicker?.value = 0
            }

            hoursTextView?.text = if (newVal == 1) "Hour" else "Hours"
            selectedTime = ((selectedHours * 3600) + (selectedMinutes * 60)).toLong()
        }

        minutesPicker.minValue = 0
        minutesPicker.maxValue = if (hours.toInt() == 0) minutes.toInt() else 59
        minutesPicker.wrapSelectorWheel = true
        minutesPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedMinutes = newVal

            if (selectedHours == 24 && selectedMinutes != 0) {
                selectedHours = 23
                hoursPicker?.value = 23
            }

            selectedTime = ((selectedMinutes  * 60) + (selectedHours * 3600)).toLong()
        }

        doneButton.setOnClickListener {
            currentSelectedInterval = selectedTime
            dialog.dismiss()
            saveStopTime()

        }

        dialog.show()
    }

    fun saveStopTime() {
        if (selectedPosition <= -1) return
        val stops = session.stops

        if (selectedPosition >= stops.size) {
            val stop = EUStop(selectedPosition, null, currentSelectedInterval)
            stops.add(stop)
        }
        else {
            val stop = stops[selectedPosition]
            stop.timeInterval = currentSelectedInterval
        }
        enablePlusButton()
    }

    fun gotoSounds() {
        val intent = Intent(this, SoundsActivity::class.java)
        startActivityForResult(intent, Constants.RESULT_SOUND)
    }

    fun saveSound(sound: Sound) {
        if (selectedPosition <= -1) return
        val stops = session.stops

        if (selectedPosition >= stops.size) {
            val stop = EUStop(selectedPosition, sound, 0)
            stops.add(stop)
        }
        else {
            val stop = stops[selectedPosition]
            stop.sound = sound
        }
        enablePlusButton()
    }

    fun enablePlusButton() {
        setAdapter()
        if (selectedPosition <= -1) return
        val stop = session.stops[selectedPosition]
        if (currentSelectedInterval <= 0 || stop.sound == null) {
            return
        }

        remainingSelectedInterval = remainingSelectedInterval - currentSelectedInterval
        currentSelectedInterval = 0
        mSaveButton.visibility = if (remainingSelectedInterval <= 0) VISIBLE else GONE
        mPlusButton.visibility = if (remainingSelectedInterval <= 0) GONE else VISIBLE

        mPlusButton.isEnabled = true
    }

    fun didTapButton(view: View) {
        mPlusButton.isEnabled = false
        numberOfStops = numberOfStops + 1
        setAdapter()
    }

    private class SessionAdapter(var session: EUSession, var numberOfStops: Int):
        RecyclerView.Adapter<BaseViewHolder<EUSession>>() {

        lateinit var listener: OnStopClickListener

        fun setOnStopClickListener(listener: OnStopClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<EUSession> {
            when (p1) {
                0 -> return SessionNameHolder(p0, R.layout.section_header_adapter)
                1 -> return SessionStartHolder(p0, R.layout.session_startadapter)
                else -> {
                    val position = p1-2
                    return SessionStopHolder(p0, R.layout.session_stopadapter, position, listener)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return numberOfStops + 2
        }

        override fun onBindViewHolder(p0: BaseViewHolder<EUSession>, p1: Int) {
            p0.bindData(session)
        }
    }

    private class SessionNameHolder(parent: ViewGroup,
                                    layoutID: Int):
        BaseViewHolder<EUSession>(parent, layoutID) {

        private var mTitleTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        override fun bindData(model: EUSession) {
            mTitleTextView?.text = model.name
        }
    }

    private class SessionStartHolder(parent: ViewGroup,
                                     layoutID: Int):
        BaseViewHolder<EUSession>(parent, layoutID) {

        private var mTitleTextView: TextView? = null
        private var mTimeTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.startTitleTextView)
            mTimeTextView = itemView.findViewById(R.id.startTimeTextView)
        }

        override fun bindData(model: EUSession) {
            mTitleTextView?.setText(R.string.start)
            mTimeTextView?.text = model.timeInterval.duration()
        }
    }

    private class SessionStopHolder(parent: ViewGroup,
                                    layoutID: Int,
                                    val p1: Int,
                                    listener: OnStopClickListener):
        BaseViewHolder<EUSession>(parent, layoutID) {

        private var mStopCell: ConstraintLayout? = null
        private var mSoundCell: ConstraintLayout? = null
        private var mStopTitleTextView: TextView? = null
        private var mStopTimeTextView: TextView? = null
        private var mSoundTextView: TextView? = null

        private var _listener: OnStopClickListener

        init {
            _listener = listener
            mStopCell = itemView.findViewById(R.id.stopCell)
            mStopCell?.setOnClickListener { v: View? ->
                _listener.onStopClick(v, p1)
            }

            mSoundCell = itemView.findViewById(R.id.soundCell)

            mSoundCell?.setOnClickListener { v: View? ->
                _listener.onSoundClick(v, p1)
            }

            mStopTitleTextView = itemView.findViewById(R.id.stopTitleTextView)
            mStopTimeTextView = itemView.findViewById(R.id.stopTimeTextView)
            mSoundTextView = itemView.findViewById(R.id.soundTextView)
        }

        override fun bindData(model: EUSession) {
            val title = itemView.resources.getString(R.string.session) + " " + (p1 + 1).toString()
            mStopTitleTextView?.text = title

            if (p1 >= model.stops.size) {
                mStopTimeTextView?.setText(R.string.zeroTime)
                mSoundTextView?.setText(R.string.list)
                return
            }
            val stop = model.stops[p1]

            if (stop.timeInterval <= 0) {
                mStopTimeTextView?.setText(R.string.zeroTime)
            }
            else {
                mStopTimeTextView?.text = stop.timeInterval.duration()
            }

            if (stop.sound == null) {
                mSoundTextView?.setText(R.string.list)
            }
            else {
                mSoundTextView?.text = stop.sound?.name
            }
        }
    }
}