package com.sparta.euphoria.Activities.Gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Video
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Generic.ViewHolder
import com.sparta.euphoria.R
import kotlinx.android.synthetic.main.activity_videoindex.*
import kotlinx.android.synthetic.main.grid_adapter.view.*
import java.io.Serializable

class VideosIndexActivity: AppCompatActivity() {
    private var mTitleTextView: TextView? = null
    var videosGridView: GridView? = null
    var videos: List<Video> = emptyList()
    private var videosAdapter: VideosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoindex)
        setTitle("VIDEOS")

        mTitleTextView = findViewById(R.id.titleView)
        videosGridView = findViewById(R.id.gridView)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val title = bundle.get("gallery") as String
            mTitleTextView?.text = title

            Thread(){
                videos = DataBaseHelper.getDatabase(applicationContext).videoDao().getVideosForTitle(title)
                loadViews()
            }.start()
        }

        videosGridView?.setOnItemClickListener { parent, view, position, id ->
            val video = videos[position]
            gotoNextActivity(video)
        }
    }

    fun loadViews() {
        runOnUiThread{
            videosAdapter = VideosAdapter(this, videos)
            videosGridView?.adapter = videosAdapter
        }
    }

    fun gotoNextActivity(video: Video) {
        runOnUiThread{
            val bundle = Bundle()
            bundle.putSerializable("video", video as Serializable)

            val intent = Intent(this, VideoViewActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class VideosAdapter(var context: Context, var list: List<Video>): BaseAdapter() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val video = list[position]
            val cellView = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.grid_adapter, parent, false)
            val holder = cellView.tag as? ViewHolder ?: ViewHolder(cellView)
            val resId = context.resources.getIdentifier(video.thumbnail, "mipmap", context.packageName)
            cellView.gridImageBg.setImageResource(resId)
            cellView.tag = holder
            cellView.name.text = video.videoName
            return cellView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}