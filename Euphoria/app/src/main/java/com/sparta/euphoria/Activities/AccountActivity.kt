package com.sparta.euphoria.Activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sparta.euphoria.Extensions.signout
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Generic.Constants
import com.sparta.euphoria.Generic.PREFS_FILENAME
import com.sparta.euphoria.Generic.USER_DATA
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AccountActivity: AppCompatActivity() {

    private var profileButton: ImageButton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var accountAdapter: AccountAdapter? = null

    private var values = arrayListOf<String>()

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

    val prefs: SharedPreferences by lazy {
        this.getSharedPreferences(PREFS_FILENAME, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setTitle("SETTINGS")

        val user = EUUser.shared(this)
        values.add(user.firstName)
        values.add(user.lastName)
        values.add(user.phone)
        values.add(user.email)

        profileButton = findViewById(R.id.profileButton)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        if (accountAdapter == null) {
            accountAdapter = AccountAdapter(values)
            recyclerView.adapter = accountAdapter
        }

        loadImage()
    }

    override fun onStart() {
        super.onStart()
        if (!externalStoragePermissionEnabled) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA),
                Constants.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION -> {
                profileButton?.isEnabled = externalStoragePermissionEnabled
                loadImage()
            }
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
            Constants.GALLERY -> {
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
            Constants.CAMERA -> {
                val bitmap = data?.extras?.get("data") as? Bitmap
                setImage(bitmap)
            }
        }
    }

    private fun loadImage() {
        val customerId = EUUser.shared(this).customerId
        val imagePath = getDirectoryForFileName(customerId.toString()+".jpg")
        if (imagePath.exists() && externalStoragePermissionEnabled) {
            val mBitmap = BitmapFactory.decodeFile(imagePath.toString())
            val cornerRadius = 77.5f
            val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, mBitmap)
            roundedBitmapDrawable.cornerRadius = cornerRadius
            roundedBitmapDrawable.isCircular = true
            roundedBitmapDrawable.setAntiAlias(true)
            profileButton?.setScaleType(ImageView.ScaleType.FIT_XY)
            profileButton?.setImageDrawable(roundedBitmapDrawable)
        }
    }

    private fun setImage(bitmap: Bitmap?) {
        if (bitmap is Bitmap) {
            profileButton?.setImageBitmap(bitmap)
            saveImage(bitmap)
        }
        else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show()
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

    private fun saveImage(bitmap: Bitmap) {
        val customerId = EUUser.shared(this).customerId
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val imagesDirectory = getDirectory()
        val imageName = customerId.toString()+".jpg"

        try {
            val file = File(imagesDirectory, imageName)
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(file.getPath()), arrayOf("image/jpeg"), null)
            outputStream.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show()
        }
    }

    fun didClickSignOutButton(view: View) {
        prefs.edit().remove("USER_DATA").apply()
        signout()
    }

    fun didTapProfileButton(view: View) {
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
        startActivityForResult(galleryIntent, Constants.GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constants.CAMERA)
    }

    private class AccountAdapter(var values: List<String>): RecyclerView.Adapter<BaseViewHolder<String>>() {

        private val titles = listOf<String>("First name:", "Last Name:", "Phone:", "E-mail:")

        override fun onBindViewHolder(p0: BaseViewHolder<String>, p1: Int) {
            p0.bindData(values[p1])
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<String> {
            return AccountHolder(p0, R.layout.account_adapter, titles[p1])
        }

        override fun getItemCount(): Int {
            return values.size
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    private class AccountHolder(parent: ViewGroup, layoutID: Int, var title: String):
        BaseViewHolder<String>(parent, layoutID) {

        private var mTitleTextView: TextView? = null
        private var mDetailTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            mDetailTextView = itemView.findViewById(R.id.detailTextView)
        }

        override fun bindData(model: String) {
            mTitleTextView?.text = title
            mDetailTextView?.text = model
        }
   }
}