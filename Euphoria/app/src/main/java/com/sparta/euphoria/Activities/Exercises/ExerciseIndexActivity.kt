package com.sparta.euphoria.Activities.Exercises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.Exercises
import com.sparta.euphoria.Enums.Element
import com.sparta.euphoria.Enums.ExerciseType
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Generic.ViewHolder
import com.sparta.euphoria.R
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

        exerciseGridView?.setOnItemClickListener { parent, view, position, id ->
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
            val resId = context.resources.getIdentifier(exercise.thumbnail, "mipmap", context.packageName)
            cellView.gridImageBg.setImageResource(resId)
            cellView.tag = holder
            cellView.name.text = exercise.videoName
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