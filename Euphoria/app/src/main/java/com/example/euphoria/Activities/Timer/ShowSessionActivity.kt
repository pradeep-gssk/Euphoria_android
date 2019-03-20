package com.example.euphoria.Activities.Timer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Timer.Session
import com.example.euphoria.DataBase.Timer.Stop
import com.example.euphoria.Extensions.duration
import com.example.euphoria.Generic.BaseViewHolder
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.R

class ShowSessionActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var sessionAdapter: SessionAdapter? = null
    private lateinit var session: Session
    private lateinit var stops: List<Stop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)
        findViewById<ImageButton>(R.id.plus).visibility = GONE

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val bundle = intent.extras?.getBundle("bundle")
        if (bundle is Bundle) {
            session = bundle.get("session") as Session
            Thread() {
                stops = DataBaseHelper.getDatabase(applicationContext).stopDao().getStop(session.uid)
                setAdapter()
            }.start()
        }
    }

    fun setAdapter() {
        runOnUiThread {
            if (sessionAdapter == null) {
                sessionAdapter = SessionAdapter(session, stops)
            }
            recyclerView.adapter = sessionAdapter

            sessionAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    gotoTimerCountDown()
                }
            })
        }
    }

    private fun gotoTimerCountDown() {
        runOnUiThread {
            val bundle = Bundle()
            bundle.putSerializable("session", session)
            val intent = Intent(this, TimerCountDownActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class SessionAdapter(var session: Session, var stops: List<Stop>):
        RecyclerView.Adapter<BaseViewHolder<Any>>() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Any> {
            when (p1) {
                0 -> return SessionNameHolder(p0, R.layout.section_header_adapter)
                1 -> return SessionStartHolder(p0, R.layout.session_startadapter)
                else -> {
                    val position = p1-2
                    return SessionStopHolder(p0, R.layout.session_stopadapter, position)
                }
            }
        }

        override fun getItemCount(): Int {
            return stops.size + 2
        }

        override fun onBindViewHolder(p0: BaseViewHolder<Any>, p1: Int) {
            when (p1) {
                0-> p0.bindData(session)
                1 -> {
                    p0.bindData(session)
                    p0.itemView.setOnClickListener { v: View? ->
                        listener.onItemClick(v, p1)
                    }
                }
                else -> {
                    val position = p1-2
                    p0.bindData(stops[position])
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    private class SessionNameHolder(parent: ViewGroup,
                                    layoutID: Int):
        BaseViewHolder<Any>(parent, layoutID) {

        private var mTitleTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        override fun bindData(model: Any) {
            if (model is Session) {
                mTitleTextView?.text = model.name
            }
        }
    }

    private class SessionStartHolder(parent: ViewGroup,
                                     layoutID: Int):
        BaseViewHolder<Any>(parent, layoutID) {

        private var mTitleTextView: TextView? = null
        private var mTimeTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.startTitleTextView)
            mTimeTextView = itemView.findViewById(R.id.startTimeTextView)
        }

        override fun bindData(model: Any) {
            if (!(model is Session)){return}
            mTitleTextView?.setText(R.string.start)
            mTimeTextView?.text = model.time.duration()
        }
    }

    private class SessionStopHolder(parent: ViewGroup,
                                    layoutID: Int,
                                    val p1: Int):
        BaseViewHolder<Any>(parent, layoutID) {

        private var mStopTitleTextView: TextView? = null
        private var mStopTimeTextView: TextView? = null
        private var mSoundTextView: TextView? = null

        init {
            mStopTitleTextView = itemView.findViewById(R.id.stopTitleTextView)
            mStopTimeTextView = itemView.findViewById(R.id.stopTimeTextView)
            mSoundTextView = itemView.findViewById(R.id.soundTextView)
        }

        override fun bindData(model: Any) {
            if (!(model is Stop)) {return}
            val title = itemView.resources.getString(R.string.session) + " " + (p1 + 1).toString()
            mStopTitleTextView?.text = title
            mStopTimeTextView?.text = model.time.duration()
            mSoundTextView?.text = model.sound
        }
    }
}