package com.sparta.euphoria.Activities.Timer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Timer.Sound
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Generic.Constants.Companion.RESULT_SOUND
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUSound
import com.sparta.euphoria.R

class SoundsActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var sounds: List<EUSound> = emptyList()
    private var soundsAdapter: SoundsAdapter? = null
    private var previousIndex = -1
    private var selectedSound: Sound? = null
    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)
        setTitle("TIMER")

        findViewById<ImageButton>(R.id.plus).visibility = View.GONE
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread() {
            val list = DataBaseHelper.getDatabase(applicationContext).soundDao().getSounds()
            val array = ArrayList<EUSound>()
            for (sound in list) {
                val item = EUSound(false, sound)
                array.add(item)
            }
            sounds = array
            loadAdapter()
        }.start()
    }

    fun loadAdapter() {
        runOnUiThread {
            if (soundsAdapter == null) {
                soundsAdapter = SoundsAdapter(
                    sounds)
                recyclerView.adapter = soundsAdapter
            }
            else {
                soundsAdapter?.list = sounds
                soundsAdapter?.notifyDataSetChanged()
            }

            soundsAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if (previousIndex == position) {
                        return
                    }

                    if (previousIndex > -1) {
                        val sound = sounds[previousIndex]
                        sound.isSelected = false
                    }

                    val sound = sounds[position]
                    sound.isSelected = true
                    previousIndex = position
                    selectedSound = sound.sound
                    playSound(sound)
                    loadAdapter()
                }
            })
        }
    }

    fun playSound(sound: EUSound) {
        val resId = this.resources.getIdentifier(sound.sound.resource, "raw", this.packageName)
        mp = MediaPlayer.create(this, resId)
        mp?.start()
    }

    override fun onBackPressed() {
        if (selectedSound == null) {
            finish()
            return
        }

        val data = Intent()
        data.putExtra("sound", selectedSound)
        setResult(RESULT_SOUND, data)
        finish()
    }

    private class SoundsAdapter(var list: List<EUSound>):
        RecyclerView.Adapter<BaseViewHolder<EUSound>>() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<EUSound> {
            return SoundHolder(p0, R.layout.session_startadapter)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: BaseViewHolder<EUSound>, p1: Int) {
            p0.bindData(list[p1])

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    private class SoundHolder(parent: ViewGroup, layoutID: Int):
        BaseViewHolder<EUSound>(parent, layoutID) {
        private var mTitleTextView: TextView? = null
        private var mImageView: ImageView? = null

        init {
            mImageView = itemView.findViewById(R.id.startImageview)
            mImageView?.scaleType = ImageView.ScaleType.CENTER
            mTitleTextView = itemView.findViewById(R.id.startTitleTextView)
            itemView.findViewById<TextView>(R.id.startTimeTextView).visibility = View.GONE
        }

        override fun bindData(model: EUSound) {
            mTitleTextView?.text = model.sound.name

            if (model.isSelected) {
                mImageView?.setImageResource(R.mipmap.checkmark_white)
            }
            else {
                mImageView?.setImageResource(0)
            }
        }
    }
}