package com.sparta.euphoria.Activities

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.widget.Button
import com.sparta.euphoria.Enums.HTMLType
import com.sparta.euphoria.Generic.I_AGREE
import com.sparta.euphoria.Generic.PREFS_FILENAME
import com.sparta.euphoria.R

class ConcentActivity: AppCompatActivity() {

    private var webView: WebView? = null
    private var button: Button? = null

    private var htmlType: HTMLType = HTMLType.concent

    val prefs: SharedPreferences by lazy {
        this.getSharedPreferences(PREFS_FILENAME, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concent)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            htmlType = bundle.get("htmlType") as HTMLType
        }

        webView = findViewById(R.id.webview)
        button = findViewById(R.id.button)
        button?.setText(htmlType.button)
        webView?.loadUrl(htmlType.path)
    }

    fun didTapAgree(view: View) {
        if (htmlType == HTMLType.concent) {
            prefs.edit().putBoolean(I_AGREE, true).apply()
        }
        finish()
    }
}