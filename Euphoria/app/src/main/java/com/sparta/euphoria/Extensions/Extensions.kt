package com.sparta.euphoria.Extensions

import android.app.Application
import org.json.JSONArray
import org.json.JSONObject

fun Any.intValue(): Int {
    return if (this is Int) this else 0
}

fun Any.stringValue(): String {
    return if (this is String) this else ""
}

fun String?.stringValue(): String {
    return if (this is String) this else ""
}

fun JSONObject.stringValue(key: String): String {
    return if (this.has(key)) this[key].stringValue() else ""
}

fun JSONObject.intValue(key: String): Int {
    return if (this.has(key)) this[key].intValue() else 0
}

fun String.getJson(application: Application): JSONObject {
    val json_string = application.assets.open(this).bufferedReader().use{
        it.readText()
    }
    return JSONObject(json_string)
}

fun String.getJsonArray(application: Application): JSONArray {
    val json_string = application.assets.open(this).bufferedReader().use{
        it.readText()
    }
    return JSONArray(json_string)
}

fun  String?.boolValue(): Boolean {
    if (this is String && this == "Yes") {
        return true
    }

    return false
}