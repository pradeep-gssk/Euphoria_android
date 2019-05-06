package com.sparta.euphoria.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.Enums.Element
import com.sparta.euphoria.Enums.HomeType
import com.sparta.euphoria.Extensions.findElement
import com.sparta.euphoria.Generic.Constants
import com.sparta.euphoria.Generic.OnItemClickListener
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var homeAdapter: HomeAdapter? = null
    private var questionnaire1Answered = false

    val items: Array<HomeType> by lazy {
        return@lazy HomeType.values()
    }

    val externalStoragePermissionEnabled: Boolean
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        setTitle("HOME")

        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, 1)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onResume() {
        super.onResume()
        getSelectedElement()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        val item = menu?.getItem(0)
        if (item != null && item.itemId == R.id.profile_button && externalStoragePermissionEnabled) {
            loadImage(item)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.profile_button -> {
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        moveTaskToBack(true);
    }

    private fun loadAdapter() {
        runOnUiThread {
            if (homeAdapter == null) {
                homeAdapter = HomeAdapter(items, questionnaire1Answered)
                recyclerView.adapter = homeAdapter

                homeAdapter?.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        gotoNextActivity(position)
                    }
                })
            }
            else {
                homeAdapter?.questionnaire1Answered = questionnaire1Answered
                homeAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun getSelectedElement() {
        val customerId = EUUser.shared(this).customerId
        Thread() {
            questionnaire1Answered = DataBaseHelper.getDatabase(this).checkIfAllAnswered(1, customerId)
            loadAdapter()
        }.start()
    }

    fun gotoNextActivity(position: Int) {
        val item = items[position]
        runOnUiThread {
            val intent = Intent(this, item.cls)
            startActivity(intent)
        }
    }


    private fun getDirectory(): File {
        val imagesDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + Constants.IMAGE_DIRECTORY
        )
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdirs()
        }
        return imagesDirectory
    }

    fun getDirectoryForFileName(fileName: String): File {
        return File(getDirectory(), fileName)
    }

    private fun loadImage(item: MenuItem) {
        val customerId = EUUser.shared(this).customerId
        val imagePath = getDirectoryForFileName(customerId.toString()+".jpg")
        if (imagePath.exists()) {
            val mBitmap = BitmapFactory.decodeFile(imagePath.toString())
            val cornerRadius = 20f
            val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, mBitmap)
            roundedBitmapDrawable.cornerRadius = cornerRadius
            roundedBitmapDrawable.isCircular = true
            roundedBitmapDrawable.setAntiAlias(true)
            item.setIcon(roundedBitmapDrawable)
        }
    }

    private class HomeAdapter(private val list: Array<HomeType>, var questionnaire1Answered: Boolean) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
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

            fun bind(homeType: HomeType) {
                mCharacterImage?.setImageResource(homeType.characterImage)
                mCharacterTextView?.text = homeType.character
                mTitleTextView?.text = homeType.title
                mAccessoryImage?.setImageResource(homeType.accessoryImage)
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
            p0.bind(list[p1])

            if (p1 == 1 || p1 == 2) {
                if (questionnaire1Answered == false) {
                    p0.itemView.alpha = 0.6F
                    p0.itemView.setBackgroundColor(Color.parseColor("#c7c8ca"))
                }
                else {
                    p0.itemView.alpha = 1.0F
                    p0.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            else {
                p0.itemView.alpha = 1.0F
                p0.itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            p0.itemView.setOnClickListener { v: View? ->
                if ((p1 == 1 || p1 == 2) && questionnaire1Answered == false) {
                    return@setOnClickListener
                }
                listener.onItemClick(v, p1)
            }
        }
    }
}