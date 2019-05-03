package com.sparta.euphoria.Activities.Questionnaires

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Questionnaires.Option
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaire
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaires
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.Section
import com.sparta.euphoria.R

class QuestionnaireActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mQuestionTextView: TextView? = null
    private var mPreviousButton: ImageButton? = null
    private var mNextButton: ImageButton? = null
    private var mToolbar: RelativeLayout? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var question: Questionnaire
    private var optionAdapter: QuestionnaireAdapter? = null

    var currentIndex = 0
    var questionsList: List<Questionnaire> = emptyList()
    var sections = ArrayList<Section>()
    var questionnaireIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
        setTitle("QUESTIONNAIRES")

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
            questionnaireIndex = questionnaire.index
            Thread() {
                questionsList = DataBaseHelper.getDatabase(applicationContext).questionnaireDao()
                    .getQuestionnaireList(questionnaire.uid)
                showQuestionnaire()
            }.start()
        }
    }

    fun getColorId(colorIndex: Int): Int {
        when (colorIndex) {
            1 -> return Color.RED
            2 -> return Color.YELLOW
            3 -> return Color.WHITE
            4 -> return Color.BLACK
            else -> return Color.GREEN
        }
    }

    fun showQuestionnaire() {
        question = questionsList[currentIndex]

        runOnUiThread {

            if (questionnaireIndex == 1) {
                val text = "● " + question.question
                val ss = SpannableString(text)
                val fcsColor = ForegroundColorSpan(getColorId(question.colorIndex))
                val fcsWhite = ForegroundColorSpan(Color.WHITE)
                ss.setSpan(fcsColor, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                ss.setSpan(fcsWhite, 2, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                mQuestionTextView?.text = ss
            }
            else {
                mQuestionTextView?.text = question.question
            }

            if (currentIndex == 0) {
                mPreviousButton?.alpha = 0.5f
                mPreviousButton?.isEnabled = false
                mNextButton?.isEnabled = true
            } else if (currentIndex >= (questionsList.size - 1)) {
                mNextButton?.isEnabled = false
                mPreviousButton?.isEnabled = true
                mNextButton?.alpha = 0.5f
            } else {
                mNextButton?.alpha = 1.0f
                mPreviousButton?.alpha = 1.0f
                mNextButton?.isEnabled = true
                mPreviousButton?.isEnabled = true
            }
        }
        setSections()
    }

    fun didClickPrevious(view: View) {
        currentIndex -= 1
        showQuestionnaire()
    }

    fun didClickNext(view: View) {
        currentIndex += 1
        showQuestionnaire()
    }

    fun setSections() {
        Thread() {
            val options = DataBaseHelper.getDatabase(applicationContext).optionDao().getOptions(question.uid)
            sections.clear()

            for (i in 0..(options.size - 1)) {
                val obj = options[i]
                val section = Section(obj, 0 , i)
                sections.add(section)
            }

        }.start()
    }

    private class QuestionnaireAdapter(var list: List<Section>, var answer: String?, var detail: String?, var activity: QuestionnaireActivity):
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
//            if (p0 is OptionHolder) {
//                p0.answer = answer
//            }
//            else if (p0 is OtherHolder) {
//                p0.detail = detail
//            }
//
//            val section = list[p1]
//            p0.bindData(section)
//
//            if (section.section == 0) {
//                p0.itemView.setOnClickListener { v: View? ->
//                    listener.onItemClick(v, p1)
//                }
//            }
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
//                    activity.updateDetails(mOtherEditText?.text.toString())
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