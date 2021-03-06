package com.example.euphoria.Activities.Diet

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
import com.example.euphoria.Enums.DietType
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.Enums.Element
import com.example.euphoria.R
import java.io.Serializable

class DietActivity: AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dietAdapter: DietAdapter? = null

    var items: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread() {
            val db = DataBaseHelper.getDatabase(applicationContext)
            items = db.dietDao().getUniqueDietsForElement("Earth")
            updateViews()
        }.start()
    }

    fun updateViews() {
        runOnUiThread {
            if (dietAdapter == null) {
                dietAdapter = DietAdapter(items)
                recyclerView.adapter = dietAdapter

                dietAdapter?.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        gotoNextActivity(position)
                    }
                })
            }
        }
    }

    fun gotoNextActivity(position: Int) {
        runOnUiThread {
            val item = items[position]
            val element = Element.getElement("Earth")
            val bundle = Bundle()
            bundle.putSerializable("diet", item as Serializable)
            bundle.putSerializable("element", element as Serializable)

            val intent = Intent(this, DietIndexActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class DietAdapter(private val list: List<String>): RecyclerView.Adapter<DietAdapter.ViewHolder>() {

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

            fun bind(diet: DietType) {
                mCharacterTextView?.text = diet.character
                mTitleTextView?.text = diet.title
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
            val item = DietType.getDietType(list[p1])
            if (item != null) {
                p0.bind(item)
            }

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}