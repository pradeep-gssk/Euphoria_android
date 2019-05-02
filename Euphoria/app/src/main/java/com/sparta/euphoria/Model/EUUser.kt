package com.sparta.euphoria.Model

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.sparta.euphoria.Generic.JSONConvertable
import com.sparta.euphoria.Generic.toObject

data class EUUser (
    @SerializedName("CustomerName")
    var firstName: String = "",
    @SerializedName("CustomerSurname")
    var lastName: String = "",
    @SerializedName("Address")
    var address: String = "",
    @SerializedName("Town")
    var town: String = "",
    @SerializedName("Country")
    var country: String = "",
    @SerializedName("ZipCode")
    var zipcode: String = "",
    @SerializedName("Email")
    var email: String = "",
    @SerializedName("Phone")
    var phone: String = "",
    @SerializedName("CustomerId")
    var customerId: Int = 0
) : JSONConvertable {
    companion object {
        @Volatile
        private var INSTANCE: EUUser? = null

        fun shared(context: Context): EUUser {
            val tempInstance = INSTANCE
            return INSTANCE ?: synchronized(this) {
                val instance = EUUser()
                INSTANCE = instance
                return instance
            }
        }

        fun saveUser(json: String) {
            INSTANCE = json.toObject()
        }
    }
}

//import android.content.Context
//
//class EUUser {
//
//    companion object {
//        @Volatile
//        private var INSTANCE: EUUser? = null
//
//        fun shared(context: Context): EUUser {
//            val tempInstance = INSTANCE
//            return INSTANCE ?: synchronized(this) {
//                val instance = EUUser()
//                INSTANCE = instance
//                return instance
//            }
//        }
//    }
//
//    private var mFirstName: String? = null
//    private var mLastName: String? = null
//    private var mAddress: String? = null
//    private var mTown: String? = null
//    private var mCountry: String? = null
//    private var mZipcode: String? = null
//    private var mEmail: String? = null
//    private var mPhone: String? = null
//    private var mCustomerId: Int? = null
//
//    val firstName: String?
//        get() {
//            return mFirstName
//        }
//
//    val lastName: String?
//        get() {
//            return mLastName
//        }
//
//    val address: String?
//        get() {
//            return mAddress
//        }
//
//    val town: String?
//        get() {
//            return mTown
//        }
//
//    val country: String?
//        get() {
//            return mCountry
//        }
//
//    val zipcode: String?
//        get() {
//            return mZipcode
//        }
//
//    val email: String?
//        get() {
//            return mEmail
//        }
//
//    val phone: String?
//        get() {
//            return mPhone
//        }
//
//    val customerId: Int?
//        get() {
//            return mCustomerId
//        }
//}