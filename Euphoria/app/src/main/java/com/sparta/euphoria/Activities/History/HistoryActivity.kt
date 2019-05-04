package com.sparta.euphoria.Activities.History

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.Enums.HistoryType
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.R
import java.io.Serializable

class HistoryActivity: AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var historyAdapter: HistoryAdapter? = null
    val items = HistoryType.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("HISTORY")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if (historyAdapter == null) {
            historyAdapter = HistoryAdapter(items)
            recyclerView.adapter = historyAdapter

            historyAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    gotoNextActivity(position)
                }
            })
        }
    }

    fun gotoNextActivity(position: Int) {
        runOnUiThread {
            val item = items[position]
            val bundle = Bundle()
            bundle.putSerializable("historyType", item as Serializable)
            val intent = Intent(this, HistoryIndexActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class HistoryAdapter(private val list: Array<HistoryType>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

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

            fun bind(history: HistoryType) {
                mCharacterImage?.setImageResource(R.mipmap.oval_blue)
                mCharacterTextView?.text = history.character
                mTitleTextView?.text = history.title
                mAccessoryImage?.setImageResource(R.mipmap.rectangle_blue)
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