package com.example.euphoria.Activities.History

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SectionIndexer
import android.widget.TextView
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.History
import com.example.euphoria.Enums.HistoryType
import com.example.euphoria.Extensions.dateMonthString
import com.example.euphoria.Extensions.monthYearString
import com.example.euphoria.Generic.BaseViewHolder
import com.example.euphoria.Generic.EUSectionIndex
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.Models.Section
import com.example.euphoria.R
import java.io.Serializable
import java.util.*

data class EUHistory(val history: History, val title: String): Serializable

class HistoryIndexActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mCameraButton: ImageButton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var sectionIndex: EUSectionIndex? = null
    private var historyAdapter: HistoryAdapter? = null
    private var historyType = HistoryType.face

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historyindex)

        mCameraButton = findViewById(R.id.cameraButton)
        mCameraButton?.visibility = VISIBLE
        mTitleTextView = findViewById(R.id.titleView)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
        sectionIndex = findViewById(R.id.indexer)

        mCameraButton?.setOnClickListener {
            gotoTakePhotoActivity()
        }

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            historyType = bundle.get("historyType") as HistoryType
            mTitleTextView?.text = historyType.title
        }
    }

    override fun onStart() {
        super.onStart()
        Thread() {
            val histories = DataBaseHelper.getDatabase(applicationContext).historyDao()
                .getHistoryForImageType(historyType.image)
            createMapping(histories)
        }.start()
    }

    fun createMapping(histories: List<History>) {
        var mapping = HashMap<String, ArrayList<EUHistory>>()
        for (history in histories) {
            val date = Date(history.date)
            val key = date.monthYearString()
            var data = mapping[key]

            if (data != null) {
                val history = EUHistory(history, date.dateMonthString())
                data.add(history)
                mapping.put(key, data)
            }
            else {
                val history = EUHistory(history, date.dateMonthString())
                mapping.put(key, arrayListOf<EUHistory>(history))
            }
        }
        createSections(mapping)
    }

    fun createSections(mapping: HashMap<String, ArrayList<EUHistory>>) {
        val sections = ArrayList<Section>()
        var positions = ArrayList<Int>()
        val keys = mapping.keys.sorted()

        for (i in 0..(keys.size - 1)) {
            val key = keys[i]
            val section = Section(i, 0, key, true)
            sections.add(section)
            positions.add(sections.size - 1)
            val histories = mapping[key]
            if (histories is ArrayList<EUHistory>) {
                for (j in 0 ..(histories.size - 1)) {
                    val history = histories[j]
                    val row = Section(history, i, j, false)
                    sections.add(row)
                }
            }
        }

        sectionIndex?.visibility = if (keys.size <= 1) INVISIBLE else VISIBLE
        val keysArray: Array<Any> = keys.toTypedArray()
        createAdapter(sections, positions, keysArray)
    }

    fun createAdapter(sections: List<Section>, positions: List<Int>, keys: Array<Any>) {
        runOnUiThread {
            if (historyAdapter == null) {
                historyAdapter = HistoryAdapter(
                    sections,
                    positions,
                    keys
                )
            }
            else {
                historyAdapter?.list = sections
                historyAdapter?.positions = positions
                historyAdapter?.keys = keys
            }
            recyclerView.adapter = historyAdapter
            sectionIndex?.setRecyclerView(recyclerView)
            sectionIndex?.invalidate()
            historyAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val item = sections[position].item
                    if (item is EUHistory) {
                        gotoShowPhotoActivity(item)
                    }
                }
            })
        }
    }

    fun gotoTakePhotoActivity() {
        runOnUiThread {
            val bundle = Bundle()
            bundle.putSerializable("historyType", historyType as Serializable)
            val intent = Intent(this, HistoryPhotoActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    fun gotoShowPhotoActivity(history: EUHistory) {
        runOnUiThread {
            val bundle = Bundle()
            bundle.putSerializable("historyType", historyType as Serializable)
            bundle.putSerializable("history", history as Serializable)
            val intent = Intent(this, HistoryPhotoViewActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class HistoryAdapter(var list: List<Section>, var positions: List<Int>, var keys: Array<Any>):
        RecyclerView.Adapter<BaseViewHolder<Section>>(), SectionIndexer {

        override fun getSections(): Array<Any> {
            return keys
        }

        override fun getSectionForPosition(position: Int): Int {
            return 0
        }

        override fun getPositionForSection(sectionIndex: Int): Int {
            return positions[sectionIndex]
        }

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Section> {
            val section = list[p1]
            if (section.isHeader) {
                return SectionHolder(p0, R.layout.section_header_adapter)
            }
            return RowHolder(p0, R.layout.row_adapter)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: BaseViewHolder<Section>, p1: Int) {
            val section = list[p1]
            p0.bindData(section)

            if (!section.isHeader) {
                p0.itemView.setOnClickListener { v: View? ->
                    listener.onItemClick(v, p1)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    private class SectionHolder(parent: ViewGroup, layoutID: Int):
        BaseViewHolder<Section>(parent, layoutID) {
        private var mTitleTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
        }

        override fun bindData(model: Section) {
            val title = model.title

            if (title is String) {
                mTitleTextView?.text = title
            }
        }
    }

    private class RowHolder(parent: ViewGroup, layoutID: Int):
        BaseViewHolder<Section>(parent, layoutID) {
        private var mTitleTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
        }

        override fun bindData(model: Section) {
            val history = model.item
            if (history is EUHistory) {
                mTitleTextView?.text = history.title
            }
        }
    }
}