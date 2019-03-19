package com.example.euphoria.Activities.History

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.*
import android.widget.*
import com.example.euphoria.DataBase.DataBaseHelper
import com.example.euphoria.DataBase.History
import com.example.euphoria.Enums.HistoryType
import com.example.euphoria.Generic.Constants.Companion.CAMERA
import com.example.euphoria.Generic.Constants.Companion.GALLERY
import com.example.euphoria.Generic.Constants.Companion.IMAGE_DIRECTORY
import com.example.euphoria.Generic.Constants.Companion.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION
import com.example.euphoria.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class HistoryPhotoActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mSaveButton: Button? = null
    private var mDeleteButton: ImageButton? = null
    private var mPhotoView: ImageView? = null
    private var mCameraButton: ImageButton? = null
    private var historyType = HistoryType.face
    private var selectedBitmap: Bitmap? = null

    var externalStoragePermissionEnabled: Boolean = false
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

    var cameraPermissionEnabled: Boolean = false
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historyphoto)

        mTitleTextView = findViewById(R.id.titleTextView)
        mSaveButton = findViewById(R.id.saveButton)
        mDeleteButton = findViewById(R.id.delete)
        mPhotoView = findViewById(R.id.photoView)
        mCameraButton = findViewById(R.id.cameraButton)

        mSaveButton?.visibility = INVISIBLE
        mDeleteButton?.visibility = INVISIBLE
        mPhotoView?.setImageResource(R.mipmap.ic_launcher)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            historyType = bundle.get("historyType") as HistoryType
            mTitleTextView?.text = historyType.title
        }
    }

    override fun onStart() {
        super.onStart()
        if (!externalStoragePermissionEnabled) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA),
                WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION ->
                mCameraButton?.isEnabled = externalStoragePermissionEnabled
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            GALLERY -> {
                val contentURI = data?.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    setImage(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA -> {
                val bitmap = data?.extras?.get("data") as? Bitmap
                setImage(bitmap)
            }
        }
    }

    fun didClickButton(view: View) {
        val bitmap = selectedBitmap
        if (bitmap is Bitmap) {
            saveImage(bitmap)
        }
    }

    fun didClickDelete(view: View) {
        mSaveButton?.visibility = INVISIBLE
        mDeleteButton?.visibility = INVISIBLE
        mCameraButton?.visibility = VISIBLE
        mPhotoView?.setImageResource(R.mipmap.ic_launcher)
    }

    fun didClickCamera(view: View) {

        if (!cameraPermissionEnabled && !externalStoragePermissionEnabled) {
            //TODO: show error alert
        }

        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Choose Image")
        val pictureDialogItems = arrayOf("Gallery", "Camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun getDirectory(): File {
        val imagesDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdirs()
        }
        return imagesDirectory
    }

    private fun setImage(bitmap: Bitmap?) {
        if (bitmap is Bitmap) {
            mPhotoView?.setImageBitmap(bitmap)
            selectedBitmap = bitmap
            mSaveButton?.visibility = VISIBLE
            mDeleteButton?.visibility = VISIBLE
            mCameraButton?.visibility = INVISIBLE
        }
        else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val imagesDirectory = getDirectory()
        val date: Long = Calendar.getInstance().timeInMillis
        val imageName = historyType.image + "-" + date.toString()+".jpg"

        try {
            val file = File(imagesDirectory, imageName)
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(file.getPath()), arrayOf("image/jpeg"), null)
            outputStream.close()

            val history = History(date, imageName, historyType.image)
            Thread(){
                DataBaseHelper.getDatabase(applicationContext).historyDao().insert(history)
                navigateBack()
            }.start()
        }
        catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateBack() {
        runOnUiThread {
            finish()
        }
    }
}