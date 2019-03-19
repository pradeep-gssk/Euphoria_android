package com.example.euphoria.Activities.Exercises

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
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.Enums.Element
import com.example.euphoria.Enums.ExerciseType
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.R
import java.io.Serializable

class ExerciseActivity: AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var exerciseAdapter: ExerciseAdapter? = null

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
            items = db.exercisesDao().getUniqueExercisesForElement("Earth")
            updateViews()
        }.start()
    }

    fun updateViews() {
        runOnUiThread {
            if (exerciseAdapter == null) {
                exerciseAdapter = ExerciseAdapter(items)
                recyclerView.adapter = exerciseAdapter

                exerciseAdapter?.setOnItemClickListener(object : OnItemClickListener {
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
            bundle.putSerializable("exercise", item as Serializable)
            bundle.putSerializable("element", element as Serializable)

            val intent = Intent(this, ExerciseIndexActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }
}

private class ExerciseAdapter(private val list: List<String>): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

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

        fun bind(exercise: ExerciseType) {
            mCharacterTextView?.text = exercise.character
            mTitleTextView?.text = exercise.title
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
        val item = ExerciseType.getExerciseType(list[p1])
        if (item != null) {
            p0.bind(item)
        }

        p0.itemView.setOnClickListener { v: View? ->
            listener.onItemClick(v, p1)
        }
    }
}