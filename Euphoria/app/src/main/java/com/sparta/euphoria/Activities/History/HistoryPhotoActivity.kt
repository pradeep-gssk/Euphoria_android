package com.sparta.euphoria.Activities.History

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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.DataBase.History
import com.sparta.euphoria.Enums.HistoryType
import com.sparta.euphoria.Extensions.gotoHome
import com.sparta.euphoria.Generic.Constants.Companion.CAMERA
import com.sparta.euphoria.Generic.Constants.Companion.GALLERY
import com.sparta.euphoria.Generic.Constants.Companion.IMAGE_DIRECTORY
import com.sparta.euphoria.Generic.Constants.Companion.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R
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

    val externalStoragePermissionEnabled: Boolean
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

    val cameraPermissionEnabled: Boolean
        get() {
            return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historyphoto)
        setTitle("HISTORY")

        mTitleTextView = findViewById(R.id.titleTextView)
        mSaveButton = findViewById(R.id.saveButton)
        mDeleteButton = findViewById(R.id.delete)
        mPhotoView = findViewById(R.id.photoView)
        mCameraButton = findViewById(R.id.cameraButton)

        mSaveButton?.visibility = View.INVISIBLE
        mDeleteButton?.visibility = View.INVISIBLE
        mPhotoView?.setImageResource(R.mipmap.ic_launcher)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            historyType = bundle.get("historyType") as HistoryType
            mTitleTextView?.text = historyType.title
            mPhotoView?.setImageResource(historyType.placeholder)
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

        if (resultCode == 0) {
            return
        }

        if (data == null) {
            //TODO: Show error
        }

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
                    //TODO: Show error
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
        mSaveButton?.visibility = View.INVISIBLE
        mDeleteButton?.visibility = View.INVISIBLE
        mCameraButton?.visibility = View.VISIBLE
        mPhotoView?.setImageResource(historyType.placeholder)
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
            mSaveButton?.visibility = View.VISIBLE
            mDeleteButton?.visibility = View.VISIBLE
            mCameraButton?.visibility = View.INVISIBLE
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

            val customerId = EUUser.shared(this).customerId
            val history = History(customerId, date, imageName, historyType.image)
            println(history)
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