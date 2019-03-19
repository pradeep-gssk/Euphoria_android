package com.example.euphoria.Activities.Questionnaires

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.euphoria.Enums.QuestionnaireType
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Questionnaires.Questionnaires
import com.example.euphoria.R
import java.io.Serializable

class QuestionnairesActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var questionnairesAdapter: QuestionnaireAdapter? = null

    var items: List<Questionnaires> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread() {
            items = DataBaseHelper.getDatabase(applicationContext).questionnairesDao().getAll()
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

            fun bind(questionnaire: QuestionnaireType) {
                mCharacterTextView?.text = questionnaire.character
                mTitleTextView?.text = questionnaire.questionnaire
            }
        }

        lateinit var listener: OnItemClickListener

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
            val title = list[p1].title

            if (title is String) {
                val questionnaireType = QuestionnaireType.getQuestionnaireType(title)
                if (questionnaireType is QuestionnaireType) {
                    p0.bind(questionnaireType)
                }
            }

            p0.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(v, p1)
            }
        }
    }
}
