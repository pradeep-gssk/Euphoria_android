package com.example.euphoria.Activities.Gallery

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
import com.example.euphoria.Enums.GalleryType
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.R
import java.io.Serializable

class GalleryActivity: AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var galleryAdapter: GalleryAdapter? = null
    val items = GalleryType.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if (galleryAdapter == null) {
            galleryAdapter = GalleryAdapter(items)
            recyclerView.adapter = galleryAdapter

            galleryAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    gotoNextActivity(position)
                }
            })
        }
    }

    fun gotoNextActivity(position: Int) {
        runOnUiThread {
            val item = items[position]

            if (item == GalleryType.videos) {
                val bundle = Bundle()
                bundle.putSerializable("gallery", item as Serializable)
                val intent = Intent(this, GalleryListActivity::class.java)
                intent.putExtra("bundle", bundle)
                startActivity(intent)
            }
        }
    }

    private class GalleryAdapter(private val list: Array<GalleryType>): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

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

            fun bind(gallery: GalleryType) {
                mCharacterTextView?.text = gallery.character
                mTitleTextView?.text = gallery.title
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