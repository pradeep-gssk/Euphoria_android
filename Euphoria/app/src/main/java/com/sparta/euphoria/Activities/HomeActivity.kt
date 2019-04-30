package com.sparta.euphoria.Activities

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
import com.sparta.euphoria.Enums.HomeType
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.R

class HomeActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var homeAdapter: HomeAdapter? = null

    val items: Array<HomeType> by lazy {
        return@lazy HomeType.values()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("Home")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if (homeAdapter == null) {
            homeAdapter = HomeAdapter(items)
            recyclerView.adapter = homeAdapter

            homeAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    gotoNextActivity(position)
                }
            })
        }
    }

    fun gotoNextActivity(position: Int) {
        val item = items[position]
        runOnUiThread {
//            val intent = Intent(this, item.cls)
//            startActivity(intent)
        }
    }

    private class HomeAdapter(private val list: Array<HomeType>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
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

            fun bind(homeType: HomeType) {
                mCharacterImage?.setImageResource(homeType.characterImage)
                mCharacterTextView?.text = homeType.character
                mTitleTextView?.text = homeType.title
                mAccessoryImage?.setImageResource(homeType.accessoryImage)
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
            p0.bind(list[p1])
            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}