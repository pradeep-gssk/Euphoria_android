package com.sparta.euphoria.Model

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

class EUEditText: AppCompatEditText {

    constructor(context: Context):
            super(context) {
        setListener()
    }

    constructor(context: Context, attrs: AttributeSet):
            super(context, attrs) {
        setListener()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int):
            super(context, attrs, defStyle) {
        setListener()
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }

        return super.onKeyPreIme(keyCode, event)
    }

    fun setListener() {
        this.setOnEditorActionListener { v, actionId, event ->
            if(actionId== EditorInfo.IME_ACTION_DONE){
                clearFocus()
                val iMgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                iMgr.hideSoftInputFromWindow(windowToken, 0)
            }
            return@setOnEditorActionListener false
        }
    }
}