package com.sparta.euphoria.Activities.Diet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Diet
import com.sparta.euphoria.Enums.DietType
import com.sparta.euphoria.Enums.Element
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Generic.EUSectionIndex
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.Section
import com.sparta.euphoria.R
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

class DietIndexActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var sectionIndex: EUSectionIndex? = null
    private var dietAdapter: DietAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dietindex)

        mTitleTextView = findViewById(R.id.titleView)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
        sectionIndex = findViewById(R.id.indexer)
        setTitle("DIET")

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val dietString = bundle.get("diet") as String
            val element = bundle.get("element") as Element

            println(dietString)
            println(element)

            val diet = DietType.getDietType(dietString)
            mTitleTextView?.text = diet?.title

            Thread(){
                val diets = DataBaseHelper.getDatabase(applicationContext).dietDao().getDietsForElementAndType(element.element, dietString)
                println(diets)
                createMapping(diets)
            }.start()
        }
    }

    fun createMapping(diets: List<Diet>) {
        val mapping = HashMap<String, ArrayList<Diet>>()

        for (diet in diets) {
            val name = diet.name
            val key = if (name != null) name.take(1) else "#"
            val data = mapping[key]

            if (data != null) {
                data.add(diet)
                mapping.put(key, data)
            }
            else {
                mapping.put(key, arrayListOf<Diet>(diet))
            }
        }

        createSections(mapping)
    }

    fun createSections(mapping: HashMap<String, ArrayList<Diet>>) {
        val sections = ArrayList<Section>()
        val positions = ArrayList<Int>()
        val keys = mapping.keys.sorted()

        for (i in 0..(keys.size - 1)) {
            val key = keys[i]
            val section = Section(i, 0, key, true)
            sections.add(section)
            positions.add(sections.size - 1)
            val diets = mapping[key]
            if (diets is ArrayList<Diet>) {
                for (j in 0 ..(diets.size - 1)) {
                    val diet = diets[j]
                    val row = Section(diet, i, j, false)
                    sections.add(row)
                }
            }
        }

        sectionIndex?.visibility = if (keys.size <= 1) View.INVISIBLE else View.VISIBLE
        val keysArray: Array<Any> = keys.toTypedArray()
        createAdapter(sections, positions, keysArray)
    }

    fun createAdapter(sections: List<Section>, positions: List<Int>, keys: Array<Any>) {
        runOnUiThread {
            if (dietAdapter == null) {
                dietAdapter = DietAdapter(
                    sections,
                    positions,
                    keys
                )
                recyclerView.adapter = dietAdapter
            }
            sectionIndex?.setRecyclerView(recyclerView)
            sectionIndex?.invalidate()
            dietAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val section = sections[position]
                    val item = section.item
                    if (item is Diet) {
                        gotoDietDetails(item)
                    }
                }
            })
        }
    }

    fun gotoDietDetails(diet: Diet) {
        val bundle = Bundle()
        bundle.putSerializable("diet", diet as Serializable)
        val intent = Intent(this, DietDetailsActivity::class.java)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    private class DietAdapter(var list: List<Section>, var positions: List<Int>, var keys: Array<Any>):
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
            val diet = model.item
            if (diet is Diet) {
                mTitleTextView?.text = diet.name
            }
        }
    }
}