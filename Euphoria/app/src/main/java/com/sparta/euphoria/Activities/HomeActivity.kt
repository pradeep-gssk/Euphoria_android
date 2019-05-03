package com.sparta.euphoria.Activities

import android.content.Intent
import android.graphics.Color
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
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.Enums.Element
import com.sparta.euphoria.Enums.HomeType
import com.sparta.euphoria.Extensions.findElement
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R

class HomeActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var homeAdapter: HomeAdapter? = null
    private var selectedElement: Element = Element.Earth
    private var questionnaire1Answered = false

    val items: Array<HomeType> by lazy {
        return@lazy HomeType.values()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("HOME")
        getSelectedElement()
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun loadAdapter() {
        runOnUiThread {
            if (homeAdapter == null) {
                homeAdapter = HomeAdapter(items, questionnaire1Answered)
                recyclerView.adapter = homeAdapter

                homeAdapter?.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        gotoNextActivity(position)
                    }
                })
            }
            else {
                homeAdapter?.questionnaire1Answered = questionnaire1Answered
                homeAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun getSelectedElement() {
        val customerId = EUUser.shared(this).customerId
        Thread() {
            val questionnaires = DataBaseHelper.getDatabase(this).questionnairesDao().getQuestionnaire(1, customerId)
            selectedElement = this.findElement(this, questionnaires.uid)
            questionnaire1Answered = DataBaseHelper.getDatabase(this).checkIfAllAnswered(1, customerId)
            loadAdapter()
        }.start()
    }

    fun gotoNextActivity(position: Int) {
        val item = items[position]
        runOnUiThread {
            val intent = Intent(this, item.cls)
            startActivity(intent)
        }
    }

    private class HomeAdapter(private val list: Array<HomeType>, var questionnaire1Answered: Boolean) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
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

            if (p1 == 1 || p1 == 2) {
                if (questionnaire1Answered == false) {
                    p0.itemView.alpha = 0.6F
                    p0.itemView.setBackgroundColor(Color.parseColor("#c7c8ca"))
                }
                else {
                    p0.itemView.alpha = 1F
                    p0.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            p0.itemView.setOnClickListener { v: View? ->
                if ((p1 == 1 || p1 == 2) && questionnaire1Answered == false) {
                    return@setOnClickListener
                }
                listener.onItemClick(v, p1)
            }
        }
    }
}