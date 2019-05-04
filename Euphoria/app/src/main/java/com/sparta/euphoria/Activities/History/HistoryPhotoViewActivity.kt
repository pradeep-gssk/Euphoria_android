package com.sparta.euphoria.Activities.History

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.History
import com.sparta.euphoria.Enums.HistoryType
import com.sparta.euphoria.Generic.Constants
import com.sparta.euphoria.Generic.Constants.Companion.IMAGE_DIRECTORY
import com.sparta.euphoria.Model.EUHistory
import com.sparta.euphoria.R
import java.io.File

class HistoryPhotoViewActivity: AppCompatActivity() {

    var externalStoragePermissionEnabled: Boolean = false
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

    private var mTitleTextView: TextView? = null
    private var mEmailButton: Button? = null
    private var mDeleteButton: ImageButton? = null
    private var mPhotoView: ImageView? = null
    private var mCameraButton: ImageButton? = null
    private var historyType = HistoryType.face
    private var history: EUHistory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historyphoto)
        setTitle("HISTORY")

        mTitleTextView = findViewById(R.id.titleTextView)
        mEmailButton = findViewById(R.id.saveButton)
        mDeleteButton = findViewById(R.id.delete)
        mPhotoView = findViewById(R.id.photoView)
        mCameraButton = findViewById(R.id.cameraButton)
        mCameraButton?.visibility = View.INVISIBLE
        mEmailButton?.setBackgroundResource(R.mipmap.envelope_blue)
        mEmailButton?.text = ""
        mPhotoView?.setImageResource(R.mipmap.ic_launcher)
        mTitleTextView?.textSize = 15.toFloat()

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            historyType = bundle.get("historyType") as HistoryType
            history = bundle.get("history") as EUHistory
            mTitleTextView?.text = history?.title
            loadImage(history?.history)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!externalStoragePermissionEnabled) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION
            )
        }
    }

    fun loadImage(history: History?) {
        val fileName = history?.fileName
        if (fileName is String) {
            val imagePath = getDirectoryForFileName(fileName)
            mPhotoView?.setImageBitmap(BitmapFactory.decodeFile(imagePath.toString()))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION -> {
                mEmailButton?.isEnabled = externalStoragePermissionEnabled
                mDeleteButton?.isEnabled = externalStoragePermissionEnabled

                if (externalStoragePermissionEnabled) {
                    loadImage(history?.history)
                }
            }
        }
    }

    fun getDirectory(): File {
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        return wallpaperDirectory
    }

    fun getDirectoryForFileName(fileName: String): File {
        return File(getDirectory(), fileName)
    }

    fun didClickButton(view: View) {
        val entity = history?.history
        if (externalStoragePermissionEnabled && entity is History) {
            val intent = Intent(Intent.ACTION_SENDTO)
            val recipient = "mailto:" + Constants.EMAIL_RECIPIENT
            intent.data = Uri.parse(recipient)
            intent.putExtra(Intent.EXTRA_SUBJECT, historyType.emailSubject)
            intent.putExtra(Intent.EXTRA_TEXT, historyType.emailMessage)

            val filePath = File(getDirectory(), entity.fileName)
            val uri = Uri.fromFile(filePath)
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    fun didClickDelete(view: View) {
        val entity = history?.history
        if (entity is History) {
            deleteFromDataBase(entity)
        }
    }

    fun deleteFromDataBase(entity: History) {
        Thread() {
            DataBaseHelper.getDatabase(applicationContext).historyDao().delete(entity)
            deleteFromDirectory(entity)
        }.start()
    }

    fun deleteFromDirectory(entity: History) {
        runOnUiThread {
            val fileName = entity.fileName

            if (fileName is String) {
                val file = getDirectory()
                val filePath = File(file, fileName)
            }

            val file = getDirectory()
            val filePath = File(file, "1552851391450.jpg")
            if (filePath.exists()) {
                if (filePath.delete()) {
                }
                else {
                    //TODO: SHOW ERROR ALERT
                }
            }

            finish()
        }
    }
}