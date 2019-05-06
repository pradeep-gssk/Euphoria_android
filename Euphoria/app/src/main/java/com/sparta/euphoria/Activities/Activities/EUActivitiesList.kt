package com.sparta.euphoria.Activities.Activities

import android.content.Intent
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
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.R
import java.io.Serializable

class EUActivitiesList: AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var activityAdapter: ActivityAdapter? = null
    val items = ActivityType.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("ACTIVITIES")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if (activityAdapter == null) {
            activityAdapter = ActivityAdapter(items)
            recyclerView.adapter = activityAdapter

            activityAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    gotoNextActivity(position)
                }
            })
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

    fun gotoNextActivity(position: Int) {
        val activityType = ActivityType.getActivityType(position)

        runOnUiThread {
            when(activityType) {
                ActivityType.calendar -> {
                    val item = items[position]
                    val bundle = Bundle()
                    bundle.putSerializable("activityType", item as Serializable)
                    val intent = Intent(this, EUCalendarActivity::class.java)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }

                ActivityType.programmes -> {
                    val item = items[position]
                    val bundle = Bundle()
                    bundle.putSerializable("activityType", item as Serializable)
                    val intent = Intent(this, EUProgrammes::class.java)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
            }
        }
    }

    private class ActivityAdapter(private val list: Array<ActivityType>): RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

        class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.recycleview_adapter, parent, false)) {

            private var mCharacterImage: ImageView? = null
            private var mCharacterTextView: TextView? = null
            private var mTitleTextView: TextView? = null
            private var mAccessoryImage: ImageView? = null

            init {
                mCharacterImage = itemView.findViewById(R.id.characterImage)
                mCharacterTextView = itemView.findViewById(R.id.characterTextView)
                mTitleTextView = itemView.findViewById(R.id.titleTextView)
                mAccessoryImage = itemView.findViewById(R.id.accessoryImage)
            }

            fun bind(activity: ActivityType) {
                mCharacterImage?.setImageResource(activity.characterImage)
                mCharacterTextView?.text = activity.character
                mTitleTextView?.text = activity.title
                mAccessoryImage?.setImageResource(activity.accessoryImage)
            }
        }

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            val inflater = LayoutInflater.from(p0.context)
            return ViewHolder(inflater, p0)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            val item = (list[p1])
            p0.bind(item)

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}