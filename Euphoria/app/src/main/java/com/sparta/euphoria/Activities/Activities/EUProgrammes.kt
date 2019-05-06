package com.sparta.euphoria.Activities.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.Enums.ActivityType
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.Constants.Companion.PROGRAM_PATH
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.R

class EUProgrammes: AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var mTitleTextView: TextView? = null
    private var programmesAdapter: ProgrammesAdapter? = null

    val items = listOf<String>("10% early booking discount")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmes)
        setTitle("ACTIVITIES")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        mTitleTextView = findViewById(R.id.titleTextView)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val activityType = bundle.get("activityType") as ActivityType
            mTitleTextView?.text = activityType.title
            loadAdapter(activityType)
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

    fun loadAdapter(activityType: ActivityType) {
        if (programmesAdapter == null) {
            programmesAdapter = ProgrammesAdapter(activityType, items)
            recyclerView.adapter = programmesAdapter

            programmesAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    //gotoNextActivity(list[position])
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(PROGRAM_PATH)
                    startActivity(openURL)
                }
            })
        }
    }

    private class ProgrammesAdapter(private val activityType: ActivityType, private val list: List<String>):
        RecyclerView.Adapter<ProgrammesAdapter.ViewHolder>() {

        class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.programmes_adapter, parent, false)) {

            private var mAccessoryImage: ImageView? = null
            private var mTitleTextView: TextView? = null

            init {
                mAccessoryImage = itemView.findViewById(R.id.accessoryImage)
                mTitleTextView = itemView.findViewById(R.id.titleTextView)
            }

            fun bind(item: String, activityType: ActivityType) {
                mAccessoryImage?.setImageResource(activityType.accessoryImage)
                mTitleTextView?.text = item
            }
        }

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProgrammesAdapter.ViewHolder {
            val inflater = LayoutInflater.from(p0.context)
            return ViewHolder(inflater, p0)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: ProgrammesAdapter.ViewHolder, p1: Int) {
            p0.bind(list[p1], activityType)

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}