package com.sparta.euphoria.Networking

import android.content.Context
import com.android.volley.Request

sealed class ApiRoute {
    val timeOut: Int
        get() {
            return 3000
        }

    val baseUrl: String
        get() {
            return "http://webapi.spasoftware.gr/api"
        }

    data class Login(var email: String, var password: String, var ctx: Context) : ApiRoute()
    data class Activities(var customerId: Int, var ctx: Context) : ApiRoute()

    val httpMethod: Int
        get() {
            return when (this) {
                is Login -> Request.Method.GET
                is Activities -> Request.Method.GET
            }
        }

    val parameters: HashMap<String, String>
        get() {
            return when (this) {
                is Login -> hashMapOf()
                is Activities -> hashMapOf()
            }
        }

    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            map["Content-Type"] = "application/json"
            map["Authorization"] = "Basic Y2tjOjEyMzQ="
            return  map
        }

    val url: String
        get() {
            return "$baseUrl/${when (this@ApiRoute) {
                is Login -> "customers/" + this.email + "/" + this.password
                is Activities -> "appointment/custommerappointments/" + this.customerId
            }}"
        }
}