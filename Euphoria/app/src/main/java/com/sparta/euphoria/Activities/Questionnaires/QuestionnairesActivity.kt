package com.sparta.euphoria.Activities.Questionnaires

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaires
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R
import java.io.Serializable

class QuestionnairesActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var questionnairesAdapter: QuestionnaireAdapter? = null
    private var mFloatingActionButton: FloatingActionButton? = null

    var items: List<Questionnaires> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("QUESTIONNAIRES")

        mFloatingActionButton = findViewById(R.id.fab)
        mFloatingActionButton?.show()

        mFloatingActionButton?.setOnClickListener { view ->
            //TODO: need to add action buttons
        }

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread() {
            val customerId = EUUser.shared(this).customerId
            items = DataBaseHelper.getDatabase(applicationContext).questionnairesDao().getQuestionnaires(customerId)
            loadAdapter()
        }.start()
    }

    fun loadAdapter() {
        runOnUiThread{
            if (questionnairesAdapter == null) {
                questionnairesAdapter =
                    QuestionnaireAdapter(items)
                recyclerView.adapter = questionnairesAdapter
                questionnairesAdapter?.setOnItemClickListener(object :
                    OnItemClickListener {
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
            val bundle = Bundle()
            bundle.putSerializable("questionnaire", item as Serializable)

            val intent = Intent(this, QuestionnaireActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    fun didAnsweredQuestionnairesInEmail() {
        //TODO: create string and send email
    }

    private class QuestionnaireAdapter(private val list: List<Questionnaires>): RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder>() {

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

            fun bind(questionnaire: Questionnaires, title: String) {
                mCharacterImage?.setImageResource(R.mipmap.oval_blue)
                mCharacterTextView?.text = title
                mTitleTextView?.text = questionnaire.title
                mAccessoryImage?.setImageResource(R.mipmap.rectangle_blue)
            }
        }

        lateinit var listener: OnItemClickListener
        private var titles: List<String> = listOf("EQ", "Q2", "Q3", "Q4", "Q5")

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            val inflater = LayoutInflater.from(p0.context)
            return ViewHolder(
                inflater,
                p0
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0.bind(list[p1], titles[p1])

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}