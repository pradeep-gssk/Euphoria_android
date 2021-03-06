package com.example.euphoria.Activities.Exercises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.Exercises
import com.example.euphoria.Enums.Element
import com.example.euphoria.Enums.ExerciseType
import com.example.euphoria.Generic.OnItemClickListener
import com.example.euphoria.Generic.ViewHolder
import com.example.euphoria.R
import kotlinx.android.synthetic.main.activity_videoindex.*
import kotlinx.android.synthetic.main.grid_adapter.view.*
import java.io.Serializable

class ExerciseIndexActivity: AppCompatActivity() {
    private var mTitleTextView: TextView? = null
    private var adapter: ExerciseAdapter? = null
    var exerciseGridView: GridView? = null

    var exercises: List<Exercises> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_videoindex)
        mTitleTextView = findViewById(R.id.titleView)
        exerciseGridView = findViewById(R.id.gridView)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val exerciseString = bundle.get("exercise") as String
            val element = bundle.get("element") as Element
            val exercise = ExerciseType.getExerciseType(exerciseString)
            mTitleTextView?.text = exercise?.title

            Thread(){
                exercises = DataBaseHelper.getDatabase(applicationContext).exercisesDao().getExercisesForElementAndType(element.element, exerciseString)
                loadViews(exercises)
            }.start()
        }

        gridView.setOnItemClickListener { parent, view, position, id ->
            val exercise = exercises[position]
            gotoNextActivity(exercise)
        }
    }

    fun loadViews(exercises: List<Exercises>) {
        runOnUiThread{
            adapter = ExerciseAdapter(
                this,
                exercises
            )
            exerciseGridView?.adapter = adapter
        }
    }

    fun gotoNextActivity(exercise: Exercises) {
        runOnUiThread{
            val bundle = Bundle()
            bundle.putSerializable("exercise", exercise as Serializable)

            val intent = Intent(this, ExerciseViewActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private class ExerciseAdapter(var context: Context, var list: List<Exercises>): BaseAdapter() {

        lateinit var listener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val exercise = list[position]
            val cellView = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.grid_adapter, parent, false)
            val holder = cellView.tag as? ViewHolder ?: ViewHolder(cellView)
            cellView.tag = holder
            cellView.videoName.text = exercise.videoName
            return cellView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}