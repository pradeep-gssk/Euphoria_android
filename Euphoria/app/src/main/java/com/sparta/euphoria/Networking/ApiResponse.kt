package com.sparta.euphoria.Networking

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class ApiResponse(response: String) {
    var success: Boolean = false
    var message: String = "An error occurred while processing the response"
    var json: String = ""
    var jsonArray: JSONArray = JSONArray()

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            if (jsonToken is JSONObject) {
                val jsonRsponse = JSONObject(response)
                if (jsonRsponse.toString().length > 0) {
                    json = jsonRsponse.toString()
                    success = true
                } else {
                    success = false
                }
            }
            else if (jsonToken is JSONArray) {
                val jsonRsponse = JSONArray(response)
                if (jsonRsponse.toString().length > 0) {
                    jsonArray = jsonRsponse
                    success = true
                } else {
                    success = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}