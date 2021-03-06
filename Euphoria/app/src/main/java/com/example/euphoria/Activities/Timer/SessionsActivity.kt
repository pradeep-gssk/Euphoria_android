package com.example.euphoria.Activities.Timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Timer.Session
import com.example.euphoria.Extensions.duration
import com.example.euphoria.Generic.BaseViewHolder
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.R

class SessionsActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var sessions: List<Session> = emptyList()
    private var sessionAdapter: SessionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onStart() {
        super.onStart()
        Thread() {
            sessions = DataBaseHelper.getDatabase(applicationContext).sessionDao().getSessions()
            loadRecyclerViews()
        }.start()
    }

    fun loadRecyclerViews() {
        runOnUiThread {
            if (sessionAdapter == null) {
                sessionAdapter = SessionAdapter(
                    this,
                    sessions)
            }
            else {
                sessionAdapter?.list = sessions
            }
            recyclerView.adapter = sessionAdapter

            sessionAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val session = sessions[position]
                    gotoShowSession(session)
                }
            })
        }
    }

    fun gotoShowSession(session: Session) {
        runOnUiThread {
            val bundle = Bundle()
            bundle.putSerializable("session", session)
            val intent = Intent(this, ShowSessionActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    fun didTapButton(view: View) {
        val intent = Intent(this, TimerSetupActivity::class.java)
        startActivity(intent)
    }

    private class SessionAdapter(var context: Context, var list: List<Session>): RecyclerView.Adapter<BaseViewHolder<Session>>() {
        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Session> {
            return SessionHolder(p0, R.layout.session_rowadapter)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: BaseViewHolder<Session>, p1: Int) {
            val section = list[p1]
            p0.bindData(section)

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }

    private class SessionHolder(parent: ViewGroup, layoutID: Int):
        BaseViewHolder<Session>(parent, layoutID) {
        private var mTitleTextView: TextView? = null
        private var mTimeTextView: TextView? = null
        private var mImageView: ImageView? = null

        init {
            mImageView = itemView.findViewById(R.id.imageview)
            mImageView?.visibility = GONE
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            mTimeTextView = itemView.findViewById(R.id.timeTextView)
        }

        override fun bindData(model: Session) {
            mTitleTextView?.text = model.name
            mTimeTextView?.text = model.time.duration()
        }
    }
}