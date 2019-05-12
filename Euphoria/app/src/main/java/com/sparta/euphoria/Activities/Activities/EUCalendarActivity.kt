package com.sparta.euphoria.Activities.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.Enums.ActivityType
import com.sparta.euphoria.Extensions.dateMonthYearString
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUActivity
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.Networking.ApiClient
import com.sparta.euphoria.R
import java.util.*
import kotlin.collections.ArrayList
import java.io.Serializable

class EUCalendarActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var calendarView: CalendarView? = null
    private var calendarAdapter: CalendarAdapter? = null
    private var activities = ArrayList<EUActivity>()
    private var tempActivities = emptyList<EUActivity>()
    private var activityType = ActivityType.calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setTitle("ACTIVITIES")

        mTitleTextView = findViewById(R.id.titleTextView)
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            activityType = bundle.get("activityType") as ActivityType
            mTitleTextView?.text = activityType.title
        }
        loadAdapter(tempActivities)
        fetchActivities()

        calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateString = calendar.timeInMillis.dateMonthYearString()
            filterActivities(dateString)
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

    fun fetchActivities() {
        val progress = ProgressDialog(this)
        progress.setTitle("Fetching Activities")
        progress.setCancelable(false)
        progress.show()

        val customerId = EUUser.shared(this).customerId
        ApiClient(this).requestActivities(customerId, success = {activities ->
            progress.dismiss()
            this.activities = activities
            val date = calendarView?.date
            if (date is Long) {
                filterActivities(date.dateMonthYearString())
            }

        }, failure = { message ->
            progress.dismiss()
            println(message)
            //TODO: Show error
        })
    }

    fun filterActivities(dateString: String) {
        tempActivities = activities.filter {activity ->
            (activity.appointmentDate == dateString)
        }
        loadAdapter(tempActivities)
    }

    fun loadAdapter(list: List<EUActivity>) {
        if (calendarAdapter == null) {
            calendarAdapter = CalendarAdapter(list)
        }
        else {
            calendarAdapter?.list = list
        }

        recyclerView.adapter = calendarAdapter

        calendarAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                gotoNextActivity(position)
            }
        })
    }


    private fun gotoNextActivity(position: Int) {
        runOnUiThread {
            val item = tempActivities[position]
            val bundle = Bundle()
            bundle.putSerializable("activity", item as Serializable)
            bundle.putSerializable("activityType", activityType as Serializable)
            val intent = Intent(this, EUActivityDetails::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class CalendarAdapter(var list: List<EUActivity>):
        RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

        class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.calendar_adapter, parent, false)) {

            private var mAccessoryImage: ImageView? = null
            private var mTimeTextView: TextView? = null
            private var mTitleTextView: TextView? = null

            init {
                mAccessoryImage = itemView.findViewById(R.id.accessoryImage)
                mTimeTextView = itemView.findViewById(R.id.timeTextView)
                mTitleTextView = itemView.findViewById(R.id.titleTextView)
            }

            fun bind(p1: Int, item: EUActivity) {

                when(p1%3) {
                    0 -> {
                        mAccessoryImage?.setImageResource(R.mipmap.rectangle_beige)
                    }

                    1 -> {
                        mAccessoryImage?.setImageResource(R.mipmap.rectangle_light_green)
                    }

                    2 -> {
                        mAccessoryImage?.setImageResource(R.mipmap.rectangle_brown)
                    }
                }

                mTimeTextView?.text = item.appointmentStartTime + "-" + item.appointmentEndTime
                mTitleTextView?.text = item.trtDescription
            }
        }

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CalendarAdapter.ViewHolder {
            val inflater = LayoutInflater.from(p0.context)
            return ViewHolder(inflater, p0)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: CalendarAdapter.ViewHolder, p1: Int) {
            p0.bind(p1, list[p1])

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}