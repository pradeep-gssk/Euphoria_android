package com.sparta.euphoria.Extensions

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.sparta.euphoria.DataBase.DataBaseHelper
import com.sparta.euphoria.Enums.Element

fun AppCompatActivity.findElement(ctx: Context, questionnaireId: Long): Element {
    var selectedElement: Element = Element.Earth
    var previousValue = 0
    Element.values().forEach { type ->
        val value = DataBaseHelper.getDatabase(ctx).getElementCount(type.element, questionnaireId)
        if (previousValue < value) {
            previousValue = value
            selectedElement = type
        }
    }

    return selectedElement
}