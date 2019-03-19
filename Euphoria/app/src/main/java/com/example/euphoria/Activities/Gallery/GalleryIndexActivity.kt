package com.example.euphoria.Activities.Gallery

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
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Video
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.Generic.ViewHolder
import com.example.euphoria.R
import kotlinx.android.synthetic.main.activity_videoindex.*
import kotlinx.android.synthetic.main.grid_adapter.view.*
import java.io.Serializable

class GalleryIndexActivity: AppCompatActivity() {
    private var mTitleTextView: TextView? = null
//    private var adapter: ExerciseIndexActivity.ExerciseAdapter? = null
    var exerciseGridView: GridView? = null
    var videos: List<Video> = emptyList()
    private var galleryAdapter: GalleryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_videoindex)
        mTitleTextView = findViewById(R.id.titleView)
        exerciseGridView = findViewById(R.id.gridView)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val title = bundle.get("gallery") as String
            mTitleTextView?.text = title

            Thread(){
                videos = DataBaseHelper.getDatabase(applicationContext).videoDao().getVideosForTitle(title)
                loadViews()
            }.start()
        }

        gridView.setOnItemClickListener { parent, view, position, id ->
            val video = videos[position]
            gotoNextActivity(video)
        }
    }

    fun loadViews() {
        runOnUiThread{
            galleryAdapter = GalleryAdapter(this, videos)
            exerciseGridView?.adapter = galleryAdapter
        }
    }

    fun gotoNextActivity(video: Video) {
        runOnUiThread{
            val bundle = Bundle()
            bundle.putSerializable("video", video as Serializable)

            val intent = Intent(this, GalleryViewActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class GalleryAdapter(var context: Context, var list: List<Video>): BaseAdapter() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val exercise = list[position]
            val cellView = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.grid_adapter, parent, false)
            val holder = cellView.tag as? ViewHolder ?: ViewHolder(cellView)
            cellView.tag = holder
            cellView.videoName.text = exercise.videoName
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