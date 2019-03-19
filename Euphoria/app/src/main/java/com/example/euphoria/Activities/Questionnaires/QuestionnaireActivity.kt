package com.example.euphoria.Activities.Questionnaires

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Questionnaires.Option
import com.example.euphoria.DataBase.Questionnaires.Questionnaire
import com.example.euphoria.DataBase.Questionnaires.Questionnaires
import com.example.euphoria.Enums.OptionType
import com.example.euphoria.Extensions.boolValue
import com.example.euphoria.Generic.BaseViewHolder
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.Models.Section
import com.example.euphoria.R

class QuestionnaireActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mQuestionTextView: TextView? = null
    private var mPreviousButton: ImageButton? = null
    private var mNextButton: ImageButton? = null
    private var mToolbar: RelativeLayout? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var question: Questionnaire
    private var optionAdapter: QuetionnaireAdapter? = null
    var sections = ArrayList<Section>()

    var questionsList: List<Questionnaire> = emptyList()

    var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        mTitleTextView = findViewById(R.id.titleView)
        mQuestionTextView = findViewById(R.id.questionView)
        mPreviousButton = findViewById(R.id.previous)
        mNextButton = findViewById(R.id.next)
        mToolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val questionnaire = bundle.get("questionnaire") as Questionnaires
            mTitleTextView?.text = questionnaire.title
            Thread() {
                questionsList = DataBaseHelper.getDatabase(applicationContext).questionnaireDao()
                    .getChildQuestionnaire(questionnaire.uid)
                showQuestionnaire()
            }.start()
        }
    }

    fun showQuestionnaire() {
        question = questionsList[currentIndex]

        runOnUiThread {
            mQuestionTextView?.text = question.question

            if (currentIndex == 0) {
                mPreviousButton?.isEnabled = false
                mNextButton?.isEnabled = true
            } else if (currentIndex >= (questionsList.size - 1)) {
                mNextButton?.isEnabled = false
                mPreviousButton?.isEnabled = true
            } else {
                mNextButton?.isEnabled = true
                mPreviousButton?.isEnabled = true
            }
        }

        setSections()
    }

    fun setSections() {
        Thread() {
            val options = DataBaseHelper.getDatabase(applicationContext).optionDao().getChildOptions(question.uid)
            sections.clear()

            for (i in 0..(options.size - 1)) {
                val obj = options[i]
                val section = Section(obj, 0 , i)
                sections.add(section)
            }

            val optionType = OptionType.getOptionType(question.optionType)

            when(optionType) {
                OptionType.always -> {
                    val section = Section(optionType.resId, 1, 0)
                    sections.add(section)
                }

                OptionType.toggle -> {
                    if (question.answer.boolValue()) {
                        val section = Section(optionType.resId, 1, 0)
                        sections.add(section)
                    }
                }
                else -> { }
            }
            loadAdapterWithSections()
        }.start()
    }

    fun loadAdapterWithSections() {
        runOnUiThread {
            if (optionAdapter == null) {
                optionAdapter = QuetionnaireAdapter(
                    sections,
                    question.answer,
                    question.details,
                    this
                )
                recyclerView.adapter = optionAdapter
            }
            else {
                optionAdapter?.list = sections
                optionAdapter?.answer = question.answer
                optionAdapter?.detail = question.details
                optionAdapter?.notifyDataSetChanged()
            }

            optionAdapter?.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val section = sections[position]
                    val item = section.item
                    if (section.section == 0 && (item is Option)) {
                        updateAnswer(item.option)
                    }
                }
            })
        }
    }

    fun updateAnswer(answer: String?) {
        Thread() {
            DataBaseHelper.getDatabase(applicationContext).questionnaireDao().updateAnswer(answer, question.uid)
            question.answer = answer

            val optionType = OptionType.getOptionType(question.optionType)
            when(optionType) {
                OptionType.always -> {
                    updateDetails(null)
                }

                OptionType.toggle -> {
                    if (!question.answer.boolValue()) {
                        updateDetails(null)
                    }
                }
                else -> { }
            }
            setSections()
        }.start()
    }

    fun updateDetails(details: String?) {
        Thread() {
            DataBaseHelper.getDatabase(applicationContext).questionnaireDao().updateDetail(details, question.uid)
            question.details = details
        }.start()
    }

    fun didClickPrevious(view: View) {
        currentIndex -= 1
        showQuestionnaire()
    }

    fun didClickNext(view: View) {
        currentIndex += 1
        showQuestionnaire()
    }

    private class QuetionnaireAdapter(var list: List<Section>, var answer: String?, var detail: String?, var activity: QuestionnaireActivity):
        RecyclerView.Adapter<BaseViewHolder<Section>>() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Section> {
            if (p1 == 1) {
                return OtherHolder(p0, R.layout.other_adapter, detail, activity)
            }

            return OptionHolder(p0, R.layout.option_adapter, answer)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: BaseViewHolder<Section>, p1: Int) {
            if (p0 is OptionHolder) {
                p0.answer = answer
            }
            else if (p0 is OtherHolder) {
                p0.detail = detail
            }

            val section = list[p1]
            p0.bindData(section)

            if (section.section == 0) {
                p0.itemView.setOnClickListener { v: View? ->
                    listener.onItemClick(v, p1)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            val section = list[position]
            return section.section
        }
    }

    private class OptionHolder(parent: ViewGroup, layoutID: Int,
                               var answer: String?):
        BaseViewHolder<Section>(parent, layoutID) {
        private var mTitleTextView: TextView? = null
        private var mAccessoryImage: ImageView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            mAccessoryImage = itemView.findViewById(R.id.accessoryImage)
        }

        override fun bindData(model: Section) {
            val item = model.item

            if (model.section == 0 && (item is Option)) {
                mTitleTextView?.text = item.option
                if (item.option == answer) {
                    mAccessoryImage?.setImageResource(R.mipmap.ic_launcher)
                }
                else {
                    mAccessoryImage?.setImageResource(0)
                }
            }
        }
    }

    private class OtherHolder(parent: ViewGroup, layoutID: Int,
                              var detail: String?,
                              var activity: QuestionnaireActivity):
        BaseViewHolder<Section>(parent, layoutID) {
        private var mOtherEditText: EditText? = null

        init {
            mOtherEditText = itemView.findViewById(R.id.otherEditText)
            mOtherEditText?.setOnFocusChangeListener { v, hasFocus ->
                activity.mToolbar?.visibility = if (hasFocus) View.INVISIBLE else View.VISIBLE
                if (!hasFocus) {
                    activity.updateDetails(mOtherEditText?.text.toString())
                }
            }
        }

        override fun bindData(model: Section) {
            val item = model.item
            if (model.section == 1 && (item is Int)) {
                mOtherEditText?.setHint(item)
                mOtherEditText?.setText(detail)
            }
        }
    }
}