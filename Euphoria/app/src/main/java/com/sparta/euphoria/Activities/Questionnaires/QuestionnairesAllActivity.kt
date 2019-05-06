package com.sparta.euphoria.Activities.Questionnaires

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Diet
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaire
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.Model.Section
import com.sparta.euphoria.R

class QuestionnairesAllActivity: AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var questionnairesAdapter: QuestionnairesAllAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("QUESTIONNAIRES")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)

        Thread {
            val customerId = EUUser.shared(this).customerId
            val results = DataBaseHelper.getDatabase(this).fetchAnsweredQuestionnairesList(customerId)
            createAdapter(results)
        }.start()
    }

    fun createAdapter(sections: List<Section>) {
        runOnUiThread {
            if (questionnairesAdapter == null) {
                questionnairesAdapter = QuestionnairesAllAdapter(sections)
                recyclerView.adapter = questionnairesAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.home_button -> {
                gotoHome()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class QuestionnairesAllAdapter(var list: List<Section>):
        RecyclerView.Adapter<BaseViewHolder<Section>>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Section> {
            val section = list[p1]
            if (section.isHeader) {
                return SectionHolder(p0, R.layout.section_header_adapter)
            }
            return RowHolder(p0, R.layout.questionnaire_all_row_adapter)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: BaseViewHolder<Section>, p1: Int) {
            val section = list[p1]
            p0.bindData(section)
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
        private var mQuestionTextView: TextView? = null
        private var mAnswerTextView: TextView? = null
        private var mDetailsTextView: TextView? = null


        init {
            mQuestionTextView = itemView.findViewById(R.id.questionTextView)
            mAnswerTextView = itemView.findViewById(R.id.answerTextView)
            mDetailsTextView = itemView.findViewById(R.id.detailTextView)
        }

        override fun bindData(model: Section) {
            val questionnaire = model.item
            if (questionnaire is Questionnaire) {
                mQuestionTextView?.text = questionnaire.question
                mAnswerTextView?.text = questionnaire.answer
                mDetailsTextView?.text = questionnaire.details

                if (questionnaire.answer.isNullOrEmpty()) {
                    mAnswerTextView?.visibility = GONE
                }

                if (questionnaire.details.isNullOrEmpty()) {
                    mDetailsTextView?.visibility = GONE
                }
            }
        }
    }
}