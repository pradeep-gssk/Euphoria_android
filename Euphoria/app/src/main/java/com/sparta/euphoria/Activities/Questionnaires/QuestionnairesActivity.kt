package com.sparta.euphoria.Activities.Questionnaires

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
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
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaire
import com.sparta.euphoria.DataBase.Questionnaires.Questionnaires
import com.sparta.euphoria.Generic.Constants
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R
import java.io.Serializable

class QuestionnairesActivity : AppCompatActivity() {

    var externalStoragePermissionEnabled: Boolean = false
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var questionnairesAdapter: QuestionnaireAdapter? = null
    private var speedDial: SpeedDialView? = null

    var items: List<Questionnaires> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("QUESTIONNAIRES")

        speedDial = findViewById(R.id.speedDial)
        speedDial?.visibility = VISIBLE

        speedDial?.addAllActionItems(
            listOf(
                SpeedDialActionItem.Builder(R.id.email, R.mipmap.envelope_blue).setLabel("Email").create(),
                SpeedDialActionItem.Builder(R.id.refresh, R.mipmap.refresh).setLabel("Refresh").create(),
                SpeedDialActionItem.Builder(R.id.text, R.mipmap.ic_launcher).setLabel("Text").create())
        )

        speedDial?.setOnActionSelectedListener {item ->
            when(item.id) {
                R.id.email -> {
                    Thread {
                        val customerId = EUUser.shared(this).customerId
                        val joined = DataBaseHelper.getDatabase(this).fetchAnsweredQuestionnaires(customerId)
                        didTapEmail(joined)
                    }.start()
                }

                R.id.refresh -> {
                    println("two")
                }

                R.id.text -> {
                    println("text")
                }
            }
            return@setOnActionSelectedListener false
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

    fun didTapEmail(text: String) {
        runOnUiThread {
            if (externalStoragePermissionEnabled) {
                val intent = Intent(Intent.ACTION_SENDTO)
                val recipient = "mailto:" + Constants.EMAIL_RECIPIENT
                intent.data = Uri.parse(recipient)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Questionnaires")
                intent.putExtra(Intent.EXTRA_TEXT, "Questions and Answers")
//                val bytes = text.toByteArray(Charsets.UTF_8)
//                intent.putExtra(Intent.EXTRA_STREAM, bytes)

                if (intent.resolveActivity(this.packageManager) != null) {
                    startActivity(intent)
                }
            }
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