package com.sparta.euphoria.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.Generic.PREFS_FILENAME
import com.sparta.euphoria.Generic.PRELOAD_DATA
import com.sparta.euphoria.Generic.USER_DATA
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.Networking.ApiClient
import com.sparta.euphoria.R

class MainActivity : AppCompatActivity() {

    private var mEmailEditText: EditText? = null
    private var mPasswordEditText: EditText? = null

    val prefs: SharedPreferences by lazy {
        this.getSharedPreferences(PREFS_FILENAME, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEmailEditText = findViewById(R.id.emailText)
        mPasswordEditText = findViewById(R.id.passwordText)

        val json = prefs.getString(USER_DATA, null)

        if (json != null) {
            EUUser.saveUser(json)
            gotoHomeActivity()
        }
    }

    fun didClickForgotPassword(view: View) {
    }

    fun didClickSignIn(view: View) {
        val email = mEmailEditText?.text.toString()
        val password = mPasswordEditText?.text.toString()

        if (email.length <= 0) return
        if (password.length <= 0) return

        //TODO: Show loading indicator

        ApiClient(this).loginUser(email, password, success = { json ->
            val prefsEditor = prefs.edit()
            prefsEditor.putString(USER_DATA, json)
            prefsEditor.apply()
            saveUserAndloadData()
            gotoHomeActivity()
        }, failure = { message ->
            //TODO: Show error
        })
    }

    fun saveUserAndloadData() {
        val customerId = EUUser.shared(this).customerId
        Thread() {
            val result = DataBaseHelper.getDatabase(this).checkIfUserExist(customerId)
            if (result == true) return@Thread
            DataBaseHelper.getDatabase(this).saveUser(customerId)
            preloadData(customerId)
        }.start()
    }

    fun gotoHomeActivity() {
        val intent = Intent(this, HomeActivity:: class.java)
        startActivity(intent)
    }

    fun preloadData(customerId: Int) {
        val dataLoaded = prefs.getBoolean(PRELOAD_DATA, false)
        if (!dataLoaded) {
            val db = DataBaseHelper.getDatabase(applicationContext)
            db.preloadData(application, customerId)
            val prefsEditor = prefs.edit()
            prefsEditor.putBoolean(PRELOAD_DATA, true)
            prefsEditor.apply()
        }
    }
}
