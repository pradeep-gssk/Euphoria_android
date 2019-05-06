package com.sparta.euphoria.Activities.Questionnaires

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Questionnaires.Option
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaire
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaires
import com.sparta.euphoria.Enums.Element
import com.sparta.euphoria.Enums.OptionType
import com.sparta.euphoria.Enums.SelectionType
import com.sparta.euphoria.Extensions.boolValue
import com.sparta.euphoria.Extensions.findElement
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Generic.OnQuestionnaireItemClickListener
import com.sparta.euphoria.Model.EUUser
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
    var selectedElement: Element? = null
    var allQuestionsAnswered: Boolean = false
    private var optionAdapter: QuestionnaireAdapter? = null

    var currentIndex = 0
    var questionsList: List<Questionnaire> = emptyList()
    var sections = ArrayList<Section>()
    private lateinit var questionnaires: Questionnaires

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
            questionnaires = bundle.get("questionnaire") as Questionnaires
            mTitleTextView?.text = questionnaires.title
            Thread() {
                initializeValues()
                questionsList = DataBaseHelper.getDatabase(applicationContext).questionnaireDao()
                    .getQuestionnaireList(questionnaires.uid)
                loadQuestionnaire()
            }.start()
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

    fun initializeValues() {
        val customerId = EUUser.shared(this).customerId
        if (questionnaires.index == 1 &&
            DataBaseHelper.getDatabase(this).checkIfAllAnswered(questionnaires.index, customerId)) {
            selectedElement = findElement(this, questionnaires.uid)
        }
        else if (questionnaires.index != 1) {
            allQuestionsAnswered = DataBaseHelper.getDatabase(this).checkIfAllAnswered(questionnaires.index, customerId)
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

    fun loadQuestionnaire() {
        question = questionsList[currentIndex]
        runOnUiThread {
            if (questionnaires.index == 1) {
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
        loadQuestionnaire()
    }

    fun didClickNext(view: View) {
        currentIndex += 1
        loadQuestionnaire()
    }

    fun setSections() {
        Thread() {
            val options = DataBaseHelper.getDatabase(applicationContext).optionDao().getOptions(question.uid)
            sections.clear()

            val answer = question.answer
            var dictionary: Map<String, String> = emptyMap()
            val selectionType = SelectionType.getSelectionType(question.selectionType)
            if (selectionType == SelectionType.multiple &&
                answer != null) {
                val answers = answer.split(", ")
                dictionary = answers.associate { it to "0" }
            }

            for (i in 0..(options.size - 1)) {
                val obj = options[i]
                val section = Section(obj, 0 , i, dictionary)
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
           loadAdapterWithSections(selectionType)
        }.start()
    }

    fun loadAdapterWithSections(selectionType: SelectionType?) {
        runOnUiThread {
            if (optionAdapter == null) {
                optionAdapter = QuestionnaireAdapter(
                    sections,
                    question.answer,
                    question.details,
                    selectionType
                )
                recyclerView.adapter = optionAdapter
            }
            else {
                optionAdapter?.list = sections
                optionAdapter?.answer = question.answer
                optionAdapter?.detail = question.details
                optionAdapter?.notifyDataSetChanged()
                checkIfAllAnswered()
            }

            optionAdapter?.setOnQuestionnaireItemClickListener(object : OnQuestionnaireItemClickListener {
                override fun toolBarVisibility(hasFocus: Boolean) {
                    mToolbar?.visibility = if (hasFocus) View.INVISIBLE else View.VISIBLE
                }

                override fun onItemClick(view: View?, position: Int) {
                    val section = sections[position]
                    val item = section.item
                    if (section.section == 0 && (item is Option)) {
                        val answer = item.option
                        if (SelectionType.getSelectionType(question.selectionType) == SelectionType.multiple && answer != null) {
                            val answers = section.answers.keys.toMutableList()

                            if (answers.contains(answer)) {
                                answers.remove(answer)
                            }
                            else {
                                answers.add(answer)
                            }

                            val result = answers.joinToString(separator = ", ")
                            updateAnswer(result)
                        }
                        else {
                            updateAnswer(item.option)
                        }
                    }
                }

                override fun updateDetails(details: String) {
                    this@QuestionnaireActivity.updateDetails(details)
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
                    if (SelectionType.getSelectionType(question.selectionType) == SelectionType.single) {
                        updateDetails(null)
                    }
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

    fun checkIfAllAnswered() {
        Thread {
            val customerId = EUUser.shared(this).customerId
             if (DataBaseHelper.getDatabase(this).checkIfAllAnswered(questionnaires.index, customerId)) {
                 when(questionnaires.index) {
                     1 -> showElementAlert()
                     else -> {
                         if (allQuestionsAnswered == false) {
                             //TODO: Show completing alert
                         }
                     }
                 }
             }
        }.start()
    }

    fun showElementAlert() {
        val currentElement = findElement(this, questionnaires.uid)
        if (selectedElement != null && selectedElement != currentElement) {
            selectedElement = currentElement
            //TODO: Show element alert
        }
    }

    private class QuestionnaireAdapter(var list: List<Section>,
                                       var answer: String?,
                                       var detail: String?,
                                       var selectionType: SelectionType?):
        RecyclerView.Adapter<BaseViewHolder<Section>>() {

        lateinit var listener: OnQuestionnaireItemClickListener

        fun setOnQuestionnaireItemClickListener(listener: OnQuestionnaireItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<Section> {
            if (p1 == 1) {
                return OtherHolder(p0, R.layout.other_adapter, detail, listener)
            }

            return OptionHolder(p0, R.layout.option_adapter, answer, selectionType)
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
                               var answer: String?,
                               var selectionType: SelectionType?):
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
                val answers = model.answers

                if (selectionType != null && selectionType == SelectionType.multiple && answers.size > 0) {
                    if (answers.containsKey(item.option)) {
                        mAccessoryImage?.setImageResource(R.mipmap.checkmark_white)
                    }
                    else {
                        mAccessoryImage?.setImageResource(0)
                    }
                }
                else if (item.option == answer) {
                    mAccessoryImage?.setImageResource(R.mipmap.checkmark_white)
                }
                else {
                    mAccessoryImage?.setImageResource(0)
                }
            }
        }
    }

    private class OtherHolder(parent: ViewGroup, layoutID: Int,
                              var detail: String?,
                              listener: OnQuestionnaireItemClickListener):
        BaseViewHolder<Section>(parent, layoutID) {
        private var mOtherEditText: EditText? = null

        init {
            mOtherEditText = itemView.findViewById(R.id.otherEditText)
            mOtherEditText?.setOnFocusChangeListener { v, hasFocus ->
                listener.toolBarVisibility(hasFocus)
                if (!hasFocus) {
                    listener.updateDetails(mOtherEditText?.text.toString())
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