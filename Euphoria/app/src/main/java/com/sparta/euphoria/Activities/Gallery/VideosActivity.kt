package com.sparta.euphoria.Activities.Gallery

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.R
import java.io.Serializable

class VideosActivity: AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var videosAdapter: VideosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("VIDEOS")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread(){
            val list = DataBaseHelper.getDatabase(applicationContext).videoDao().getUniqueVideoTitle()
            updateViews(list)
        }.start()
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

    fun updateViews(list: List<String>) {
        runOnUiThread {
            if (videosAdapter == null) {
                videosAdapter = VideosAdapter(list)
                recyclerView.adapter = videosAdapter

                videosAdapter?.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        gotoNextActivity(list[position])
                    }
                })
            }
        }
    }

    fun gotoNextActivity(item: String) {
        runOnUiThread {
            val bundle = Bundle()
            bundle.putSerializable("gallery", item as Serializable)

            val intent = Intent(this, VideosIndexActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class VideosAdapter(private val list: List<String>): RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

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

            fun bind(item: String) {
                mCharacterImage?.setImageResource(R.mipmap.oval_green)
                mCharacterTextView?.text = item.take(1)
                mTitleTextView?.text = item
                mAccessoryImage?.setImageResource(R.mipmap.rectangle_green)
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